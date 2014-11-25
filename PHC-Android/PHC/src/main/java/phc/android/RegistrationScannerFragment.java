package phc.android;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class RegistrationScannerFragment extends ScannerFragment {

    private Button mContinueButton;
    private String mScanResult;
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

    /**
     * Used when the user confirms the scan result
     * and chooses to continue
     */
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

    @Override
    protected void recordScan() {
        mPreferenceEditor.storeScanResult(mScanResult);
        showSuccessToast();
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(reqCode, resCode, data);
        mScanResult = result.getContents();
        if (mScanResult == null) {
            showFailureToast();
            resetState();
        } else {
            confirmScan();
        }
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
