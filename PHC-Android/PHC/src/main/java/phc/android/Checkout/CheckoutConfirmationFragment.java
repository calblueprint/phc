package phc.android.Checkout;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import phc.android.R;
import phc.android.SharedFragments.ScannerConfirmationFragment;
import phc.android.SharedFragments.SuccessFragment;


public class CheckoutConfirmationFragment extends ScannerConfirmationFragment {

    /* Name for logs and fragment transaction code */
    public final static String TAG = "CheckoutConfirmationFragment";

    /* Displays comments */
    protected TextView mCommentsView;

    /* Displays experience */
    protected TextView mExperienceView;

    /* Holds their input for experience rating 0-5 */
    protected int mExperience;

    /* Holds their comments */
    protected String mComments;

    /* Displays correct prompt */
    protected TextView mPrompt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /** Inflate the layout for this fragment and set up view. **/
        View view = setupView(inflater, container);

        /* Grab the last scan result from this fragment or the previous */
        if (savedInstanceState != null) {
            mScanResult = savedInstanceState.getCharSequence("scan_result");
            mManualInput = savedInstanceState.getBoolean("manual_input");
            mExperience = savedInstanceState.getInt("experience");
            mComments = savedInstanceState.getString("comments");
        } else {
            mScanResult =  getArguments().getCharSequence("scan_result");
            mManualInput = getArguments().getBoolean("manual_input");
            mExperience = getArguments().getInt("experience");
            mComments =getArguments().getString("comments");
        }
        String prompt;
        if (mManualInput) {
            prompt = getString(R.string.text_input_confirmation);
        } else {
            prompt = getString(R.string.text_scan_confirmation);
        }

        mPrompt = (TextView) view.findViewById(R.id.checkout_confirmation_code_prompt);
        mPrompt.setText(prompt);

        mExperienceView = (TextView) view.findViewById(R.id.checkout_confirmation_experience_result);
        String mExperienceString = Integer.toString(mExperience);
        mExperienceView.setText(mExperienceString);

        mCommentsView = (TextView) view.findViewById(R.id.checkout_confirmation_comments_result);
        mCommentsView.setText(mComments);

        mScanResultView = (TextView) view.findViewById(R.id.checkout_confirmation_scan_result);
        mScanResultView.setText(mScanResult);

        mConfirmButton = (Button) view.findViewById(R.id.checkout_confirmation_confirm_scan);
        mConfirmButton.setOnClickListener(new ConfirmListener());

        mRetryButton = (Button) view.findViewById(R.id.checkout_confirmation_retry_scan);
        mRetryButton.setOnClickListener(new RetryListener());


        return view;
    }

    /**
     * Separate method for setting up view so that this
     * functionality can be overriden by a subclass.
     * @param inflater instantiates the XML layout
     * @param container is the view group this view belongs to
     */
    @Override
    protected View setupView(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragment_checkout_confirmation, container, false);

        return view;
    }
    // Can remove when sidebar is added
    // TODO: Fix resumeHelper
    @Override
    protected void resumeHelper(){

    }

   /* *//**
     * Records the scan result in shared preferences
     * and displays a success toast.
     *//*
    @Override
    protected void recordScan() {
        CheckoutActivity activity = (CheckoutActivity) getActivity();
        activity.recordResult((String) mScanResult);
    }
*/
    /**
     * Returns to scanner fragment and displays a
     * failure toast.
     */
    @Override
    protected void retry() {
        showFailureToast();
        FragmentManager manager = getFragmentManager();
        manager.popBackStack(CheckoutConfirmationFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }



    /**
     * Save the scan result when this fragment is
     * paused
     * @param outState Bundle passed in by Android
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("scan_result", mScanResult);
        outState.putBoolean("manual_input", mManualInput);
        outState.putInt("experience", mExperience);
        outState.putString("comments", mComments);
    }


    /**
     * Records the scan, returns to scanner fragment,
     * and displays a success toast.
     */
    @Override
    protected void confirm() {
        //recordScan();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        SuccessFragment successFragment = new SuccessFragment();
        successFragment.setType(SuccessFragment.SuccessType.CHECKOUT_SUCCESS);
        transaction.replace(R.id.checkout_activity_container, successFragment);
        transaction.commit();
    }

    /**
     * Records the scan result in shared preferences
     * and displays a success toast.
     */
    @Override
    protected void recordScan() {
        CheckoutActivity activity = (CheckoutActivity) getActivity();
        activity.recordResult((String) mScanResult);
    }


}