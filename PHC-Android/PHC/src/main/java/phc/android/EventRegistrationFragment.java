package phc.android;

import android.app.Fragment;
import android.content.res.Resources;
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

import java.util.concurrent.atomic.AtomicInteger;


/**
 * EventRegistrationFragment is the event registration form for all clients
 * and includes fields that might have changed since the last event.
 */
public class EventRegistrationFragment extends Fragment{
    /* Continue button */
    private Button mContinueButton;
    /* Integer used to help generate unique IDs for the checkboxes. */
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private Spinner mNeighborhoodSpinner;

    /**
     * On creation of the fragment, sets content for spinners and an onClickListener
     * for the continue button.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_registration, container, false);

        // Grab list of all services offered for the current event from Salesforce DB
        // Dynamically populate linear layout with checkboxes for each service
        Resources res = getResources();

        setSpinnerContent(view);
        mContinueButton = (Button) view.findViewById(R.id.button_event_continue);
        mContinueButton.setOnClickListener(new OnContinueClickListener(getActivity(), new SelectServicesFragment(), getResources().getString(R.string.sidebar_services_info)));

        return view;
    }

    @Override
    public void onResume() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getText(R.string.sidebar_event_info)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
        super.onResume();
    }

    /**
     * Sets multiple choice options for each spinner.
     */
    protected void setSpinnerContent(View view){
        mNeighborhoodSpinner = (Spinner) view.findViewById(R.id.spinner_neighborhood);
        String[] neighborhoods = getResources().getStringArray(R.array.neighborhood_array);
        ArrayAdapter<String> neighborhoodAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, neighborhoods);
        mNeighborhoodSpinner.setAdapter(neighborhoodAdapter);
        mNeighborhoodSpinner.setSelection(neighborhoodAdapter.getCount());
    }
}
