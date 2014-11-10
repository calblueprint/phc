package phc.android;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * SuccessFragment is launched on successful submission of a client's form data,
 * and allows the user to go back to register another client.
 */
public class SuccessFragment extends Fragment {
    /* Submit button. */
    private Button mRegisterAnotherButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success, container, false);
        setOnRegisterAnotherClickListener(view);
        return view;
    }

    @Override
    public void onResume() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getText(R.string.sidebar_confirmation)))) {
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
     * Creates an OnClickListener for the "Register Another Client" button,
     * which calls a new instance of RegisterActivity.
     */
    protected void setOnRegisterAnotherClickListener(View view) {
        mRegisterAnotherButton = (Button) view.findViewById(R.id.button_register_another);
        mRegisterAnotherButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), RegisterActivity.class);
                        getActivity().startActivity(intent);
                    }
        });
    }
}
