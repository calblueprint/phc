package phc.android.Checkin;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import phc.android.R;

public class SelectionFragment extends CheckinFragment {

    /** Parent Activity **/
    private CheckinActivity mParent;
    public static final String SEARCH_RESULT = "SEARCH_RESULT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("recreated", "recreated");
        View view = inflater.inflate(R.layout.fragment_selection, container, false);

        Button newUserButton = (Button) view.findViewById(R.id.new_user_button);
        newUserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PersonalInfoFragment newFragment = new PersonalInfoFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.checkin_fragment_container, newFragment, getResources().getString(R.string.sidebar_personal_info));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button returningUserButton = (Button) view.findViewById(R.id.returning_user_button);
        returningUserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SearchFragment newFragment = new SearchFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.checkin_fragment_container, newFragment, getResources().getString(R.string.sidebar_search));
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
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.checkin_sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getText(R.string.sidebar_selection)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }

        // When returning to this selection fragment, we assume we are a new user and clear returning user data
        mParent.setCurrentState(CheckinActivity.FormDataState.CLEAR_DATA);
        clearPreferences();

        super.onResume();
    }

    /** Clears all preferences saved from a previous user **/
    private void clearPreferences() {
        SharedPreferences sharedPreferences = mParent.getSharedPreferences(SEARCH_RESULT, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("SS_Num");
        editor.remove("FirstName");
        editor.remove("LastName");
        editor.remove("Phone");
        editor.remove("Birthdate");
        editor.remove("Email");
        editor.remove("Gender");
        editor.remove("Ethnicity");
        editor.remove("Language");
//        editor.remove("SFID");
        editor.apply();
    }
}
