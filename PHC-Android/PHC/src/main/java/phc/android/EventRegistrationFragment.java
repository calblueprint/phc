package phc.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EventRegistrationFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_registration, container, false);
        setSpinnerContent(view);
        return view;
    }

    private void setSpinnerContent(View view){
        Spinner neighborhood_spinner = (Spinner) view.findViewById(R.id.neighborhood_spinner);
        ArrayAdapter<CharSequence> neighborhood_adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.neighborhood_array, android.R.layout.simple_spinner_item);
        neighborhood_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        neighborhood_spinner.setAdapter(neighborhood_adapter);
    }
}