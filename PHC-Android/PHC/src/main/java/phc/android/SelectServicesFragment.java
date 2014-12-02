package phc.android;

import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectServicesFragment extends RegistrationFragment {
    /**
     * On creation of the fragment, grabs list of all services offered for
     * the current event from Salesforce DB and dynamically populates layout
     * with checkboxes for each service.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_services, container, false);
        dynamicSetCheckboxes(view);

        Button mContinueButton = (Button) view.findViewById(R.id.button_services_continue);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.registration_fragment_container,
                        new RegistrationScannerFragment(), getResources().getString(R.string.sidebar_scan_code));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    /**
     * Dynamically populates layout with checkboxes for each service.
     * Note: the current ID assignment method is rather hacky, but it's the only way that seems to
     * work. Android is not able to save/load dynamically created views even when their ID is
     * set, unless the views are created again with the same previous Id.
     * TODO: CHANGE TO ACTUALLY GRAB FROM SALESFORCE.
     */
    protected void dynamicSetCheckboxes(View view){
        String[] services = getResources().getStringArray(R.array.services_array);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.services_list);

        for(int i = 0; i < services.length; i++){
            CheckBox cb = new CheckBox(getActivity());
            cb.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            cb.setId(i);
            cb.setText(services[i]);
            cb.setOnClickListener(new OnDynamicCheckboxClickListener(getActivity(), services[i]));
            layout.addView(cb);
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
}
