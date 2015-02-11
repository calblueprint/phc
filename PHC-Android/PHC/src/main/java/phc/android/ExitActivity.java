package phc.android;

import android.app.Activity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        Button scanQr = (Button) findViewById(R.id.exit_qr_scanner);
        scanQr.setOnClickListener(new ScanListener());
        mComment = (EditText) findViewById(R.id.exit_comment);
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
        // Need to replace with function that wipes comments, experience, and services mComment.setText("");
        // reloadSuccessFragment();
    }
}
