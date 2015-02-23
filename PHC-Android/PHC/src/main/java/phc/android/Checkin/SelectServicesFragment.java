package phc.android.Checkin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import phc.android.Checkin.CheckinActivity;
import phc.android.Checkin.CheckinFragment;
import phc.android.Checkin.CheckinScannerFragment;
import phc.android.Helpers.OnContinueClickListener;
import phc.android.R;

public class SelectServicesFragment extends CheckinFragment {
    /** Continue button. */
    private Button mContinueButton;
    /** Sorted array of all service salesforce names (keys of the hashmap). */
    private String[] mServiceSFNames;
    /** Parent layout that holds all checkbox views. */
    private ViewGroup mLayout;

    /**
     * On creation of the fragment, grabs list of all services offered for
     * the current event from Salesforce DB and dynamically populates layout
     * with checkboxes for each service.
     * IMPORTANT NOTE: this fragment is unable to use the OnContinueClickListener because
     * the checkboxes do not have declared resource_names like
     * In other words, the values of the checkboxes are not written to SharedPreferences
     * when the continue button is hit, but when the checkboxes themselves are clicked.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_services, container, false);

        dynamicSetCheckboxes(view);

        mContinueButton = (Button) view.findViewById(R.id.button_services_continue);
        mContinueButton.setOnClickListener(new OnContinueClickListener(
                getActivity(), this, mLayout, new CheckinScannerFragment(),
                getResources().getString(R.string.sidebar_services_info)));
        return view;
    }

    /**
     * Dynamically populates layout with checkboxes for each service.
     * Note: the current ID assignment method is rather hacky, but it's the only way that seems to
     * work. Android is not able to save and load dynamically created views even when their ID is
     * set, unless the views are created again with the same previous ID.
     */
    private void dynamicSetCheckboxes(View view){
        mLayout = (LinearLayout) view.findViewById(R.id.services_list);

        HashMap<String, String> service_map = ((CheckinActivity) getActivity()).getServices();
        mServiceSFNames = ((CheckinActivity) getActivity()).getServiceSFNames();

        for(int i = 0; i < mServiceSFNames.length; i++){
            CheckBox cb = new CheckBox(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cb.setLayoutParams(params);
            cb.setId(i);
            cb.setText(service_map.get(mServiceSFNames[i]));
            mLayout.addView(cb);
        }
    }

    @Override
    public void onResume() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getText(R.string.sidebar_services_info)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
        super.onResume();
    }

    /** Static method that returns a string array of salesforce names for all services. */
    public String[] getServiceSFNames() {
        return this.mServiceSFNames;
    }
}
