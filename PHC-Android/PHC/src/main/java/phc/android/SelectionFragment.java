package phc.android;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectionFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selection, container, false);

        ImageButton newUserButton = (ImageButton) view.findViewById(R.id.new_user_button);
        newUserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AccountRegistrationFragment newFragment = new AccountRegistrationFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.registration_fragment_container, newFragment, getResources().getString(R.string.sidebar_personal_info));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        ImageButton returningUserButton = (ImageButton) view.findViewById(R.id.returning_user_button);
        returningUserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SearchFragment newFragment = new SearchFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.registration_fragment_container, newFragment, getResources().getString(R.string.sidebar_personal_info));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
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
        super.onResume();
    }
}