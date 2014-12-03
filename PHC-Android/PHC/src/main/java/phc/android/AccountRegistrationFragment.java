package phc.android;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * AccountRegistrationFragment is the registration form for all new clients
 * and contains personal account questions that do not change over time.
 */
public class AccountRegistrationFragment extends Fragment {
    private Button mContinueButton;
    private Spinner mGenderSpinner, mEthnicitySpinner, mLanguageSpinner;
    private EditText mMonth, mDay, mYear, mPhone1, mPhone2, mPhone3, mSSN1, mSSN2, mSSN3, mEmail, mFirstName, mLastName;

    /**
     * Set spinner content and continue button functionality.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_registration, container, false);

        initializeLocalVariables(view);
        addEditTextListeners(view);
        setSpinnerContent(view);
        prepopulateForm();
        mContinueButton = (Button) view.findViewById(R.id.button_account_continue);
        mContinueButton.setOnClickListener(new OnContinueClickListener(getActivity(),
                new EventRegistrationFragment(), getResources().getString(R.string.sidebar_event_info)));
        return view;
    }

    @Override
    public void onResume() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getText(R.string.sidebar_personal_info)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
        super.onResume();
    }

    private void initializeLocalVariables(View view) {

        mFirstName = (EditText) view.findViewById(R.id.first_name);
        mLastName = (EditText) view.findViewById(R.id.last_name);

        mSSN1 = (EditText) view.findViewById(R.id.ssn_1);
        mSSN2 = (EditText) view.findViewById(R.id.ssn_2);
        mSSN3 = (EditText) view.findViewById(R.id.ssn_3);

        mMonth = (EditText) view.findViewById(R.id.birthday_month);
        mDay = (EditText) view.findViewById(R.id.birthday_day);
        mYear = (EditText) view.findViewById(R.id.birthday_year);

        mPhone1 = (EditText) view.findViewById(R.id.phone_1);
        mPhone2 = (EditText) view.findViewById(R.id.phone_2);
        mPhone3 = (EditText) view.findViewById(R.id.phone_3);

        mEmail = (EditText) view.findViewById(R.id.email);

        mGenderSpinner = (Spinner) view.findViewById(R.id.spinner_gender);
        mEthnicitySpinner = (Spinner) view.findViewById(R.id.spinner_ethnicity);
        mLanguageSpinner = (Spinner) view.findViewById(R.id.spinner_language);
    }

    private void addEditTextListeners(View view){
        mSSN1.addTextChangedListener(new TextLengthWatcher(3,mSSN2));
        mSSN2.addTextChangedListener(new TextLengthWatcher(2,mSSN3));
        mSSN3.addTextChangedListener(new TextLengthWatcher(4,mMonth));

        mMonth.addTextChangedListener(new TextLengthWatcher(2,mDay));
        mDay.addTextChangedListener(new TextLengthWatcher(2,mYear));
        mYear.addTextChangedListener(new TextLengthWatcher(4,mPhone1));

        mPhone1.addTextChangedListener(new TextLengthWatcher(3,mPhone2));
        mPhone2.addTextChangedListener(new TextLengthWatcher(3,mPhone3));
        mPhone3.addTextChangedListener(new TextLengthWatcher(4,mEmail));

    }

    private void setSpinnerContent(View view){
        String[] genders = getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<String> genderAdapter = new HintAdapter(
                getActivity(), android.R.layout.simple_spinner_item, genders);
        mGenderSpinner.setAdapter(genderAdapter);
        mGenderSpinner.setSelection(genderAdapter.getCount());

        String[] ethnicities = getResources().getStringArray(R.array.ethnicity_array);
        ArrayAdapter<String> ethnicityAdapter = new HintAdapter(
                getActivity(), android.R.layout.simple_spinner_item, ethnicities);
        mEthnicitySpinner.setAdapter(ethnicityAdapter);
        mEthnicitySpinner.setSelection(ethnicityAdapter.getCount());

        String[] languages = getResources().getStringArray(R.array.language_array);
        ArrayAdapter<String> languageAdapter = new HintAdapter(
                getActivity(), android.R.layout.simple_spinner_item, languages);
        mLanguageSpinner.setAdapter(languageAdapter);
        mLanguageSpinner.setSelection(languageAdapter.getCount());
    }

    private void prepopulateForm() {
        String preferencesFile = SearchResultsFragment.SEARCH_RESULT_PREFERENCES;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(preferencesFile, 0);

        String ssNum = sharedPreferences.getString("SS_Num", null);
        String firstName = sharedPreferences.getString("FirstName", null);
        String lastName = sharedPreferences.getString("LastName", null);
        String phone = sharedPreferences.getString("Phone", null);
        String birthdate = sharedPreferences.getString("Birthdate", null);
        String email = sharedPreferences.getString("Email", null);
        String gender = sharedPreferences.getString("Gender", null);
        String ethnicity = sharedPreferences.getString("Ethnicity", null);
        String language = sharedPreferences.getString("Language", null);

        if(firstName != null) mFirstName.setText(firstName);
        if(lastName != null) mLastName.setText(lastName);
        if(email != null) mEmail.setText(email);
        if(ssNum != null && ssNum.length() == 9) {
            mSSN1.setText(ssNum.substring(0, 3));
            mSSN2.setText(ssNum.substring(3, 5));
            mSSN3.setText(ssNum.substring(5));
        }
        if(phone != null && phone.length() == 10) {
            mPhone1.setText(ssNum.substring(0, 3));
            mPhone2.setText(ssNum.substring(3, 6));
            mPhone3.setText(ssNum.substring(6));
        }
    }
}
