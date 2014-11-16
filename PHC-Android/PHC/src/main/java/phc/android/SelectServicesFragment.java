package phc.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

public class SelectServicesFragment extends RegistrationFragment {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

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
                transaction.replace(R.id.registration_fragment_container, new SuccessFragment(), getResources().getString(R.string.sidebar_confirmation));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    /**
     * Dynamically populates layout with checkboxes for each service.
     * TODO: CHANGE TO ACTUALLY GRAB FROM SALESFORCE.
     */
    protected void dynamicSetCheckboxes(View view){
        Resources res = getResources();
        String[] services = res.getStringArray(R.array.services_array);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.services_list);

        for(String s: services){
            CheckBox cb = new CheckBox(getActivity());
            cb.setLayoutParams(new LinearLayout.LayoutParams(
                    R.dimen.input_text_width,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            //assigns Id to checkbox. if build version is level 17 or higher, uses built-in method.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cb.setId(generateViewId());
            } else {
                cb.setId(View.generateViewId());
            }
            cb.setText(s);
            cb.setOnClickListener(new OnDynamicCheckboxClickListener(getActivity(), s));
            layout.addView(cb);
        }
    }

    /**
     * Generate a unique ID for each checkbox view.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
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
