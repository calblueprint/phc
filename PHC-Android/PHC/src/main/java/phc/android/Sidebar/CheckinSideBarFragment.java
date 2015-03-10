package phc.android.Sidebar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import phc.android.Checkin.CheckinFragment;
import phc.android.R;


public class CheckinSideBarFragment extends CheckinFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkin_side_bar, container, false);

        // Grab list of all services offered for the current event from Salesforce DB
        Resources res = getResources();
        final String[] elements = res.getStringArray(R.array.checkin_sidebar);
        LinearLayout sidebarList = (LinearLayout) view.findViewById(R.id.checkin_sidebar);

        // Dynamically add sidebar buttons to sidebar
        for (String element : elements) {
            final String tag = element; // set as final, so we can use them in the onClick listener

            Button button = new Button(getActivity());
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setText(tag);
            button.setTextColor(getResources().getColor(R.color.black));
            button.setTextSize(getResources().getDimensionPixelSize(R.dimen.sidebar_button_size));
            button.setTag(tag);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fragMan = getFragmentManager();
                    Fragment newFragment = fragMan.findFragmentByTag(tag);

                    if (newFragment != null) {
                        FragmentTransaction transaction = fragMan.beginTransaction();
                        transaction.replace(R.id.checkin_fragment_container, newFragment, tag);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            });
            sidebarList.addView(button);

            View horizRule = new View(getActivity());
            horizRule.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.horizontal_rule_height)));
            sidebarList.addView(horizRule);
        }

        return view;
    }
}
