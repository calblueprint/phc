package phc.android.Checkout;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import phc.android.R;
import phc.android.Services.ScannerFragment;


/**
 * User enters or scans code, leading to CheckoutFormFragment
 */
public class CheckoutScannerFragment extends ScannerFragment {
    /* Name for logs and fragment transaction code */
    public final static String TAG = "CheckoutScannerFragment";


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        /* Inflate the layout for this fragment and set up view */
        View view = setupView(inflater, container);
        return view;
    }

    @Override
    protected View setupView(LayoutInflater inflater, ViewGroup container){
        View view = inflater.inflate(R.layout.fragment_checkout_scanner, container, false);

        mScanButton = (Button) view.findViewById(R.id.checkout_start_scan);
        mScanButton.setOnClickListener(new ScannerFragment.ScanListener());

        setupButtons(view);

        return view;
    }

    @Override
    protected void setupButtons(View view){

        mCodeInput = (EditText) view.findViewById(R.id.checkout_scanner_code_input);
        mCodeInput.addTextChangedListener(new ScannerFragment.InputTextWatcher());

        mCodeInputSubmitButton = (Button) view.findViewById(R.id.checkout_scanner_submit);
        mCodeInputSubmitButton.setOnClickListener(new ScannerFragment.InputSubmitListener());
        setInputSubmitButton();
    }


    /**
     * Sets up the view for the user to confirm
     * the scanned code.
     */
    @Override
    protected void confirmScan(CharSequence scanResult, boolean manualInput) {
        /* Can use bundle passed in, or must create new? */
        Bundle args = new Bundle();
        args.putCharSequence("scan_result", scanResult);
        args.putBoolean("manual_input", manualInput);

        Fragment confFrag = new CheckoutFormFragment();
        confFrag.setArguments(args);
        displayNextFragment(confFrag, CheckoutFormFragment.TAG);
    }

    /**
     * Brings up another fragment when this fragment
     * is complete
     * @param nextFrag Fragment to display next
     * @param fragName String fragment names
     */
    @Override
    protected void displayNextFragment(Fragment nextFrag, String fragName) {
        FragmentTransaction transaction =
                (getActivity()).getFragmentManager().beginTransaction();
        transaction.replace(R.id.checkout_activity_container, nextFrag, fragName);
        transaction.addToBackStack(fragName);
        transaction.commit();
    }



    // don't need?
    @Override
    protected void resumeHelper(){
        ;
    }


}
