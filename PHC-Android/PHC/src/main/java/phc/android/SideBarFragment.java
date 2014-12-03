package phc.android;

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


public class SideBarFragment extends RegistrationFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_side_bar, container, false);

        // Grab list of all services offered for the current event from Salesforce DB
        Resources res = getResources();
        final String[] elements = res.getStringArray(R.array.sidebar);
        LinearLayout sidebarList = (LinearLayout) view.findViewById(R.id.sidebar_list);

        // Dynamically add sidebar buttons to sidebar
        for (String element : elements) {
            final String tag = element; // set as final, so we can use them in the onClick listener

            Button button = new Button(getActivity());
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setText(tag);
            button.setTextColor(getResources().getColor(R.color.button_text_color));
            button.setTextSize(getResources().getDimensionPixelSize(R.dimen.button_text_size));
            button.setTag(tag);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fragMan = getFragmentManager();
                    Fragment newFragment = fragMan.findFragmentByTag(tag);

                    if (newFragment != null) {
                        FragmentTransaction transaction = fragMan.beginTransaction();
                        transaction.replace(R.id.registration_fragment_container, newFragment, tag);
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
            horizRule.setBackgroundColor(getResources().getColor(R.color.gray_sidebar));
            sidebarList.addView(horizRule);
        }

        return view;
    }
}
