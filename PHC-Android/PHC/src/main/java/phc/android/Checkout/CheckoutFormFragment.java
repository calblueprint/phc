package phc.android.Checkout;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import phc.android.Helpers.SuccessFragment;
import phc.android.R;

public class CheckoutFormFragment extends Fragment {

    /** Holds the result of the scan. */
    protected String mScanResult;
    /** The textview that holds the comment. */
    /** currently not used because comment box is not yet set up  **/
    private EditText mComment;
    /** Holds their input for experience rating 0-5 */
    private int mExperience;
    /** Holds manual code input. **/
    /** currently not used because not yet set up  **/
    private EditText mCodeInput;
    /** Used to set listener to detect when the user rates their experience **/
    private RadioGroup mRadioGroup;
    /** Used to set listener to detect when the user clicks submit **/
    private Button mCodeInputSubmitButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState){
        //Button scanQr = (Button) findViewById(R.id.exit_qr_scanner);
        //scanQr.setOnClickListener(new ScanListener())
        View view = inflater.inflate(R.layout.fragment_checkout_form, container, false);
        mComment = (EditText) view.findViewById(R.id.checkout_comment);
        mCodeInputSubmitButton = (Button) view.findViewById(R.id.button_submit);
        mCodeInputSubmitButton.setOnClickListener(new SubmitListener(getActivity()));
        mCodeInput = (EditText) view.findViewById(R.id.text_code);

        mRadioGroup = (RadioGroup) view.findViewById(R.id.checkout_radiogroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId){
                //Resource ids are not constants -> Can't use switch statements
                if (checkedId==R.id.checkout_radio_0){
                    mExperience = 0;
                }
                else if (checkedId==R.id.checkout_radio_1){
                    mExperience = 1;
                }
                else if (checkedId==R.id.checkout_radio_2){
                    mExperience = 2;
                }
                else if (checkedId==R.id.checkout_radio_3){
                    mExperience = 3;
                }
                else if (checkedId==R.id.checkout_radio_4){
                    mExperience = 4;
                }
                else if (checkedId==R.id.checkout_radio_5){
                    mExperience = 5;
                }
            }
        });


        return view;
    }


    /**
     * Used when the user submits their inputted code.
     */
    protected class SubmitListener implements View.OnClickListener{
        private Context mContext;

        public SubmitListener(Context context){
            mContext = context;
        }

        @Override
        public void onClick(View view){
            /**
             * Loads next fragment onto the current stack.
             */
            FragmentTransaction transaction =
                    ((Activity)mContext).getFragmentManager().beginTransaction();
                    SuccessFragment successFragment = new SuccessFragment();
                    successFragment.setType(SuccessFragment.SuccessType.CHECKOUT_SUCCESS);
                    transaction.replace(R.id.checkout_activity_container, successFragment);
            transaction.commit();
        }
    }


    /**
     * Used when the user wants to send an intent
     * to the scanner app.
     */
    protected class ScanListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            startScan();
        }
    }

    /**
     * Starts the BarcodeScanner app.
     */
    protected void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    /**
     * Used to retrieve the result from the
     * BarcodeScanner app.
     *
     * @param reqCode int request code
     * @param resCode int result code
     * @param data Intent containing the result data
     */
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(reqCode, resCode, data);
        mScanResult = result.getContents();
        if (mScanResult == null) {
            showFailureToast();
        } else {
            confirmScan();
        }
    }

    /** Shows toast if the QR Scan was not successful. */
    protected void showFailureToast() {
        CharSequence message = getResources().getString(R.string.scan_failure);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getActivity(), message, duration);
        toast.show();
    }

    /** Method that runs when a QR scan is successful. */
    protected void confirmScan() {
        CharSequence message = getResources().getString(R.string.scan_success);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getActivity(), message, duration);
        toast.show();
        // TODO: Load success fragment: Onclick listener that wipes comments, experience, and services
        // TODO:
        // mComment.setText("");
        // reloadSuccessFragment();
    }
}
