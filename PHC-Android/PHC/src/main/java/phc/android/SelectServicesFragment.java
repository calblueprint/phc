package phc.android;

import android.app.ActionBar;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

public class SelectServicesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_services, container, false);

        // Grab list of all services offered for the current event from Salesforce DB
        Resources res = getResources();
        String[] services = res.getStringArray(R.array.services_array);

        // Dynamically populate linear layout with checkboxes for each service
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.services_list);
        for(int i = 0; i < services.length; i++){
            CheckBox cb = new CheckBox(getActivity());
            cb.setLayoutParams(new LinearLayout.LayoutParams(
                    R.dimen.input_text_width,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            cb.setText(services[i]);
            ll.addView(cb);
        }

        return view;
    }
}