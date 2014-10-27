package phc.android;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EventRegistrationFragment extends Fragment {
    Spinner mNeighborhoodSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_registration, container, false);
        setSpinnerContent(view);
        return view;
    }

    @Override
    public void onResume() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
        for (int i=0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            if ((v.getTag() != null) && (v.getTag().equals(getResources().getText(R.string.sidebar_personal_info)))) {
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
        ArrayAdapter<CharSequence> mNeighborhoodAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.neighborhood_array, android.R.layout.simple_spinner_item);
        mNeighborhoodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNeighborhoodSpinner.setAdapter(mNeighborhoodAdapter);
    }
}