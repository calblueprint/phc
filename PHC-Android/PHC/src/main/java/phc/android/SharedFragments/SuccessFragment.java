package phc.android.SharedFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import phc.android.Checkin.CheckinActivity;
import phc.android.Checkin.CheckinFragment;
import phc.android.Checkout.CheckoutActivity;
import phc.android.Helpers.SharedPreferenceEditorListener;
import phc.android.R;
import phc.android.Services.ServicesActivity;

/**
 * SuccessFragment is launched on successful submission of a client's data,
 * and allows the user to go back to the beginning of their current flow.
 */
public class SuccessFragment extends CheckinFragment {

    // Different flows this success fragment can be in
    public static enum SuccessType {
        CHECKIN_SUCCESS, SERVICE_SUCCESS, CHECKOUT_SUCCESS};
    // Submit button
    private Button mRepeatActionButton;
    // The flow this success fragment is in
    private SuccessType mCurrentSuccessType;
    // Determines what text is shown
    private HashMap<SuccessType, String> mActionTextMapping;
    // Determines what button text is shown
    private HashMap<SuccessType, String> mButtonTextMapping;

    private static final String TAG = "SUCCESS_FRAGMENT";
    // Key for saving the success type across instance states
    private static final String SUCCES_TYPE_KEY = "SUCCESS_TYPE";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success, container, false);
        if (savedInstanceState != null) {
            mCurrentSuccessType = (SuccessType) savedInstanceState.getSerializable(SUCCES_TYPE_KEY);
        }
        setupStringMappings();
        setOnRepeatActionClickListener(view);
        setupUI(view);
        return view;
    }

    @Override
    public void onResume() {
        switch (mCurrentSuccessType) {
            case CHECKIN_SUCCESS:
                // TODO: Will delete/change once Warren removes the sidebar
                LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.checkin_sidebar);
                for (int i = 0; i < sidebarList.getChildCount(); i++) {
                    View v = sidebarList.getChildAt(i);
                    Object vTag = v.getTag();
                    if ((vTag != null) && (vTag.equals(getResources().getText(R.string.sidebar_confirm)))) {
                        TextView tv = (TextView) v;
                        tv.setTypeface(null, Typeface.BOLD);
                    } else if (v instanceof TextView) {
                        TextView tv = (TextView) v;
                        tv.setTypeface(null, Typeface.NORMAL);
                    }
                }
                break;
            case SERVICE_SUCCESS:
                break;
            case CHECKOUT_SUCCESS:
                break;

        }

        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(SUCCES_TYPE_KEY, mCurrentSuccessType);
    }

    /**
     * Should be called from the previous fragment to modify the success fragment based on what flow it is in
     */
    public void setType(SuccessType successType) {
        mCurrentSuccessType = successType;
    }

    /**
     * Setup mapping from success types to text in confirmation message and button
     */
    private void setupStringMappings(){
        mActionTextMapping = new HashMap<SuccessType, String>();
        mActionTextMapping.put(SuccessType.CHECKIN_SUCCESS, "checked in.");
        mActionTextMapping.put(SuccessType.SERVICE_SUCCESS, "checked in.");
        mActionTextMapping.put(SuccessType.CHECKOUT_SUCCESS, "checked out.");
        mButtonTextMapping = new HashMap<SuccessType, String>();
        mButtonTextMapping.put(SuccessType.CHECKIN_SUCCESS, "Check in another client");
        mButtonTextMapping.put(SuccessType.SERVICE_SUCCESS, "Check in another client");
        mButtonTextMapping.put(SuccessType.CHECKOUT_SUCCESS, "Check out another client");
    }

    /**
     * sets an OnClickListener for the repeat action button,
     * which calls a new instance of whatever activity this fragment is in.
     */
    protected void setOnRepeatActionClickListener(View view) {
        mRepeatActionButton = (Button) view.findViewById(R.id.button_repeat_action);
        switch (mCurrentSuccessType) {
            case CHECKIN_SUCCESS:
                mRepeatActionButton.setOnClickListener(new CheckInRepeatOnClickListener());
                break;
            case SERVICE_SUCCESS:
                mRepeatActionButton.setOnClickListener(new ServiceRepeatOnClickListener());
                break;
            case CHECKOUT_SUCCESS:
                mRepeatActionButton.setOnClickListener(new CheckOutRepeatOnClickListener());
                break;
            default:
                Log.e(TAG, "Did not set the success type using the setType() method");
                break;
        }
    }

    private class CheckInRepeatOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), CheckinActivity.class);
            getActivity().finish();
            getActivity().startActivity(intent);
        }
    }

    private class ServiceRepeatOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ServicesActivity.class);
            //pass currently selected service
            intent.putExtra("provided_service", ((ServicesActivity) getActivity())
                    .getServiceSelected());
            getActivity().finish();
            getActivity().startActivity(intent);
        }
    }

    private class CheckOutRepeatOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), CheckoutActivity.class);
            getActivity().finish();
            getActivity().startActivity(intent);
        }
    }

    /**
     * Grabs the person's name from SharedPreferences, and adds it after the success text
     */
    protected void setupUI(View view) {
        // Set up text that appears in textview
        TextView mSuccessText = (TextView) view.findViewById(R.id.text_success);
        SharedPreferences sharedPref = getActivity().getSharedPreferences
                (SharedPreferenceEditorListener.CLIENT_INFO_PREFS_NAME, Context.MODE_PRIVATE);
        String clientName = "";

        String firstName = sharedPref.getString("first_name", "");
        String lastName = sharedPref.getString("last_name", "");
        if (firstName.equals("") && lastName.equals("")){
            clientName = "The client";
        } else {
            if (!firstName.equals("")) clientName += firstName;
            if (!lastName.equals("")) clientName += " " + lastName;
        }

        mSuccessText.setText("Success! " + clientName + " is now " + mActionTextMapping.get
                (mCurrentSuccessType));

        // Set up text that appears in the button
        mRepeatActionButton.setText(mButtonTextMapping.get(mCurrentSuccessType));
    }
}