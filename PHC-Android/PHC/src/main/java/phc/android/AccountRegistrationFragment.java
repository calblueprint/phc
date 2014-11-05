package phc.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * AccountRegistrationFragment is the registration form for all new clients
 * and contains personal account questions that do not change over time.
 */
public class AccountRegistrationFragment extends Fragment{
    /* Continue button */
    Button mContinueButton;
    /* Listener for when the continue button is clicked */
    OnContinueClickListener mContinueClickListener;
    /* Spinners for multiple choice questions */
    Spinner mGenderSpinner, mEthnicitySpinner, mLanguageSpinner;

    /**
     * On creation of the fragment, sets content for spinners and an onClickListener
     * for the continue button.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_registration, container, false);
        setSpinnerContent(view);
        setOnContinueClickListener(view);
        return view;
    }

    /**
     * Sets multiple choice options for each spinner.
     */
    protected void setSpinnerContent(View view){
        mGenderSpinner = (Spinner) view.findViewById(R.id.spinner_gender);
        ArrayAdapter<CharSequence> mGenderAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        mGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenderSpinner.setAdapter(mGenderAdapter);

        mEthnicitySpinner = (Spinner) view.findViewById(R.id.spinner_ethnicity);
        ArrayAdapter<CharSequence> ethnicity_adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.ethnicity_array, android.R.layout.simple_spinner_item);
        ethnicity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEthnicitySpinner.setAdapter(ethnicity_adapter);

        mLanguageSpinner = (Spinner) view.findViewById(R.id.spinner_language);
        ArrayAdapter<CharSequence> mLanguageAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.language_array, android.R.layout.simple_spinner_item);
        mLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLanguageSpinner.setAdapter(mLanguageAdapter);
    }

    /**
     * Sets on-click listener for the continue button.
     * and sets the next fragment to the EventRegistrationFragment.
     */
    protected void setOnContinueClickListener(View view) {
        mContinueButton = (Button) view.findViewById(R.id.button_account_continue);
        mContinueClickListener =
                new OnContinueClickListener(getActivity(), new EventRegistrationFragment());
        mContinueButton.setOnClickListener(mContinueClickListener);
    }
}
