package phc.android;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class RegistrationScannerConfirmationFragment extends ScannerConfirmationFragment {

    /* Tag for logs and fragment code */
    public final static String TAG = "RegistrationScannerConfirmationFragment";

    /* Name to store code in saved preferences */
    private final String mName = "qr_code";

    /* Preference editor for saved preferences */
    private PreferenceEditor mPreferenceEditor;

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

    /**
     * Separate method for setting up view so that this
     * functionality can be overriden by a subclass.
     * Override to setup mPreferenceEditor
     * @param view is passed in by onCreateView()
     */
    @Override
    protected View setupView(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragment_registration_scanner_confirmation, container, false);
        mPreferenceEditor = new PreferenceEditor(getActivity().getApplicationContext());

        mScanResultView = (TextView) view.findViewById(R.id.scan_result);
        mScanResultView.setText(mScanResult);

        mRetryButton = (Button) view.findViewById(R.id.retry_scan);
        mRetryButton.setOnClickListener(new RetryListener());

        mConfirmButton = (Button) view.findViewById(R.id.confirm_scan);
        mConfirmButton.setOnClickListener(new SubmitListener(getActivity()));
        return view;
    }

    /**
     * Used when the user wants to return to scan
     */
    protected class RetryListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            showFailureToast();
            displayNextFragment(new RegistrationScannerFragment(), RegistrationScannerFragment.TAG);
        }
    }

    /**
     * Used to confirm the scan result.
     * Uses OnSubmit
     */
    protected class SubmitListener extends OnSubmitClickListener implements View.OnClickListener {

        public SubmitListener(Context context) {
            super(context);
        }

        @Override
        public void onClick(View view) {
            /* shows success toast */
            recordScan();
            super.onClick(view);
        }
    }

    /**
     * Records the scan result in shared preferences
     * and displays a success toast.
     */
    @Override
    protected void recordScan() {
        mPreferenceEditor.storeScanResult(mScanResult.toString());
        showSuccessToast();
    }

    /**
     * Override to replace registration_fragment_container
     * @param nextFrag Fragment to display next
     * @param fragName String fragment name
     */
    @Override
    protected void displayNextFragment(Fragment nextFrag, String fragName) {
        FragmentTransaction transaction =
                getActivity().getFragmentManager().beginTransaction();
        transaction.replace(R.id.registration_fragment_container, nextFrag, fragName);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void resumeHelper() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getString(R.string.sidebar_scan_code)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
    }
}

