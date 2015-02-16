package phc.android;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ScannerConfirmationFragment extends android.app.Fragment {

    /* Name for logs and fragment transaction code */
    public final static String TAG = "ScannerConfirmationFragment";

    /* Displays last scan result */
    protected TextView mScanResultView;

    /* Holds last scan result */
    protected CharSequence mScanResult;

    /* Button to go back to scanner fragment */
    protected Button mRetryButton;

    /* Button to confirm result */
    protected Button mConfirmButton;

    /** Keeps track of whether the user
     * scanned a code or input it
     * manually from the ScannerFragment
     */
    protected boolean mManualInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /** Inflate the layout for this fragment and set up view. **/
        View view = setupView(inflater, container);

        /* Grab the last scan result from this fragment or the previous */
        if (savedInstanceState != null) {
            mScanResult = savedInstanceState.getCharSequence("scan_result");
            mManualInput = savedInstanceState.getBoolean("manual_input");
        } else {
            mScanResult = getArguments().getCharSequence("scan_result");
            mManualInput = getArguments().getBoolean("manual_input");
        }
        String prompt;
        if (mManualInput) {
            prompt = getString(R.string.input_confirmation_text);
        } else {
            prompt = getString(R.string.scan_confirmation_texts);
        }
        mScanResultView.setText(Html.fromHtml(prompt + "<br /><br />" + "<b><big><big>" + mScanResult + "</b></big></big><br />"));
        return view;
    }

    /**
     * Separate method for setting up view so that this
     * functionality can be overriden by a subclass.
     * @param inflater instantiates the XML layout
     * @param container is the view group this view belongs to
     */
    protected View setupView(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragment_scanner_confirmation, container, false);
        mScanResultView = (TextView) view.findViewById(R.id.scan_result);
        mScanResultView.setText(mScanResult);

        mConfirmButton = (Button) view.findViewById(R.id.confirm_scan);
        mConfirmButton.setOnClickListener(new ConfirmListener());

        mRetryButton = (Button) view.findViewById(R.id.retry_scan);
        mRetryButton.setOnClickListener(new RetryListener());
        return view;
    }

    /**
     * Used when the user wants to return to scanner fragment.
     */
    protected class RetryListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            retry();
        }
    }

    /**
     * Used to confirm the scan result.
     */
    protected class ConfirmListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            confirm();
        }
    }

    /**
     * Returns to scanner fragment and displays a
     * failure toast.
     */
    protected void retry() {
        showFailureToast();
        FragmentManager manager = getFragmentManager();
        manager.popBackStack(ScannerConfirmationFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Records the scan, returns to scanner fragment,
     * and displays a success toast.
     */
    protected void confirm() {
        recordScan();
        FragmentManager manager = getFragmentManager();
        manager.popBackStack(ScannerConfirmationFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Displays a success message at the bottom of
     * the screen.
     */
    protected void showSuccessToast() {
        Context c = getActivity().getApplicationContext();
        CharSequence message = getResources().getString(R.string.scan_success);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }

    /**
     * Displays a failure message at the bottom
     * of the screen.
     */
    protected void showFailureToast() {
        Context c = getActivity().getApplicationContext();
        CharSequence message = getResources().getString(R.string.scan_failure);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }

    /**
     * Records the scan result in shared preferences
     * and displays a success toast.
     */
    protected void recordScan() {
        ServiceActivity activity = (ServiceActivity) getActivity();
        activity.recordResult((String) mScanResult);
    }

    /**
     * Save the scan result when this fragment is
     * paused
     * @param outState Bundle passed in by Android
     */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("scan_result", mScanResult);
        outState.putBoolean("manual_input", mManualInput);
    }

    @Override
    public void onResume() {
        resumeHelper();
        super.onResume();
    }

    /**
     * Used so that a subclass can implement their own
     * resumeHelper() so method calls to onResume() will
     * execute this class's super.onResume()
     */
    protected void resumeHelper() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.services_sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getString(R.string.sidebar_confirm)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    /**
     * Called by the service activity's onBackPressed()
     * method. This method just calls retry(), everything
     * else is handled by the activity.
     */
    public void whenBackPressed() {
        retry();
    }

    /**
     * Lets the calling activity know that a valid
     * QR code was received. This valid code may be
     * overwritten multiple times before it is
     * returned to the calling activity.
     * Not currently used.
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
     * Not currently used.
     *
     * @return no return value, uses Intent to communicate
     */
    private void returnCanceledResult() {
        Intent scanResult = new Intent();
        getActivity().setResult(getActivity().RESULT_CANCELED, scanResult);
        getActivity().finish();
    }
}

