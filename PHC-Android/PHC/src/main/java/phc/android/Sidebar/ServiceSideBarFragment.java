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

import phc.android.R;
import phc.android.SharedFragments.ScannerFragment;


public class ServiceSideBarFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service_side_bar, container, false);

        Resources res = getResources();
        final String[] elements = res.getStringArray(R.array.services_sidebar);
        LinearLayout sidebarList = (LinearLayout) view.findViewById(R.id.services_sidebar_list);

        // Dynamically add sidebar buttons to sidebar
        for (String element : elements) {
            final String tag = element; // set as final, so we can use them in the onClick listener

            Button button = new Button(getActivity());
            /* give button an id to enable or disable */
            button.setId(Math.abs(tag.hashCode()));
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setText(tag);
            button.setTextColor(getResources().getColor(R.color.black));
            button.setTextSize(getResources().getDimension(R.dimen.sidebar_button_size));
            button.setTag(tag);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fragMan = getFragmentManager();
                    Fragment newFragment = fragMan.findFragmentByTag(tag);

                    if (newFragment == null) {
                        if (tag.equals(getResources().getString(R.string.sidebar_scan_code))) {
                            newFragment = new ScannerFragment();
                        } else if (tag.equals(getResources().getString(R.string.sidebar_confirm))) {
                            newFragment = new ScannerFragment();
                        }
                        ((ScannerFragment)newFragment).setType(ScannerFragment.FlowType.SERVICES);
                    }

                    FragmentTransaction transaction = fragMan.beginTransaction();
                    transaction.replace(R.id.service_fragment_container, newFragment, tag);
                    transaction.addToBackStack(null);
                    transaction.commit();
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

