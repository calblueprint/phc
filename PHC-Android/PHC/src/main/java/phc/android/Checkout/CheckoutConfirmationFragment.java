package phc.android.Checkout;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

import phc.android.Helpers.OnSubmitClickListener;
import phc.android.Helpers.SharedPreferenceEditorListener;
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

    protected String mServicesNotReceived;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /** Inflate the layout for this fragment and set up view. **/
        View view = setupView(inflater, container);

        /* Grab the last scan result from this fragment or the previous */
        if (savedInstanceState != null) {
            mScanResult = savedInstanceState.getCharSequence("scan_result").toString();
            mManualInput = savedInstanceState.getBoolean("manual_input");
            mExperience = savedInstanceState.getInt("experience");
            mComments = savedInstanceState.getString("comments");
        } else {
            mScanResult =  getArguments().getCharSequence("scan_result").toString();
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
        //TODO: Submit results
        //recordScan();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        SuccessFragment successFragment = new SuccessFragment();
        successFragment.setType(SuccessFragment.SuccessType.CHECKOUT_SUCCESS);
        transaction.replace(R.id.checkout_activity_container, successFragment);
        transaction.commit();
    }

    /**
     * Used to submit checkout fields
     * Uses OnSubmit
     */
    protected class SubmitListener extends OnSubmitClickListener implements View.OnClickListener{
        public SubmitListener(Context context){ super (context); }
        @Override
        public void onClick(View view){
            // mPreferenceEditor.storescanresult(mScanResult);
            fillFields();
            // clear previous information
            super.onClick(view);
        }

        /**
         * Creates hashmap that has checkout fields (comments, experience, services not received
         */
        private void fillFields(){
           HashMap<String, Object> fields = getFields();
            mUserPreferences = getActivity().getSharedPreferences(USER_AUTH_PREFS_NAME,
                    Context.MODE_PRIVATE);
            String userId = mUserPreferences.getString("user_id", null);
            String authToken = mUserPreferences.getString("auth_token", null);

            sRequestManager.requestUpdateFeedback(
                    fields,
                    userId,
                    authToken,
                    new RegisterResponseListener(),
                    new RegisterErrorListener());
        }

        private class RegisterResponseListener implements Response.Listener<JSONObject>{

            @Override
            public void onResponse(JSONObject jsonObject){
                //mUserInfo.edit().clear().apply();
                // TODO: Not sure what this is for?
                Log.d(TAG, jsonObject.toString());
            }
        }
        private class RegisterErrorListener implements Response.ErrorListener{
            @Override
            public void onErrorResponse(VolleyError volleyError){
                if (volleyError.getLocalizedMessage() != null){
                    Log.e(TAG, "Volley Error");
                    volleyError.printStackTrace();
                }
                volleyError.printStackTrace();
                Toast toast = Toast.makeText(getActivity(), "Error checking out user", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        /**
         * A Helper function for getting the relevant information to post
         * @return a Map containing key value pairs of checkout information. The keys are
         * field names, and the values are their associated values.
         */
        private HashMap<String, Object> getFields(){
            HashMap<String, Object> fields = new HashMap<String,Object>();

            fields.put("Comment", mComments);
            fields.put("Experience", mExperience);
            fields.put("ServicesNotReceived", mServicesNotReceived);
            fields.put("Number__c", mScanResult);

            return fields;
        }

    }

}

























