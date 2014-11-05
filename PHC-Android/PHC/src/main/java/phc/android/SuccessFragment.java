package phc.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
