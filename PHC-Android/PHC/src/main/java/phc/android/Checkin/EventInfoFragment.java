package phc.android.Checkin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    /** Homeless duration spinner */
    private Spinner mHomelessDurationSpinner;
    /** Homeless layout */
    private ViewGroup mHomelessLayout;
    /** Healthcare spinner */
    private Spinner mHealthcareSpinner;
    /** Healthcare's other editText. */
    private EditText mHealthcareOtherText;

    /** Doctor layout. */
    private ViewGroup mHealthcareLayout;
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
        mHealthcareLayout = (LinearLayout) view.findViewById(R.id.healthcare_layout);
        mHomelessLayout = (LinearLayout) view.findViewById(R.id.homeless_layout);
        mContinueButton = (Button) view.findViewById(R.id.button_event_continue);
        mNeighborhoodSpinner = (Spinner) view.findViewById(R.id.spinner_neighborhood);
        mHousingSpinner = (Spinner) view.findViewById(R.id.spinner_housing);
        mHealthcareSpinner = (Spinner) view.findViewById(R.id.spinner_healthcare);

        if(savedInstanceState != null){
            Log.d("savedInstanceState != null ", " ");
            if(savedInstanceState.getInt("healthcare_spinner") == 5) {
                // Healthcare is Other
                mHealthcareOtherText = (EditText) view.findViewById(R.id.healthcare_name);
                mHealthcareOtherText.setText(savedInstanceState.getString("healthcare_name"));
                Log.d("savedInstanceState != null ", "mHealthcareName is set");
            } else{
                Log.d("", "");
                mHealthcareOtherText = (EditText) view.findViewById(R.id.healthcare_name);
                removeHealthcareName();
                Log.d("savedInstanceState != null ", "mHealthcareName is removed");
            }

            if (savedInstanceState.getInt("housing_spinner") == 1){
                // Housing Status is Homeless
                // Set HomelessSpinner
                mHomelessDurationSpinner = (Spinner) view.findViewById(R.id.spinner_homeless);
                mHomelessDurationSpinner.setSelection(savedInstanceState.getInt("homeless_spinner"));
                Log.d("savedInstanceState != null ", "mHomelessspinner is set");
            }else{
                mHomelessDurationSpinner = (Spinner) view.findViewById(R.id.spinner_homeless);
                removeHomelessDuration();
                Log.d("savedInstanceState != null ", "mHomelessspinner is removed");
            }
        } else {
            if (mHomelessDurationSpinner != null){
                removeHomelessDuration();
            }
            if (mHealthcareOtherText != null){
                removeHealthcareName();
            }
        }

        setSpinnerContent();
        setOnClickListeners(view);
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
            public void onNothingSelected(AdapterView<?> adapterView) {
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
    }

    /**
     * Creates a new Spinner prompting the time spent homeless
     */

    public void addHomelessDuration(){
        mHomelessDurationSpinner = new Spinner(mHomelessLayout.getContext());
        mHomelessDurationSpinner.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mHomelessDurationSpinner.setId(R.id.spinner_homeless);

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
        mHomelessLayout.addView(mHomelessDurationSpinner);
    }

    /**
     * Removes the homeless duration Spinner
     */
    public void removeHomelessDuration(){
        mHomelessLayout.removeView(mHomelessDurationSpinner);
    }


    /**
     * Creates a new EditText prompting the name of the clinic/healthcare when the Other option is
     * selected for healthcare.
     */
    public void addHealthcareName(){
        mHealthcareOtherText = new EditText(mHealthcareLayout.getContext());
        mHealthcareOtherText.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mHealthcareOtherText.setHint(R.string.prompt_clinic_name);
        mHealthcareOtherText.setId(R.id.healthcare_name);
        mHealthcareLayout.addView(mHealthcareOtherText);
    }

    /**
     * Removes the clinic name EditText when unchecked.
     */
    public void removeHealthcareName(){
        Log.d("ENTER-REMOVE-HEALTHCARE", "ENTERED");
        mHealthcareLayout.removeView(mHealthcareOtherText);
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
     *  Explicitly stores state of the housing and healthcare spinners,
     *  along with their optional EditText and Spinner input, into bundle.
     *  Handles orientation rotations.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("housing_spinner", mHousingSpinner.getSelectedItemPosition());
        outState.putInt("healthcare_spinner", mHealthcareSpinner.getSelectedItemPosition());
        if (mHealthcareOtherText != null ){
            outState.putString("healthcare_name", mHealthcareOtherText.getText().toString());
            Log.d("onSaveInstanceState", "mHealthCareName != null ");
        }
    }
}
