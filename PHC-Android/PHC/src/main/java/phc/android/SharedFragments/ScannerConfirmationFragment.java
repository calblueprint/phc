package phc.android.SharedFragments;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import phc.android.Networking.RequestManager;
import phc.android.R;


public class ScannerConfirmationFragment extends android.app.Fragment {

    /* Name for logs and fragment transaction code */
    public final static String TAG = "ScannerConfirmationFragment";

    /* Displays last scan result */
    protected TextView mScanResultView;

    /* Holds last scan result */
    protected String mScanResult;

    /* Button to go back to scanner fragment */
    protected Button mRetryButton;

    /* Button to confirm result */
    protected Button mConfirmButton;

    protected static RequestManager sRequestManager;
    protected static RequestQueue sRequestQueue;

    /* Shared Preferences */
    protected static final String USER_AUTH_PREFS_NAME = "UserKey";
    protected SharedPreferences mUserPreferences;
    protected String mUserId;
    protected String mAuthToken;

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
            mScanResult = savedInstanceState.getCharSequence("scan_result").toString();
            mManualInput = savedInstanceState.getBoolean("manual_input");
        } else {
            mScanResult = getArguments().getCharSequence("scan_result").toString();
            mManualInput = getArguments().getBoolean("manual_input");
        }
        String prompt;
        if (mManualInput) {
            prompt = getString(R.string.text_input_confirmation);
        } else {
            prompt = getString(R.string.text_scan_confirmation);
        }
        mScanResultView.setText(Html.fromHtml(prompt + "<br /><br />" + "<b><big><big>" + mScanResult + "</b></big></big><br />"));

        //Set up Volley request framework
        sRequestQueue = Volley.newRequestQueue(getActivity());
        sRequestManager = new RequestManager(TAG, sRequestQueue);

        // Get userId and authToken
        mUserPreferences = getActivity().getSharedPreferences(USER_AUTH_PREFS_NAME,
                Context.MODE_PRIVATE);
        mUserId = mUserPreferences.getString("user_id", null);
        mAuthToken = mUserPreferences.getString("auth_token", null);

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
    public class RetryListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            retry();
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
     * Used to confirm the scan result.
     */
    public class ConfirmListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            confirm();
        }
    }

    /**
     * To be overriden by the subclass.
     * Each type of ScannerConfirmation Fragment will perform a different operation
     * when the "confirm" button is pressed.
     */
    protected void confirm() {}

    /**
     * Displays a success message at the bottom of
     * the screen.
     */
    protected void showSuccessToast() {
        Context c = getActivity().getApplicationContext();
        CharSequence message = getResources().getString(R.string.toast_scan_success);
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
        CharSequence message = getResources().getString(R.string.toast_scan_failure);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
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
}
