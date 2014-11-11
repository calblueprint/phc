package phc.android;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * AccountRegistrationFragment is the registration form for all new clients
 * and contains personal account questions that do not change over time.
 */
public class AccountRegistrationFragment extends Fragment{
    /* Continue button */
    Button mContinueButton;
    /* Spinners for multiple choice questions */
    Spinner mGenderSpinner, mEthnicitySpinner, mLanguageSpinner, mNeighborhoodSpinner;

    /**
     * On creation of the fragment, sets content for spinners and an onClickListener
     * for the continue button.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_registration, container, false);
        setSpinnerContent(view);

        mContinueButton = (Button) view.findViewById(R.id.button_account_continue);
        mContinueButton.setOnClickListener(
                new OnContinueClickListener(getActivity(), new EventRegistrationFragment()));
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

    private void setSpinnerContent(View view){

        mNeighborhoodSpinner = (Spinner) view.findViewById(R.id.spinner_neighborhood);
        String[] neighborhoods = getResources().getStringArray(R.array.neighborhood_array);
        ArrayAdapter<String> neighborhoodAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, neighborhoods);
        mNeighborhoodSpinner.setAdapter(neighborhoodAdapter);
        mNeighborhoodSpinner.setSelection(neighborhoodAdapter.getCount());

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
