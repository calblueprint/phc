package phc.android;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * EventRegistrationFragment is the event registration form for all clients
 * and includes fields that might have changed since the last event.
 */
public class EventRegistrationFragment extends Fragment{
    /* Continue button */
    Button mSubmitButton;
    /* Integer used to help generate unique IDs for the checkboxes. */
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    /**
     * On creation of the fragment, sets content for spinners and an onClickListener
     * for the continue button.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_registration, container, false);

        // Grab list of all services offered for the current event from Salesforce DB
        // Dynamically populate linear layout with checkboxes for each service
        Resources res = getResources();

        String[] medicalServices = res.getStringArray(R.array.medical_services_array);
        LinearLayout medicalServicesList = (LinearLayout) view.findViewById(R.id.medical_services_list);
        dynamicSetCheckboxes(view, medicalServicesList, medicalServices);


        String[] supportServices = res.getStringArray(R.array.support_services_array);
        LinearLayout supportServicesList = (LinearLayout) view.findViewById(R.id.support_services_list);
        dynamicSetCheckboxes(view, supportServicesList, supportServices);

        mSubmitButton = (Button) view.findViewById(R.id.button_submit);
        mSubmitButton.setOnClickListener(new OnSubmitClickListener(getActivity()));

        return view;
    }

    @Override
    public void onResume() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getText(R.string.sidebar_event_info)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
        super.onResume();
    }

    /**
     * Dynamically populates layout with checkboxes for each service.
     * TODO: CHANGE TO ACTUALLY GRAB FROM SALESFORCE.
     */
    protected void dynamicSetCheckboxes(View view, LinearLayout ll, String[] services){

        for(String s: services){
            CheckBox cb = new CheckBox(getActivity());
            cb.setLayoutParams(new LinearLayout.LayoutParams(
                    R.dimen.input_text_width,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            //assigns Id to checkbox. if build version is level 17 or higher, uses built-in method.
            cb.setId(generateViewId());
            cb.setText(s);
            cb.setOnClickListener(new OnDynamicCheckboxClickListener(getActivity(), s));
            ll.addView(cb);
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
}
