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

        mNeighborhoodSpinner = (Spinner) view.findViewById(R.id.neighborhood_spinner);
        ArrayAdapter<CharSequence> mNeighborhoodAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.neighborhood_array, android.R.layout.simple_spinner_item);
        mNeighborhoodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNeighborhoodSpinner.setAdapter(mNeighborhoodAdapter);
    }
}
