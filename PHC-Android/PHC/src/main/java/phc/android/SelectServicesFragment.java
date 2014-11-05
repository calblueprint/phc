package phc.android;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class SelectServicesFragment extends Fragment{
    /* Submit button. */
    private Button mSubmitButton;
    /* Listener for when the submit button is clicked. */
    private OnSubmitClickListener mSubmitClickListener;
    /* Services offered for the current event. */
    private String[] services;

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
        setOnSubmitClickListener(view);
        return view;
    }

    /**
     * Dynamically populates layout with checkboxes for each service.
     * TODO: CHANGE TO ACTUALLY GRAB FROM SALESFORCE.
     */
    protected void dynamicSetCheckboxes(View view){
        Resources res = getResources();
        services = res.getStringArray(R.array.services_array);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.services_list);

        for(String s: services){
            CheckBox cb = new CheckBox(getActivity());
            cb.setLayoutParams(new LinearLayout.LayoutParams(
                    R.dimen.input_text_width,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            cb.setText(s);
            cb.setOnClickListener(new OnDynamicCheckboxClickListener(getActivity(), s));
            layout.addView(cb);
        }
    }

    /**
     * Creates an OnSubmitClickListener for the submit button
     * and sets the next fragment to the SuccessFragment.
     */
    protected void setOnSubmitClickListener(View view) {
        mSubmitButton = (Button) view.findViewById(R.id.button_submit);
        mSubmitClickListener = new OnSubmitClickListener(getActivity());
        mSubmitButton.setOnClickListener(mSubmitClickListener);
    }
}
