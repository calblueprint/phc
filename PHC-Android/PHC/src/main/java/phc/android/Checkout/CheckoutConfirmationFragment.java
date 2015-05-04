package phc.android.Checkout;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import phc.android.Helpers.OnSubmitClickListener;
import phc.android.Helpers.SharedPreferenceEditorListener;
import phc.android.R;
import phc.android.SharedFragments.ScannerConfirmationFragment;
import phc.android.SharedFragments.SuccessFragment;


public class CheckoutConfirmationFragment extends ScannerConfirmationFragment {

    /* Name for logs and fragment transaction code */
    public final static String TAG = "CheckoutConfirmationFragment";

    /* Displays comments */
    protected TextView mCommentsView;

    /* Displays experience */
    protected TextView mExperienceView;

    /* Displays experience */
    protected TextView mServicesView;


    /* Holds their input for experience rating 0-5 */
    protected int mExperience;

    /* Holds their comments */
    protected String mComments;

    /* Displays correct prompt */
    protected TextView mPrompt;

    /* Holds the services they have not received, but still would like */
    protected JSONArray mServicesNotReceived;

    /* Holds their checked services*/
    protected ArrayList<String> mServicesChecked;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /** Inflate the layout for this fragment and set up view. **/
        View view = setupView(inflater, container);

        /* Grab the last scan result from this fragment or the previous */
        if (savedInstanceState != null) {
            mScanResult = savedInstanceState.getCharSequence("scan_result").toString();
            mManualInput = savedInstanceState.getBoolean("manual_input");
            mExperience = savedInstanceState.getInt("experience");
            mComments = savedInstanceState.getString("comments");
            mServicesChecked = savedInstanceState.getStringArrayList("services");
        } else {
            mScanResult =  getArguments().getCharSequence("scan_result").toString();
            mManualInput = getArguments().getBoolean("manual_input");
            mExperience = getArguments().getInt("experience");
            mComments = getArguments().getString("comments");
            mServicesChecked = getArguments().getStringArrayList("services");
        }
        String prompt;
        if (mManualInput) {
            prompt = getString(R.string.text_input_confirmation);
        } else {
            prompt = getString(R.string.text_scan_confirmation);
        }

        mPrompt = (TextView) view.findViewById(R.id.checkout_confirmation_code_prompt);
        mPrompt.setText(prompt);

        mExperienceView = (TextView) view.findViewById(R.id.checkout_confirmation_experience_result);
        String mExperienceString = Integer.toString(mExperience);
        mExperienceView.setText(mExperienceString);

        mCommentsView = (TextView) view.findViewById(R.id.checkout_confirmation_comments_result);
        mCommentsView.setText(mComments);

        mServicesView= (TextView)view.findViewById(R.id.checkout_confirmation_services);

        mServicesNotReceived = new JSONArray();

        // Set the text beneath "Requested Services:"
        String mServicesString = new String();
        for(int i = 0; i < mServicesChecked.size(); i++){
            mServicesNotReceived.put(mServicesChecked.get(i).toString());
            mServicesString += mServicesChecked.get(i);
            mServicesString += ", ";
        }
        mServicesView.setText(mServicesString);

        mScanResultView = (TextView) view.findViewById(R.id.checkout_confirmation_scan_result);
        mScanResultView.setText(mScanResult);

        mConfirmButton = (Button) view.findViewById(R.id.checkout_confirmation_confirm_scan);
        mConfirmButton.setOnClickListener(new ConfirmListener());

        mRetryButton = (Button) view.findViewById(R.id.checkout_confirmation_retry_scan);
        mRetryButton.setOnClickListener(new RetryListener());

        return view;
    }


    /**
     * Separate method for setting up view so that this
     * functionality can be overriden by a subclass.
     * @param inflater instantiates the XML layout
     * @param container is the view group this view belongs to
     */
    @Override
    protected View setupView(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragment_checkout_confirmation, container, false);

        return view;
    }
    // Can remove when sidebar is added
    // TODO: Fix resumeHelper
    @Override
    protected void resumeHelper(){

    }


    /**
     * Returns to scanner fragment and displays a
     * failure toast.
     */
    @Override
    protected void retry() {
        showFailureToast();
        FragmentManager manager = getFragmentManager();
        manager.popBackStack(CheckoutConfirmationFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }



    /**
     * Save the scan result when this fragment is
     * paused
     * @param outState Bundle passed in by Android
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("scan_result", mScanResult);
        outState.putBoolean("manual_input", mManualInput);
        outState.putInt("experience", mExperience);
        outState.putString("comments", mComments);
    }

    /**
     * Used to confirm the scan result.
     */
    public class ConfirmListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            confirm();
        }
    }


    /**
     * Records the scan, returns to scanner fragment,
     * and displays a success toast.
     */
    @Override
    protected void confirm() {
        mUserPreferences = getActivity().getSharedPreferences(USER_AUTH_PREFS_NAME,
                Context.MODE_PRIVATE);
        String userId = mUserPreferences.getString("user_id", null);
        String authToken = mUserPreferences.getString("auth_token", null);

        sRequestManager.requestUpdateFeedback(
                mComments,
                mExperience,
                mServicesNotReceived,
                mScanResult,
                userId,
                authToken,
                new UpdateResponseListener(),
                new UpdateErrorListener());

    }

    private class UpdateResponseListener implements Response.Listener<JSONObject>{

        @Override
        public void onResponse(JSONObject jsonObject){
            //mUserInfo.edit().clear().apply();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            SuccessFragment successFragment = new SuccessFragment();
            successFragment.setType(SuccessFragment.SuccessType.CHECKOUT_SUCCESS);
            transaction.replace(R.id.checkout_activity_container, successFragment);
            transaction.commit();
            Log.d(TAG, jsonObject.toString());
        }
    }
    private class UpdateErrorListener implements Response.ErrorListener{
        @Override
        public void onErrorResponse(VolleyError volleyError){
            if (volleyError != null && volleyError.getLocalizedMessage()!=null) {
                Log.e(TAG, "volleyError.getLocalizedMessage() " + volleyError.getLocalizedMessage());
                volleyError.printStackTrace();
            }
            Toast toast = Toast.makeText(getActivity(), "Error checking out user", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}