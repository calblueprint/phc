package phc.android.Checkin;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import phc.android.Helpers.OnSubmitClickListener;
import phc.android.Main.MainActivity;
import phc.android.Networking.RequestManager;
import phc.android.R;
import phc.android.SharedFragments.ScannerConfirmationFragment;
import phc.android.Helpers.SharedPreferenceEditorListener;


public class CheckinScannerConfirmationFragment extends ScannerConfirmationFragment {

    /* Tag for logs and fragment code */
    public final static String TAG = "CheckinScannerConfirmationFragment";
    // Key for user shared preferences
    private static final String USER_AUTH_PREFS_NAME = "UserKey";

    // Request Manager Objects
    private static RequestManager sRequestManager;
    private static RequestQueue sRequestQueue;

    /* Name to store code in saved preferences */
    private final String mName = "qr_code";

    // Shared Preferences
    private SharedPreferences mUserPreferences;

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
     * @param inflater instantiates the XML layout
     * @param container is the view group this view belongs to
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        super.onCreateView(inflater, container, bundle);

        View view = inflater.inflate(R.layout.fragment_scanner_confirmation, container, false);
        mPreferenceEditor = new PreferenceEditor(getActivity().getApplicationContext());

        mScanResultView = (TextView) view.findViewById(R.id.scan_result);
        mScanResultView.setText(mScanResult);

        mRetryButton = (Button) view.findViewById(R.id.retry_scan);
        mRetryButton.setOnClickListener(new RetryListener());

        mConfirmButton = (Button) view.findViewById(R.id.confirm_scan);
        mConfirmButton.setText(getString(R.string.submit_form));
        mConfirmButton.setOnClickListener(new SubmitListener(getActivity()));

        //Set up Volley request framework
        sRequestQueue = Volley.newRequestQueue(getActivity());
        sRequestManager = new RequestManager(TAG, sRequestQueue);
        return view;
    }

    /**
     * Used when the user wants to return to scan
     */
    protected class RetryListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            retry();
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
            registerPerson();
            // clear previous information here
            super.onClick(view);

        }

        /**
         * Registers the current person to Salesforce using the information stored in sharedpreferences.
         * If the person is returning (i.e. if search was used), then the function will call updatePerson()
         * with the Id returned from search. Otherwise, it inserts a new person. In both cases, it will also
         * create a new Event Registration object that reflects the user's preferred services.
         */
        private void registerPerson() {
            HashMap<String, Object> fields = getFields();
            mUserPreferences = getActivity().getSharedPreferences(USER_AUTH_PREFS_NAME,
                    Context.MODE_PRIVATE);
            String userId = mUserPreferences.getString("user_id", null);
            String authToken = mUserPreferences.getString("auth_token", null);

            sRequestManager.requestCreateEventReg(
                    fields,
                    userId,
                    authToken,
                    new RegisterResponseListener(),
                    new RegisterErrorListener());
        }

        private class RegisterResponseListener implements Response.Listener<JSONObject> {

            @Override
            public void onResponse(JSONObject jsonObject) {
                mUserInfo.edit().clear().apply();
                Log.d(TAG, jsonObject.toString());
            }
        }

        private class RegisterErrorListener implements Response.ErrorListener {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.getLocalizedMessage() != null) {
                    Log.e(TAG, "Volley Error");
                    volleyError.printStackTrace();
                }
                volleyError.printStackTrace();

                Toast toast = Toast.makeText(getActivity(), "Error registering user", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        /**
         * A helper function for getting the relevant information to post to the new Account on Salesforce.
         *
         * @return a Map containing key value pairs of Account information. The keys are field names,
         * and the values are their associated values.
         */
        private HashMap<String, Object> getFields() {
            HashMap<String, Object> fields = new HashMap<String, Object>();
            SharedPreferences userPreferences;
            userPreferences = mUserInfo;

            fields.put("FirstName", userPreferences.getString("first_name", null));
            fields.put("LastName", userPreferences.getString("last_name", null));

            String ssn = "";
            ssn = ssn + userPreferences.getString("ssn_1", "");
            ssn = ssn + userPreferences.getString("ssn_2", "");
            ssn = ssn + userPreferences.getString("ssn_3", "");

            fields.put("SS_Num__c", ssn);

            String year = userPreferences.getString("birthday_year", "");
            String month = userPreferences.getString("birthday_month", "");
            String day = userPreferences.getString("birthday_day", "");

            if(!year.equals("") && !month.equals("") && !day.equals("")) {
                String birthday = year + "-" + month + "-" + day;
                fields.put("Birthdate__c", birthday);
            } else {
                fields.put("Birthdate__c", "");
            }

            String phone = "";
            phone = phone + userPreferences.getString("phone_1", "");
            phone = phone + userPreferences.getString("phone_2", "");
            phone = phone + userPreferences.getString("phone_3", "");

            fields.put("Phone", phone);

            fields.put("PersonEmail", userPreferences.getString("email", ""));

            fields.put("Gender__c", userPreferences.getString("spinner_gender", ""));
            fields.put("Ethnicity__pc", userPreferences.getString("spinner_ethnicity", ""));
            fields.put("Primary_Language__c", userPreferences.getString("spinner_language", ""));
            fields.put("Identify_as_GLBT__c", userPreferences.getBoolean("checkbox_glbt", false));
            fields.put("Foster_Care__c", userPreferences.getBoolean("checkbox_foster", false));
            fields.put("Veteran__c", userPreferences.getBoolean("checkbox_military", false));

            return fields;
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
     * Returns to scanner fragment and displays a
     * failure toast
     */
    @Override
    protected void retry() {
        showFailureToast();
        FragmentManager manager = getFragmentManager();
        manager.popBackStack(CheckinScannerConfirmationFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

