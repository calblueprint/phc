package phc.android;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class SideBarFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_side_bar, container, false);

        // Grab list of all services offered for the current event from Salesforce DB
        Resources res = getResources();
        String[] elements = res.getStringArray(R.array.sidebar);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.sidebar_list);

        // Dynamically add sidebar buttons to sidebar
        for (int i = 0; i < elements.length; i++) {
            Button button = new Button(getActivity());
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setText(elements[i]);
            button.setTextColor(getResources().getColor(R.color.button_text_color));
            button.setTextSize(getResources().getDimensionPixelSize(R.dimen.button_text_size));
            button.setTag(elements[i]);
            ll.addView(button);

            View horizRule = new View(getActivity());
            horizRule.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.horizontal_rule_height)));
            horizRule.setBackgroundColor(getResources().getColor(R.color.gray_sidebar));
            ll.addView(horizRule);
        }

        return view;
    }
}