package phc.android.Checkin;

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

import phc.android.Helpers.NothingSelectedSpinnerAdapter;
import phc.android.Helpers.OnContinueClickListener;
import phc.android.R;

/**
 * EventInfoFragment.java is the event registration form for all clients
 * and includes fields that might have changed since the last event.
 */
public class EventInfoFragment extends CheckinFragment {
    /** Parent layout for all views. */
    private ViewGroup mLayout;
    /** Neighborhood spinner. */
    private Spinner mNeighborhoodSpinner;
    /** Housing spinner */
    private Spinner mHousingSpinner;
    /** Doctor checkbox. */
    private CheckBox mDoctorCheckbox;
    /** Whether the Doctor checkbox has been checked. */
    private Boolean mDoctorChecked = false;
    /** Doctor's clinic editText. */
    private EditText mClinicName;
    /** Doctor layout. */
    private ViewGroup mDoctorLayout;
    /** Children checkbox. */
    private CheckBox mChildrenCheckbox;
    /** Whether the Children checkbox has been checked. */
    private Boolean mChildrenChecked = false;
    /** Children age editText. */
    private EditText mChildrenAge;
    /** Children age layout. */
    private ViewGroup mChildrenLayout;
    /** Continue button. */
    private Button mContinueButton;

    /**
     * On creation of the fragment for the first time, sets onClickListeners for checkboxes with
     * optional open-ended EditTexts, sets content for spinners, and sets an onClickListener for the
     * continue button.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_info, container, false);
        mLayout = (LinearLayout) view.findViewById(R.id.event_fields);

        mDoctorCheckbox = (CheckBox) view.findViewById(R.id.checkbox_doctor);
        mDoctorLayout = (LinearLayout) view.findViewById(R.id.doctor_layout);
        mChildrenCheckbox = (CheckBox) view.findViewById(R.id.checkbox_children);
        mChildrenLayout = (LinearLayout) view.findViewById(R.id.children_layout);
        mContinueButton = (Button) view.findViewById(R.id.button_event_continue);
        mNeighborhoodSpinner = (Spinner) view.findViewById(R.id.spinner_neighborhood);
        mHousingSpinner = (Spinner) view.findViewById(R.id.spinner_housing);

        setSpinnerContent();
        setOnClickListeners(view);

        // If the bundle contains information, loads the optional EditTexts along with their state.
        if (savedInstanceState != null){
            if (savedInstanceState.getBoolean("doctor_check")){
                addClinicName();
                mClinicName.setText(savedInstanceState.getString("clinic_name"));
            }
            if (savedInstanceState.getBoolean("children_check")){
                addChildrenAge();
                mChildrenAge.setText(savedInstanceState.getString("children_age"));
            }
        }
        // Alternatively, if we are returning to this fragment on the backstack,
        // onSaveInstanceState is never called and we cannot rely on the bundle to store the
        // information. For this case, we rely on the two instance vars mDoctorChecked and
        // mChildrenChecked to recreate the state.
        else{
            if (mDoctorChecked){
                addClinicName();
            }
            if (mChildrenChecked){
                addChildrenAge();
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
                mDoctorChecked = mDoctorCheckbox.isChecked();
                if (mDoctorChecked == true) {
                    addClinicName();
                } else {
                    removeClinicName();
                }
            }
        });
        mChildrenCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChildrenChecked = mChildrenCheckbox.isChecked();
                if (mChildrenChecked == true) {
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
    private void setSpinnerContent(){
        ArrayAdapter<CharSequence> neighborhoodAdapter =
                ArrayAdapter.createFromResource(getActivity(),
                                                R.array.neighborhood_array,
                                                android.R.layout.simple_spinner_item);
        mNeighborhoodSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        neighborhoodAdapter,
                        R.layout.neighborhood_spinner_row_nothing_selected,
                        getActivity()));

        /** Set housing spinner values **/
        ArrayAdapter<CharSequence> housingAdapter =
                ArrayAdapter.createFromResource(getActivity(),
                                                R.array.housing_array,
                                                android.R.layout.simple_spinner_item);
        mHousingSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        housingAdapter,
                        R.layout.housing_spinner_row_nothing_selected,
                        getActivity()));
    }

    /**
     * Creates a new EditText prompting the name of the doctor's clinic when the doctor checkbox is
     * checked.
     */
    public void addClinicName(){
        mClinicName = new EditText(mDoctorLayout.getContext());
        mClinicName.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mClinicName.setHint(R.string.prompt_clinic_name);
        mClinicName.setId(R.id.clinic_name);
        mDoctorLayout.addView(mClinicName);
    }

    /**
     * Removes the clinic name EditText when unchecked.
     */
    public void removeClinicName(){
        mDoctorLayout.removeView(mClinicName);
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
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.checkin_sidebar_list);
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
     *  Handles orientation rotations.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDoctorCheckbox != null && mChildrenCheckbox != null){
            outState.putBoolean("doctor_check", mDoctorCheckbox.isChecked());
            if (mDoctorCheckbox.isChecked()){
                outState.putString("clinic_name", mClinicName.getText().toString());
            }
            outState.putBoolean("children_check", mChildrenCheckbox.isChecked());
            if (mChildrenCheckbox.isChecked()){
                outState.putString("children_age", mChildrenAge.getText().toString());
            }
        }
    }
}
