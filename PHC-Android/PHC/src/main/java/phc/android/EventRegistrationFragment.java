package phc.android;

import android.app.Fragment;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * EventRegistrationFragment is the event registration form for all clients
 * and includes fields that might have changed since the last event.
 */
public class EventRegistrationFragment extends Fragment{
    /* Continue button */
    Button mContinueButton;
    /* Listener for when the continue button is clicked */
    OnContinueClickListener mContinueClickListener;
    /* Spinners for multiple choice questions */
    Spinner mNeighborhoodSpinner;

    /**
     * On creation of the fragment, sets content for spinners and an onClickListener
     * for the continue button.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_registration, container, false);
        setSpinnerContent(view);
        setOnContinueClickListener(view);
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

    /**
     * Sets multiple choice options for each spinner.
     */
    protected void setSpinnerContent(View view){
        mNeighborhoodSpinner = (Spinner) view.findViewById(R.id.spinner_neighborhood);
        ArrayAdapter<CharSequence> neighborhoodAdapter = ArrayAdapter.createFromResource
                (getActivity(), R.array.neighborhood_array, android.R.layout.simple_spinner_item);
        neighborhoodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNeighborhoodSpinner.setAdapter(neighborhoodAdapter);
    }

    /**
     * Sets on-click listener for the continue button
     * and sets the next fragment to the SelectServicesFragment.
     */
    protected void setOnContinueClickListener(View view) {
        mContinueButton = (Button) view.findViewById(R.id.button_event_continue);
        mContinueClickListener =
                new OnContinueClickListener(getActivity(), new SelectServicesFragment());
        mContinueButton.setOnClickListener(mContinueClickListener);
    }
}
