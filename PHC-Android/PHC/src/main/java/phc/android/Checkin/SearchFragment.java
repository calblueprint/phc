package phc.android.Checkin;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import phc.android.Checkin.CheckinActivity;
import phc.android.Checkin.CheckinFragment;
import phc.android.Checkin.SearchResultsFragment;
import phc.android.R;

/**
 * SearchFragment is launched on successful submission of a client's form data,
 * and allows the user to go back to activity_checkin another client.
 */


public class SearchFragment extends CheckinFragment {

    public static final String SEARCH_PARAMETERS = "SearchParametersFile";
    /** Parent Activity **/
    private CheckinActivity mParent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Button submitSearchButton = (Button) view.findViewById(R.id.button_submit_search);
        final EditText firstName = (EditText)view.findViewById(R.id.first_name);
        final EditText lastName = (EditText) view.findViewById(R.id.last_name);
        submitSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Save parameters to SharedPrefs to access from the next activity.
                SharedPreferences searchPrameters = getActivity().getSharedPreferences(SEARCH_PARAMETERS, 0);
                SharedPreferences.Editor searchParametersEditor = searchPrameters.edit();
                searchParametersEditor.putString("firstName", firstName.getText().toString());
                searchParametersEditor.putString("lastName", lastName.getText().toString());
                searchParametersEditor.commit();

                SearchResultsFragment newFragment = new SearchResultsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.checkin_fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        mParent = (CheckinActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getText(R.string.sidebar_search)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
        mParent.setCurrentState(CheckinActivity.RegistrationState.NEW_USER);
        super.onResume();
    }

}
