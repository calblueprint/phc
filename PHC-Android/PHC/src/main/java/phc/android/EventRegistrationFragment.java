package phc.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EventRegistrationFragment extends Fragment {
    Spinner mNeighborhoodSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_registration, container, false);
        setSpinnerContent(view);
        return view;
    }

    private void setSpinnerContent(View view){
        mNeighborhoodSpinner = (Spinner) view.findViewById(R.id.neighborhood_spinner);
        ArrayAdapter<CharSequence> mNeighborhoodAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.neighborhood_array, android.R.layout.simple_spinner_item);
        mNeighborhoodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNeighborhoodSpinner.setAdapter(mNeighborhoodAdapter);
    }
}