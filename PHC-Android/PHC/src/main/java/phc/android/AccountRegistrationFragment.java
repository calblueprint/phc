package phc.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AccountRegistrationFragment extends Fragment {
    Spinner mGenderSpinner, mEthnicitySpinner, mLanguageSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_registration, container, false);
        setSpinnerContent(view);
        return view;
    }

    private void setSpinnerContent(View view){
        mGenderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> mGenderAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        mGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenderSpinner.setAdapter(mGenderAdapter);

        mEthnicitySpinner = (Spinner) view.findViewById(R.id.ethnicity_spinner);
        ArrayAdapter<CharSequence> ethnicity_adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.ethnicity_array, android.R.layout.simple_spinner_item);
        ethnicity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEthnicitySpinner.setAdapter(ethnicity_adapter);

        mLanguageSpinner = (Spinner) view.findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> mLanguageAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.language_array, android.R.layout.simple_spinner_item);
        mLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLanguageSpinner.setAdapter(mLanguageAdapter);
    }
}