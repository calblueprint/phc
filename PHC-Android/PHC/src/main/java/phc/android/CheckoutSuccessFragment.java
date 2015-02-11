package phc.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;


/**
 * CheckoutSuccessFragment is launched on successful submission of a client's code,
 * and allows the user to go back to Exit_Activity to checkout another client.
 */
public class CheckoutSuccessFragment extends Fragment {
    private Button mCheckoutAnotherButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout_success, container, false);
        //setSuccessName(view);
        setOnCheckoutAnotherClickListener(view);
        return view;
    }


    /**
     * Creates an OnClickListener for the "Checkout Another Client" button,
     * which calls a new instance of ExitActivity.
     */
    protected void setOnCheckoutAnotherClickListener(View view) {
        mCheckoutAnotherButton = (Button) view.findViewById(R.id.button_checkout_another);
        mCheckoutAnotherButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ExitActivity.class);
                        getActivity().startActivity(intent);
                    }
                });
    }


}
