package phc.android;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
    /* Listener for when the continue button is clicked */
    OnContinueClickListener mContinueClickListener;
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

        Button continueButton = (Button) view.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                EventRegistrationFragment newFragment = new EventRegistrationFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.registration_fragment_container, newFragment, getResources().getString(R.string.sidebar_event_info));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

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

        mNeighborhoodSpinner = (Spinner) view.findViewById(R.id.neighborhood_spinner);
        String[] neighborhoods = getResources().getStringArray(R.array.neighborhood_array);
        ArrayAdapter<String> mNeighborhoodAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, neighborhoods);
        mNeighborhoodSpinner.setAdapter(mNeighborhoodAdapter);
        mNeighborhoodSpinner.setSelection(mNeighborhoodAdapter.getCount());


        mGenderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
        String[] genders = getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<String> mGenderAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, genders);
        mGenderSpinner.setAdapter(mGenderAdapter);
        mGenderSpinner.setSelection(mGenderAdapter.getCount());

        mEthnicitySpinner = (Spinner) view.findViewById(R.id.ethnicity_spinner);
        String[] ethnicities = getResources().getStringArray(R.array.ethnicity_array);
        ArrayAdapter<String> mEthnicityAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, ethnicities);
        mEthnicitySpinner.setAdapter(mEthnicityAdapter);
        mEthnicitySpinner.setSelection(mEthnicityAdapter.getCount());


        mLanguageSpinner = (Spinner) view.findViewById(R.id.language_spinner);
        String[] languages = getResources().getStringArray(R.array.language_array);
        ArrayAdapter<String> mLanguageAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, languages);
        mLanguageSpinner.setAdapter(mLanguageAdapter);
        mLanguageSpinner.setSelection(mLanguageAdapter.getCount());
    }
}
