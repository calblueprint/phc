package phc.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * An activity for when a client exits an event and wants someone to follow up
 * on them.
 *
 * Created by howardchen on 12/1/14.
 */
public class ExitActivity extends Activity {

    /** Holds the result of the scan. */
    protected String mScanResult;
    /** The textview that holds the comment. */
    private EditText mComment;
    /** The checkboxview holds their input for good/bad experience */
    private CheckBox mExperience;
    /** The checkboxview holds their input for whether they found their services */
    private CheckBox mServices;
    /** Field and submit button for manual code input. **/
    protected EditText mCodeInput;
    protected Button mCodeInputSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
//        Button scanQr = (Button) findViewById(R.id.exit_qr_scanner);
//        scanQr.setOnClickListener(new ScanListener());
        mComment = (EditText) findViewById(R.id.exit_comment);
        mCodeInputSubmitButton = (Button) findViewById(R.id.button_submit);
        mCodeInputSubmitButton.setOnClickListener(new SubmitListener(this));
        mCodeInput = (EditText) findViewById(R.id.text_code);
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
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
            transaction.add(R.id.checkout_fragment_container, new CheckoutSuccessFragment());
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
        Context c = getApplicationContext();
        CharSequence message = getResources().getString(R.string.scan_failure);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }

    /** Method that runs when a QR scan is successful. */
    protected void confirmScan() {
        Context c = getApplicationContext();
        CharSequence message = getResources().getString(R.string.scan_success);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
        // TODO: Load success fragment: Onclick listener that wipes comments, experience, and services
        // TODO:
        // mComment.setText("");
        // reloadSuccessFragment();
    }
}
