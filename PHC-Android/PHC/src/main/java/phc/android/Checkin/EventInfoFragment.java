package phc.android.Checkin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    /** Housing spinner */
    private Spinner mHousingSpinner;
    /** Homeless duration spinner */
    private Spinner mHomelessDurationSpinner;
    /** Healthcare spinner */
    private Spinner mHealthcareSpinner;
    /** Healthcare's other editText. */
    private EditText mHealthcareOtherText;
    /** Continue button. */
    private Button mContinueButton;
    /** Whether they selected Other option on the heatlthcare spinner  */
    private Boolean mHealthcareOtherChecked = false;
    /** Whether they selected Homeless option on the housing spinner  */
    private Boolean mHomelessChecked = false;

    /**
     * On creation of the fragment for the first time, sets onClickListeners for checkboxes with
     * optional open-ended EditTexts, sets content for spinners, and sets an onClickListener for the
     * continue button.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_info, container, false);
        mHealthcareOtherText = (EditText) view.findViewById(R.id.healthcare_other);
        mHomelessDurationSpinner = (Spinner) view.findViewById(R.id.spinner_homeless_duration);
        mLayout = (LinearLayout) view.findViewById(R.id.event_fields);
        mContinueButton = (Button) view.findViewById(R.id.button_event_continue);

        mHousingSpinner = (Spinner) view.findViewById(R.id.spinner_housing);
        mHealthcareSpinner = (Spinner) view.findViewById(R.id.spinner_healthcare);
        setSpinnerContent();
        setOnClickListeners(view);
        if(savedInstanceState != null){
            mHealthcareOtherChecked = savedInstanceState.getBoolean("healthcare_other_check");
            mHomelessChecked = savedInstanceState.getBoolean("homeless_check");
            if(mHealthcareOtherChecked) {
                // 'Other' option selected on Healthcare spinner
                addHealthcareName();
                mHealthcareOtherText.setText(savedInstanceState.getString("healthcare_name"));
            } else{
                removeHealthcareName();
            }
            if(mHomelessChecked){
                // 'Homeless' option selected on homeless spinner
                addHomelessDuration();
                mHomelessDurationSpinner.setSelection(savedInstanceState.getInt("homeless_duration")); // Set duration spinner
            }else{
                removeHomelessDuration();
            }
        }
        // Alternatively, if we are returning to this fragment on the backstack,
        // onSaveInstanceState is never called and we cannot rely on the bundle to store the
        // information. For this case, we rely on the two instance vars mHealthcareOtherchecked and
        // mHomelessChecked to recreate the state.

        else {
            if (mHealthcareOtherChecked){
                addHealthcareName();
            } else{
                removeHealthcareName();
            }
            if (mHomelessChecked){
                addHomelessDuration();
            }
        }
        return view;
    }


    private void setOnClickListeners(View view) {
        mHealthcareSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                Object item = adapterView.getItemAtPosition(pos);
                if (item != null && item.toString().equals("Other")){
                        addHealthcareName();
                }else{
                    removeHealthcareName();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mHousingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                Object item = adapterView.getItemAtPosition(pos);
                if (item != null && item.toString().equals("Homeless")){
                        addHomelessDuration();
                }else{
                    removeHomelessDuration();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        mContinueButton.setOnClickListener(new OnContinueClickListener(
                getActivity(), this, mLayout, new SelectServicesFragment(),
                getResources().getString(R.string.sidebar_services_info)));
    }


    private void setSpinnerContent(){
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

        /** Set healthcare spinner values **/
        ArrayAdapter<CharSequence> healthcareAdapter =
                ArrayAdapter.createFromResource(getActivity(),
                        R.array.healthcare_array,
                        android.R.layout.simple_spinner_item);
        mHealthcareSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        healthcareAdapter,
                        R.layout.healthcare_spinner_row_nothing_selected,
                        getActivity()));

        /** Set homeless spinner values **/
        ArrayAdapter<CharSequence> homelessAdapter=
                ArrayAdapter.createFromResource(getActivity(),
                        R.array.homeless_array,
                        android.R.layout.simple_spinner_item);
        mHomelessDurationSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        homelessAdapter,
                        R.layout.homeless_spinner_row_nothing_selected,
                        getActivity()));
    }

    /**
     * Shows the Spinner prompting the time spent homeless
     */
    public void addHomelessDuration(){
        mHomelessDurationSpinner.setVisibility(View.VISIBLE);
        mHomelessChecked = true;
    }

    /**
     * Hides the homeless duration Spinner
     */
    public void removeHomelessDuration(){
        mHomelessDurationSpinner.setVisibility(View.GONE);
        mHomelessChecked = false;
    }


    /**
     * Shows the EditText prompting the name of the clinic/healthcare when the 'Other' option is
     * selected for the healthcare spinner.
     */
    public void addHealthcareName(){
        mHealthcareOtherText.setVisibility(View.VISIBLE);
        mHealthcareOtherChecked = true;
    }

    /** Hides the EditText for Healthcare name */
    public void removeHealthcareName(){
        mHealthcareOtherText.setVisibility(View.GONE);
        mHealthcareOtherChecked = false;
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
     *  Explicitly stores state of the housing and healthcare spinners,
     *  along with their optional EditText and Spinner input, into bundle.
     *  Handles orientation rotations.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("healthcare_other_check", mHealthcareOtherChecked);
        outState.putBoolean("homeless_check", mHomelessChecked);
        if (mHomelessChecked){
            // Save the Duration selected
            outState.putInt("homeless_duration", mHomelessDurationSpinner.getSelectedItemPosition());
        }
        if (mHealthcareOtherChecked){
            outState.putString("healthcare_name", mHealthcareOtherText.getText().toString());
        }

    }
}
