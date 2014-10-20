package phc.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AccountRegistrationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_registration, container, false);
        setSpinnerContent(view);
        return view;
    }

    private void setSpinnerContent(View view){
        Spinner gender_spinner = (Spinner) view.findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> gender_adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(gender_adapter);

        Spinner ethnicity_spinner = (Spinner) view.findViewById(R.id.ethnicity_spinner);
        ArrayAdapter<CharSequence> ethnicity_adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.ethnicity_array, android.R.layout.simple_spinner_item);
        ethnicity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ethnicity_spinner.setAdapter(ethnicity_adapter);

        Spinner language_spinner = (Spinner) view.findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> language_adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.language_array, android.R.layout.simple_spinner_item);
        language_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_spinner.setAdapter(language_adapter);
    }
}