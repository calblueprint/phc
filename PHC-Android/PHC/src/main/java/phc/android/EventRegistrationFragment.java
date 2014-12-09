package phc.android;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * EventRegistrationFragment is the event registration form for all clients
 * and includes fields that might have changed since the last event.
 */
public class EventRegistrationFragment extends RegistrationFragment{
    /** Parent layout for all views */
    private ViewGroup mLayout;
    /** Neighborhood spinner. */
    private Spinner mNeighborhoodSpinner;
    /** Doctor checkbox. */
    private CheckBox mDoctorCheckbox;
    /** Doctor name editText. */
    private EditText mDoctorName;
    /** Linear layout for doctor information.*/
    private ViewGroup mDoctorLayout;
    /** Children checkbox. */
    private CheckBox mChildrenCheckbox;
    /** Children age editText. */
    private EditText mChildrenAge;
    /** Linear layout for doctor information.*/
    private ViewGroup mChildrenLayout;
    /** Continue button. */
    private Button mContinueButton;

    /**
     * On creation of the fragment for the first time, sets onClickListeners for checkboxes with
     * optional open-ended EditTexts, sets content for spinners, and sets an onClickListener for the
     * continue button.
     * If the bundle contains information, loads the optional EditTexts along with their state.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_registration, container, false);
        mLayout = (LinearLayout) view.findViewById(R.id.event_fields);

        mDoctorCheckbox = (CheckBox) view.findViewById(R.id.checkbox_doctor);
        mDoctorLayout = (LinearLayout) view.findViewById(R.id.doctor_layout);
        mChildrenCheckbox = (CheckBox) view.findViewById(R.id.checkbox_children);
        mChildrenLayout = (LinearLayout) view.findViewById(R.id.children_layout);
        mContinueButton = (Button) view.findViewById(R.id.button_event_continue);

        setSpinnerContent(view);
        setOnClickListeners(view);

        if (savedInstanceState != null){
            if (savedInstanceState.getBoolean("doctor_check")){
                addDoctorName();
                mDoctorName.setText(savedInstanceState.getString("doctor_name"));
            }
            if (savedInstanceState.getBoolean("children_check")){
                addChildrenAge();
                mChildrenAge.setText(savedInstanceState.getString("children_age"));
            }
        }
        return view;
    }

    /**
     * Sets onClickListeners for the doctor and children checkboxes,
     * as well as the continue button.
     */
    private void setOnClickListeners(View view) {
        mDoctorCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = mDoctorCheckbox.isChecked();
                if (checked == true) {
                    addDoctorName();
                } else {
                    removeDoctorName();
                }
            }
        });
        mChildrenCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = mChildrenCheckbox.isChecked();
                if (checked == true) {
                    addChildrenAge();
                } else {
                    removeChildrenAge();
                }
            }
        });
        mContinueButton.setOnClickListener(new OnContinueClickListener(
                getActivity(), this, mLayout, new SelectServicesFragment(),
                getResources().getString(R.string.sidebar_services_info)));
    }

    /**
     * Sets multiple choice options for the neighborhood spinner.
     */
    private void setSpinnerContent(View view){
        mNeighborhoodSpinner = (Spinner) view.findViewById(R.id.spinner_neighborhood);
        String[] neighborhoods = getResources().getStringArray(R.array.neighborhood_array);
        ArrayAdapter<String> neighborhoodAdapter = new HintAdapter(
                getActivity(), android.R.layout.simple_spinner_item, neighborhoods);
        mNeighborhoodSpinner.setAdapter(neighborhoodAdapter);
        mNeighborhoodSpinner.setSelection(neighborhoodAdapter.getCount());
    }

    /**
     * Creates a new EditText prompting the doctor's name when the doctor checkbox is checked.
     */
    public void addDoctorName(){
        mDoctorName = new EditText(mDoctorLayout.getContext());
        mDoctorName.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mDoctorName.setHint(R.string.prompt_doctor_name);
        mDoctorName.setId(R.id.doctor_name);
        mDoctorLayout.addView(mDoctorName);
    }

    /**
     * Removes the doctor name EditText when unchecked.
     */
    public void removeDoctorName(){
        mDoctorLayout.removeView(mDoctorName);
    }

    /**
     * Creates a new EditText prompting the childrens' ages when the child checkbox is checked.
     */
    public void addChildrenAge(){
        mChildrenAge = new EditText(mChildrenLayout.getContext());
        mChildrenAge.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mChildrenAge.setHint(R.string.prompt_children_age);
        mChildrenAge.setId(R.id.children_age);
        mChildrenLayout.addView(mChildrenAge);
    }

    /**
     * Removes the children age EditText when unchecked.
     */
    public void removeChildrenAge(){
        mChildrenLayout.removeView(mChildrenAge);
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
     *  Explicitly stores state of the children and doctor checkboxes,
     *  along with their optional EditText inputs, into bundle.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDoctorCheckbox != null && mChildrenCheckbox != null){
            outState.putBoolean("doctor_check", mDoctorCheckbox.isChecked());
            if (mDoctorCheckbox.isChecked()){
                outState.putString("doctor_name", mDoctorName.getText().toString());
            }
            outState.putBoolean("children_check", mChildrenCheckbox.isChecked());
            if (mChildrenCheckbox.isChecked()){
                outState.putString("children_age", mChildrenAge.getText().toString());
            }
        }
    }
}
