package phc.android.Checkin;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import phc.android.Helpers.NothingSelectedSpinnerAdapter;
import phc.android.Helpers.OnContinueClickListener;
import phc.android.Helpers.TextLengthWatcher;
import phc.android.R;

/**
 * PersonalInfoFragment is the registration form for all new clients
 * and contains personal account questions that do not change over time.
 */
public class PersonalInfoFragment extends Fragment {
    // Parent layout for all views.
    private ViewGroup mLayout;
    // Prompt text. Changes depending on whether client is a new user or returning user.
    private TextView mPrompt;
    // Spinners.
    private Spinner mGenderSpinner, mEthnicitySpinner, mLanguageSpinner;
    // EditTexts.
    private EditText mMonth, mDay, mYear, mPhone1, mPhone2, mPhone3,
            mSSN1, mSSN2, mSSN3, mEmail, mFirstName, mLastName;
    // Continue button.
    private Button mContinueButton;
    // Parent Activity
    private CheckinActivity mParent;
    // GLBT Checkbox
    private CheckBox mGLBTCheckbox;
    // Military Checkbox
    private CheckBox mMilitaryCheckbox;
    // Foster Checkbox
    private CheckBox mFosterCheckbox;


    /**
     * Set spinner content and continue button functionality.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);
        mLayout = (LinearLayout) view.findViewById(R.id.form_fields);

        initializeLocalVariables(view);
        addEditTextListeners();
        setSpinnerContent();

        mPrompt = (TextView) view.findViewById(R.id.personal_info_prompt);

        // Only pre-populate the form if the user is a returning user.
        if (mParent.getCurrentState() == CheckinActivity.FormDataState.SAVE_DATA) {
            mPrompt.setText(R.string.personal_info_edit);
            prepopulateForm();
        } else {
            mPrompt.setText(R.string.personal_info_new);
        }

        mContinueButton = (Button) view.findViewById(R.id.button_account_continue);
        mContinueButton.setOnClickListener(new OnPersonalInfoContinueClickListener(getActivity(),
                this, mLayout, new EventInfoFragment(), getResources().getString(R.string
                .sidebar_event_info)));
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        mParent = (CheckinActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.checkin_sidebar_list);
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

        // If we have a new user, we need to clear the fields
        if (mParent.getCurrentState() == CheckinActivity.FormDataState.CLEAR_DATA) {
            clearFields();
        }
        // Once this fragment is entered, we now save form data
        // This will be reset to CLEAR DATA when returning to the SelectionFragment
        mParent.setCurrentState(CheckinActivity.FormDataState.SAVE_DATA);
        super.onResume();
    }

    /**
     * Assign local variables to their respective views.
     *
     * @param view: The fragment's view, containing all of the necessary
     *            sub-views
     */
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

        mGLBTCheckbox = (CheckBox) view.findViewById(R.id.checkbox_glbt);
        mFosterCheckbox = (CheckBox) view.findViewById(R.id.checkbox_foster);
        mMilitaryCheckbox = (CheckBox) view.findViewById(R.id.checkbox_military);
    }

    /**
     * Clears the fields
     */
    private void clearFields() {
        mFirstName.setText("");
        mLastName.setText("");

        mSSN1.setText("");
        mSSN2.setText("");
        mSSN3.setText("");

        mMonth.setText("");
        mDay.setText("");
        mYear.setText("");

        mPhone1.setText("");
        mPhone2.setText("");
        mPhone3.setText("");

        mEmail.setText("");

        mGenderSpinner.setSelection(0);
        mEthnicitySpinner.setSelection(0);
        mLanguageSpinner.setSelection(0);

        mGLBTCheckbox.setChecked(false);
        mFosterCheckbox.setChecked(false);
        mMilitaryCheckbox.setChecked(false);
    }

    /**
     * Adds TextChangedListeners to the phone and ssn fields
     */
    private void addEditTextListeners(){
        mSSN1.addTextChangedListener(new TextLengthWatcher(3, mSSN2));
        mSSN2.addTextChangedListener(new TextLengthWatcher(2, mSSN3));
        mSSN3.addTextChangedListener(new TextLengthWatcher(4, mMonth));

        mMonth.addTextChangedListener(new TextLengthWatcher(2, mDay));
        mDay.addTextChangedListener(new TextLengthWatcher(2, mYear));
        mYear.addTextChangedListener(new TextLengthWatcher(4, mPhone1));

        mPhone1.addTextChangedListener(new TextLengthWatcher(3, mPhone2));
        mPhone2.addTextChangedListener(new TextLengthWatcher(3, mPhone3));
        mPhone3.addTextChangedListener(new TextLengthWatcher(4, mEmail));
    }

    /**
     * Initializes spinners with hints.
     */
    private void setSpinnerContent() {
        ArrayAdapter<CharSequence> genderAdapter =
                ArrayAdapter.createFromResource(getActivity(),
                                                R.array.gender_array,
                                                android.R.layout.simple_spinner_item);
        mGenderSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        genderAdapter,
                        R.layout.gender_spinner_row_nothing_selected,
                        getActivity()));

        ArrayAdapter<CharSequence> ethnicityAdapter =
                ArrayAdapter.createFromResource(getActivity(),
                                                R.array.ethnicity_array,
                                                android.R.layout.simple_spinner_item);
        mEthnicitySpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        ethnicityAdapter,
                        R.layout.ethnicity_spinner_row_nothing_selected,
                        getActivity()));

        ArrayAdapter<CharSequence> languageAdapter =
                ArrayAdapter.createFromResource(getActivity(),
                                                R.array.language_array,
                                                android.R.layout.simple_spinner_item);
        mLanguageSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        languageAdapter,
                        R.layout.language_spinner_row_nothing_selected,
                        getActivity()));
    }

    /**
     * Prepopulates the form if there is a search result in shared preferences.
     */
    private void prepopulateForm() {
        String preferencesFile = SearchResultsFragment.SEARCH_RESULT;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(preferencesFile, 0);

        if (!sharedPreferences.getBoolean("Searched", false)) {
            return;
        }

        String ssNum = sharedPreferences.getString("SS_Num", "");
        String firstName = sharedPreferences.getString("FirstName", "");
        String lastName = sharedPreferences.getString("LastName", "");
        String phone = sharedPreferences.getString("Phone", "");
        String birthdateString = sharedPreferences.getString("Birthdate", "");
        String email = sharedPreferences.getString("Email", "");
        String gender = sharedPreferences.getString("Gender", "");
        String ethnicity = sharedPreferences.getString("Ethnicity", "");
        String language = sharedPreferences.getString("Language", "");
        boolean glbt = sharedPreferences.getBoolean("GLBT", false);
        boolean foster = sharedPreferences.getBoolean("Foster", false);
        boolean military = sharedPreferences.getBoolean("Military", false);

        if(!firstName.equals("null")) mFirstName.setText(firstName);
        if(!lastName.equals("null")) mLastName.setText(lastName);
        if(!email.equals("null")) mEmail.setText(email);
        if(ssNum.length() == 9) {
            mSSN1.setText("xxx");
            mSSN2.setText("xx");
            mSSN3.setText(ssNum.substring(5));
        }
        if(!phone.equals("null") && phone.length() == 10) {
            mPhone1.setText(phone.substring(0, 3));
            mPhone2.setText(phone.substring(3, 6));
            mPhone3.setText(phone.substring(6));
        }
        if (!birthdateString.equals("null")) {
            Log.d("Date", birthdateString);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date birthdateDate = df.parse(birthdateString);
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(birthdateDate);
                mDay.setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
                mMonth.setText(Integer.toString(calendar.get(Calendar.MONTH)+1));
                mYear.setText(Integer.toString(calendar.get(Calendar.YEAR)));

            } catch (ParseException e) {
                Log.e("Birthday Format Error", e.toString());
            }
        }

        if (!gender.equals("null")) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) ((NothingSelectedSpinnerAdapter) mGenderSpinner.getAdapter()).getSpinnerAdapter();
            int position = adapter.getPosition(gender);
            if (position != -1) {
                mGenderSpinner.setSelection(position+1);
            }
        }

        if (!ethnicity.equals("null")) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) ((NothingSelectedSpinnerAdapter) mEthnicitySpinner.getAdapter()).getSpinnerAdapter();
            int position = adapter.getPosition(ethnicity);
            if (position != -1) {
                mEthnicitySpinner.setSelection(position+1);
            }
        }

        if (!language.equals("null")) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) ((NothingSelectedSpinnerAdapter) mLanguageSpinner.getAdapter()).getSpinnerAdapter();
            int position = adapter.getPosition(language);
            if (position != -1) {
                mLanguageSpinner.setSelection(position+1);
            }
        }

        mGLBTCheckbox.setChecked(glbt);
        mMilitaryCheckbox.setChecked(military);
        mFosterCheckbox.setChecked(foster);
    }

    private class OnPersonalInfoContinueClickListener extends OnContinueClickListener {

        public OnPersonalInfoContinueClickListener(Context context, Fragment currFrag, ViewGroup currLayout, Fragment nextFrag, String nextFragName) {
            super(context, currFrag, currLayout, nextFrag, nextFragName);
        }

        /**
         * When continue button is clicked, updates SharedPreferences and loads the next fragment.
         * SelectServicesFragment is handled by a different method because it has dynamically
         * added checkbox views.
         */
        @Override
        public void onClick(View view) {
            if (!validateFields()) {
                return;
            }
            super.onClick(view);
        }
    }

    /**
     * Corrects fields within the form and does a validation check
     * @return false if some fields need correcting, true if okay
     */
    private boolean validateFields() {
        return validateSSN() && validateBirthday() && validateEmail();
    }

    private boolean validateSSN(){
        final String SSN = mSSN1.getText().toString()
                        + mSSN2.getText().toString()
                        + mSSN3.getText().toString();

        //only allow either empty or properly formatted SSN
        if (SSN.length() == 0 || SSN.length() == 9) { return true; }
        Toast.makeText(getActivity(),
                "Please enter in either a valid SSN or none at all",
                Toast.LENGTH_LONG).show();
        return false;
    }

    private boolean validateBirthday() {
        String monthText = mMonth.getText().toString();
        String dayText = mDay.getText().toString();
        String yearText = mYear.getText().toString();
        String birthdate = monthText + dayText + yearText;

        //do not use validation checks if birthday is completely empty
        if (birthdate.length() != 0) {
            if (!(yearText.length() == 4 || yearText.length() == 0)) {
                Toast.makeText(getActivity(),
                        "Please enter in 4 digits for the birthday year field",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            if (!(monthText.matches("^[0-9]*$") &&
                    dayText.matches("^[0-9]*$") &&
                    yearText.matches("^[0-9]*$"))) {
                Toast.makeText(getActivity(),
                        "Please only enter in numerical digits for the birthday fields",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            if (mMonth.getText().length() == 1) {
                mMonth.setText("0" + mMonth.getText());
            }

            if (mDay.getText().length() == 1) {
                mDay.setText("0" + mDay.getText());
            }

            //tests validity of date
            try {
                SimpleDateFormat df = new SimpleDateFormat("MMddyyyy");
                //illegal dates are not allowed, even if correctly formatted (e.g. 2-30-1980)
                df.setLenient(false);
                df.parse(birthdate);
            } catch (ParseException e) {
                Toast.makeText(getActivity(),
                        "Please enter in a valid date",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private boolean validateEmail() {
        final String emailText = mEmail.getText().toString();
        if (emailText.length() != 0) {
            if (!isValidEmail(emailText)) {
                Toast.makeText(getActivity(),
                        "Please enter a valid email address",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
