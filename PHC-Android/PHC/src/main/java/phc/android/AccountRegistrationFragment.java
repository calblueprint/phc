package phc.android;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
public class AccountRegistrationFragment extends Fragment{
    private Button mContinueButton;
    private Spinner mGenderSpinner, mEthnicitySpinner, mLanguageSpinner;
    private EditText mMonth, mDay, mYear, mPhone1, mPhone2, mPhone3, mSSN1, mSSN2, mSSN3;

    /**
     * Set spinner content and continue button functionality.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_registration, container, false);

        addEditTextListeners(view);
        setSpinnerContent(view);
        mContinueButton = (Button) view.findViewById(R.id.button_account_continue);
        mContinueButton.setOnClickListener(
                new OnContinueClickListener(getActivity(), new EventRegistrationFragment(), getResources().getString(R.string.sidebar_event_info)));
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

    private void addEditTextListeners(View view){
        mMonth = (EditText) view.findViewById(R.id.birthday_month);
        mDay = (EditText) view.findViewById(R.id.birthday_day);
        mYear = (EditText) view.findViewById(R.id.birthday_year);
        mPhone1 = (EditText) view.findViewById(R.id.phone_1);
        mPhone2 = (EditText) view.findViewById(R.id.phone_2);
        mPhone3 = (EditText) view.findViewById(R.id.phone_3);
        mSSN1 = (EditText) view.findViewById(R.id.ssn_1);
        mSSN2 = (EditText) view.findViewById(R.id.ssn_2);
        mSSN3 = (EditText) view.findViewById(R.id.ssn_3);

        mMonth.addTextChangedListener(new TextLengthWatcher(2,mDay));
        mDay.addTextChangedListener(new TextLengthWatcher(2,mYear));

        mPhone1.addTextChangedListener(new TextLengthWatcher(3,mPhone2));
        mPhone2.addTextChangedListener(new TextLengthWatcher(3,mPhone3));

        mSSN1.addTextChangedListener(new TextLengthWatcher(3,mSSN2));
        mSSN2.addTextChangedListener(new TextLengthWatcher(2,mSSN3));
    }

    private void setSpinnerContent(View view){
        mGenderSpinner = (Spinner) view.findViewById(R.id.spinner_gender);
        String[] genders = getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<String> genderAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, genders);
        mGenderSpinner.setAdapter(genderAdapter);
        mGenderSpinner.setSelection(genderAdapter.getCount());

        mEthnicitySpinner = (Spinner) view.findViewById(R.id.spinner_ethnicity);
        String[] ethnicities = getResources().getStringArray(R.array.ethnicity_array);
        ArrayAdapter<String> ethnicityAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, ethnicities);
        mEthnicitySpinner.setAdapter(ethnicityAdapter);
        mEthnicitySpinner.setSelection(ethnicityAdapter.getCount());

        mLanguageSpinner = (Spinner) view.findViewById(R.id.spinner_language);
        String[] languages = getResources().getStringArray(R.array.language_array);
        ArrayAdapter<String> languageAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, languages);
        mLanguageSpinner.setAdapter(languageAdapter);
        mLanguageSpinner.setSelection(languageAdapter.getCount());
    }
}
