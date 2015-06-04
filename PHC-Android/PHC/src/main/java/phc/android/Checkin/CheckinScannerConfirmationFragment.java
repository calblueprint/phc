package phc.android.Checkin;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

import phc.android.Helpers.SharedPreferenceEditorListener;
import phc.android.Main.MainActivity;
import phc.android.R;
import phc.android.SharedFragments.ScannerConfirmationFragment;
import phc.android.SharedFragments.SuccessFragment;


public class CheckinScannerConfirmationFragment extends ScannerConfirmationFragment {

    /* Tag for logs and fragment code */
    public final static String TAG = "CHECKIN_SCANNER_CONF";

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
            mUserInfoEditor.apply();
        }
    }

    @Override
    protected View setupView(LayoutInflater inflater, ViewGroup container) {
        View view = super.setupView(inflater, container);
        mPreferenceEditor = new PreferenceEditor(getActivity().getApplicationContext());

        return view;
    }

    /**
     * Writes all entries to SalesForce DB and clears SharedPreferences.
     */
    @Override
    protected void confirm() {
        mPreferenceEditor.storeScanResult(mScanResult);
        registerPerson();
    }

    /**
     * Registers the current person to Salesforce using the information stored in sharedpreferences.
     * If the person is returning (i.e. if search was used), then the function will call updatePerson()
     * with the Id returned from search. Otherwise, it inserts a new person. In both cases, it will also
     * create a new Event Registration object that reflects the user's preferred services.
     */
    private void registerPerson() {
        HashMap<String, Object> fields = getFields();
        String userId = mUserPreferences.getString("user_id", null);
        String authToken = mUserPreferences.getString("auth_token", null);

        sRequestManager.requestCreateEventReg(
                fields,
                userId,
                authToken,
                new RegisterResponseListener(),
                new RegisterErrorListener());

        showProgressDialog(getActivity());
    }

    private class RegisterResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jsonObject) {
            mRequestCompleted = true;
            mUserPreferences.edit().clear().apply();
            loadSuccess();
        }

        public void loadSuccess(){
            FragmentTransaction transaction =
                    getActivity().getFragmentManager().beginTransaction();
            SuccessFragment successFragment = new SuccessFragment();
            successFragment.setType(SuccessFragment.SuccessType.CHECKIN_SUCCESS);
            transaction.replace(R.id.checkin_fragment_container, successFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private class RegisterErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mRequestCompleted = true;

            if (volleyError.getLocalizedMessage() != null) {
                Log.e(TAG, "Volley Error");
                volleyError.printStackTrace();
            }
            volleyError.printStackTrace();

            Toast toast = Toast.makeText(getActivity(),
                    getResources().getString(R.string.register_error),
                    Toast.LENGTH_LONG);
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

        fields.put("FirstName", mUserPreferences.getString("first_name", null));
        fields.put("LastName", mUserPreferences.getString("last_name", null));

        String ssn = "";
        ssn = ssn + mUserPreferences.getString("ssn_1", "");
        ssn = ssn + mUserPreferences.getString("ssn_2", "");
        ssn = ssn + mUserPreferences.getString("ssn_3", "");

        fields.put("SS_Num__c", ssn);

        String year = mUserPreferences.getString("birthday_year", "");
        String month = mUserPreferences.getString("birthday_month", "");
        String day = mUserPreferences.getString("birthday_day", "");

        if(!year.equals("") && !month.equals("") && !day.equals("")) {
            String birthday = year + "-" + month + "-" + day;
            fields.put("Birthdate__c", birthday);
        } else {
            fields.put("Birthdate__c", "");
        }

        String phone = "";
        phone = phone + mUserPreferences.getString("phone_1", "");
        phone = phone + mUserPreferences.getString("phone_2", "");
        phone = phone + mUserPreferences.getString("phone_3", "");

        fields.put("Phone", phone);

        fields.put("PersonEmail", mUserPreferences.getString("email", ""));

        fields.put("Gender__c", mUserPreferences.getString("spinner_gender", ""));
        fields.put("Race__c", mUserPreferences.getString("spinner_ethnicity", ""));
        fields.put("Primary_Language__c", mUserPreferences.getString("spinner_language", ""));
        fields.put("Identify_as_GLBT__c", mUserPreferences.getBoolean("checkbox_glbt", false));
        fields.put("Foster_Care__c", mUserPreferences.getBoolean("checkbox_foster", false));
        fields.put("Veteran__c", mUserPreferences.getBoolean("checkbox_military", false));
        fields.put("How_long_have_you_been_homeless__c", mUserPreferences.getString("spinner_homeless_duration", ""));
        fields.put("Where_do_you_usually_go_for_healthcare__c", mUserPreferences.getString("spinner_healthcare", ""));
        fields.put("Medical_Care_Other__c", mUserPreferences.getString("healthcare_other", ""));
        fields.put("Number__c", mUserPreferences.getString("qr_code", ""));
        fields.put("account_sfid", mUserPreferences.getString("SFID", ""));

        // Add services
        String[] sf_names = ((MainActivity) MainActivity.getContext()).getSalesforceNames();
        for (String name : sf_names) {
            boolean fieldValue = mUserPreferences.getBoolean(name, false);
            if (fieldValue) {
                fields.put(name, fieldValue);
            }
        }

        return fields;
    }

    /**
     * Returns to scanner fragment and displays a
     * failure toast
     */
    @Override
    protected void retry() {
        Log.d("retried", "not working");
        showFailureToast();
        FragmentManager manager = getFragmentManager();
        manager.popBackStack(CheckinScannerConfirmationFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    protected void resumeHelper() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.checkin_sidebar_list);
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
