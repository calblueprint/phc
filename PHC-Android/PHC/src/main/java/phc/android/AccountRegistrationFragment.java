package phc.android;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class AccountRegistrationFragment extends Fragment {
    Spinner mGenderSpinner, mEthnicitySpinner, mLanguageSpinner, mNeighborhoodSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_registration, container, false);
        setSpinnerContent(view);
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
        mNeighborhoodSpinner.setAdapter(mGenderAdapter);
        mGenderSpinner.setAdapter(mGenderAdapter);

        mEthnicitySpinner = (Spinner) view.findViewById(R.id.ethnicity_spinner);
        String[] ethnicities = getResources().getStringArray(R.array.ethnicity_array);
        ArrayAdapter<String> mEthnicityAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, ethnicities);
        mNeighborhoodSpinner.setAdapter(mEthnicityAdapter);
        mEthnicitySpinner.setAdapter(mEthnicityAdapter);


        mLanguageSpinner = (Spinner) view.findViewById(R.id.language_spinner);
        String[] languages = getResources().getStringArray(R.array.language_array);
        ArrayAdapter<String> mLanguageAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, languages);
        mNeighborhoodSpinner.setAdapter(mLanguageAdapter);
        mLanguageSpinner.setAdapter(mLanguageAdapter);
    }
}
