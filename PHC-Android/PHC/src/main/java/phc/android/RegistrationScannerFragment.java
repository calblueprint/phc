package phc.android;


import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegistrationScannerFragment extends ScannerFragment {

    private Button mContinueButton;
    private PreferenceEditor mPreferenceEditor;
    private final String mName = "qr_code";

    /**
     * Used to store a scan result in Shared Preferences
     */
    private class PreferenceEditor extends SharedPreferenceEditorListener {
        public PreferenceEditor(Context context) {
            super(context);
        }

        public void storeScanResult(String result) {
            mUserInfoEditor.putString(mName, result);
            mUserInfoEditor.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_scanner, container, false);

        mPreferenceEditor = new PreferenceEditor(getActivity().getApplicationContext());

        mContinueButton = (Button) view.findViewById(R.id.button_services_continue);
        mContinueButton.setEnabled(false);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.registration_fragment_container, new SuccessFragment(), getResources().getString(R.string.sidebar_confirmation));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        setupView(view);

        return view;
    }

    protected class ContinueListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            recordScan();
            showSuccessToast();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.replace(R.id.registration_fragment_container, new SuccessFragment(), getResources().getString(R.string.sidebar_confirmation));
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
    @Override
    protected void setupView(View view) {
        mScanConfirmation = (TextView) view.findViewById(R.id.scan_result);
        mScanButton = (Button) view.findViewById(R.id.start_scan);
        mScanButton.setOnClickListener(new ScanListener());
        mConfirmButton = (Button) view.findViewById(R.id.button_services_continue);         mConfirmButton.setOnClickListener(new ContinueListener());
        mConfirmButton.setEnabled(false);
    }

    @Override
    protected void confirmScan() {
        mScanConfirmation.setText("Last successful scan result was\n: " + mScanResult);
        mConfirmButton.setEnabled(true);
        mScanButton.setText("Return");
        mScanButton.setOnClickListener(new ReturnListener());
    }

    @Override
    protected void resetState() {
        mScanButton.setText("Click to Scan");
        mScanButton.setOnClickListener(new ScanListener());
    }

    /**
     * Called to handle a valid QR code after it has
     * been scanned and decoded.
     *
     * @param result is the decoded string
     */
    @Override
    protected void handleSuccessfulResult(String result) {
        mContinueButton.setEnabled(true);
        mPreferenceEditor.storeScanResult(result);
        super.handleSuccessfulResult(result);
    }

    /**
     * Called when an QR code could not be successfully read
     */
    @Override
    protected void handleInvalidResult() {
        super.handleInvalidResult();
    }

    /**
     * Called when activity is re opened.
     * Camera must be acquired again, and
     * the preview's camera handle should
     * be updated as well.
     */
    @Override
    public void onResume() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getText(R.string.sidebar_scan_code)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
        super.onResume();
    }

}
