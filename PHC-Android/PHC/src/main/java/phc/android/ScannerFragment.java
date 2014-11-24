package phc.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class ScannerFragment extends android.app.Fragment {

    public final static String TAG = "ScannerFragment";
    /* holds the result of the scan */
    protected String mScanResult;
    /* displays last scan result */
    protected TextView mScanConfirmation;
    /* button to go to scanner */
    protected Button mScanButton;
    /* button to confirm result */
    protected Button mConfirmButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        setupView(view);
        return view;
    }

    protected void setupView(View view) {
        mScanConfirmation = (TextView) view.findViewById(R.id.scan_result);
        mScanButton = (Button) view.findViewById(R.id.start_scan);
        mScanButton.setOnClickListener(new ScanListener());
        mConfirmButton = (Button) view.findViewById(R.id.confirm_scan);
        mConfirmButton.setOnClickListener(new ConfirmListener());
        mConfirmButton.setVisibility(View.GONE);
    }

    protected class ReturnListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mScanConfirmation.setText("");
            resetState();
        }
    }
    protected class ScanListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            startScan();
        }
    }
    protected class ConfirmListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            recordScan();
            showSuccessToast();
            resetState();
        }
    }

    protected void showSuccessToast() {
        Context c = getActivity().getApplicationContext();
        CharSequence message = "Scan successfully recorded";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

    }

    protected void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(reqCode, resCode, data);
        mScanResult = result.getContents();
        confirmScan();
    }
    /**
     * Called to handle a valid QR code after it has
     * been scanned and decoded.
     *
     * @param result is the decoded string
     */
    protected void handleSuccessfulResult(String result) {

    }

    /**
     * Called when an QR code could not be successfully read
     */
    protected void handleInvalidResult() {

    }

    protected void confirmScan() {
        mScanConfirmation.setText("Last successful scan result was\n: " + mScanResult);
        mConfirmButton.setVisibility(View.VISIBLE);
        mScanButton.setText("Return");
        mScanButton.setOnClickListener(new ReturnListener());
    }

    protected void recordScan() {

    }

    protected void resetState() {
        mConfirmButton.setVisibility(View.GONE);
        mScanButton.setText("Click to Scan");
        mScanButton.setOnClickListener(new ScanListener());
    }

    /**
     * Lets the calling activity know that a valid
     * QR code was received. This valid code may be
     * overwritten multiple times before it is
     * returned to the calling activity.
     *
     * @param result is the decoded string
     * @return no return value, uses Intent to communicate
     */
    private void returnSuccessfulResult(String result) {
        Intent scanResult = new Intent();
        scanResult.putExtra("scan_result", result);
        getActivity().setResult(getActivity().RESULT_OK, scanResult);
        getActivity().finish();
    }

    /**
     * Lets the calling activity know that a valid
     * QR code was not received before the user
     * returned using the back button.
     *
     * @return no return value, uses Intent to communicate
     */
    private void returnCanceledResult() {
        Intent scanResult = new Intent();
        getActivity().setResult(getActivity().RESULT_CANCELED, scanResult);
        getActivity().finish();
    }
}

