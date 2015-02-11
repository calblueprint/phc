package phc.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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
     * @param inflater instantiates the XML layout
     * @param container is the view group this view belongs to
     */
    @Override
    protected View setupView(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragment_scanner_confirmation, container, false);
        mPreferenceEditor = new PreferenceEditor(getActivity().getApplicationContext());

        mScanResultView = (TextView) view.findViewById(R.id.scan_result);
        mScanResultView.setText(mScanResult);

        mRetryButton = (Button) view.findViewById(R.id.retry_scan);
        mRetryButton.setOnClickListener(new RetryListener());

        mConfirmButton = (Button) view.findViewById(R.id.confirm_scan);
        mConfirmButton.setText(getString(R.string.form_submit));
        mConfirmButton.setOnClickListener(new SubmitListener(getActivity()));
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
            /* shows success toast */
            recordScan();
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
            SharedPreferences searchResult;
            searchResult = getActivity().getSharedPreferences(SearchResultsFragment.SEARCH_RESULT, 0);

            if(!searchResult.getBoolean("Searched", false)) {
                newPerson();
            } else {
                updatePerson(searchResult.getString("SFID", null));
                searchResult.edit().clear().commit();
            }
        }

        /**
         * Inserts a new Account object using the information from the form if search was not used.
         */
        private void newPerson() {
            String apiVersion = getActivity().getResources().getString(R.string.api_version);
            String objectName = "Account";
            Map<String, Object> fields = getFields();

            //@TODO: Add error handling

            try {
                RestRequest request = RestRequest.getRequestForCreate(apiVersion, objectName, fields);
                RestClient.AsyncRequestCallback callback = new RestClient.AsyncRequestCallback() {
                    @Override
                    public void onSuccess(RestRequest request, RestResponse response) {
                        try {
                            JSONObject json = response.asJSONObject();
                            boolean success = json.getBoolean("success");
                            if (success) {registration(json.getString("id"));}
                        } catch (IOException e1) {

                        } catch (JSONException e2) {

                        }
                    }

                    @Override
                    public void onError(Exception exception) {

                        Log.e("Insert Response Error", exception.toString());
                    }
                };
                sendRequest(request, callback);
            } catch (Exception e) {
                Log.e("Person Insert Error", e.toString());
            }

        }

        /**
         * Updated a known Account object if search was used to arrive at submit page.
         * @param Id: The id of the returning client.
         */
        private void updatePerson(final String Id) {
            String apiVersion = getActivity().getResources().getString(R.string.api_version);
            String objectName = "Account";
            Map<String, Object> fields = getFields();
            //@TODO: Add error handling

            try {
                RestRequest request = RestRequest.getRequestForUpdate(apiVersion, objectName, Id, fields);
                RestClient.AsyncRequestCallback callback = new RestClient.AsyncRequestCallback() {
                    @Override
                    public void onSuccess(RestRequest request, RestResponse response) {
                       registration(Id);
                    }

                    @Override
                    public void onError(Exception exception) {

                        Log.e("Update Response Error", exception.toString());
                    }
                };
                sendRequest(request, callback);
            } catch (Exception e) {
                Log.e("Person Update Error", e.toString());
            }
        }

        /**
         * After an Account is sucessfully inserted/updated, this method will create a new registration
         * object using the current PHC Event and the rest of the information from the filled out forms.
         *
         * @param PersonId: The id of the person to whom the registration refers
         */
        private void registration(String PersonId) {
            String eventId = ((RegisterActivity) getActivity()).getmEventId();
            String apiVersion = getActivity().getResources().getString(R.string.api_version);
            String objectName = "Event_Registration__c";
            String[] fields = ((RegisterActivity) getActivity()).getServiceSFNames();
            Map<String, Object> fieldValues = new HashMap<String, Object>();

            SharedPreferences servicePreferences = mUserInfo;
            for (String field : fields) {
                Log.d("Field", field);
                boolean fieldValue = servicePreferences.getBoolean(field, false);
                if (fieldValue) {
                    fieldValues.put(field, "Applied");
                }
            }
            fieldValues.put("PHC_Event__c", eventId);
            fieldValues.put("Account__c", PersonId);
            fieldValues.put("Number__c", servicePreferences.getString(mName, "0"));

            try {
                RestRequest request = RestRequest.getRequestForCreate(apiVersion, objectName, fieldValues);
                RestClient.AsyncRequestCallback callback = new RestClient.AsyncRequestCallback() {
                    @Override
                    public void onSuccess(RestRequest request, RestResponse response) {
                        try {
                            JSONObject json = response.asJSONObject();
                            boolean success = json.getBoolean("success");
                            mUserInfo.edit().clear().commit();
                        } catch (IOException e1) {

                        } catch (JSONException e2) {

                        }
                    }

                    @Override
                    public void onError(Exception exception) {

                        Log.e("Register Response Error", exception.toString());
                    }
                };
                sendRequest(request, callback);

            } catch (Exception e) {
                Log.e("Person Update Error", e.toString());
            }
        }

        /**
         * A helper function for getting the relevant information to post to the new Account on Salesforce.
         *
         * @return a Map containing key value pairs of Account information. The keys are field names,
         * and the values are their associated values.
         */
        private Map<String, Object> getFields() {
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
            fields.put("Foster_Care__c", userPreferences.getBoolean("checkbox_foster", false));
            fields.put("Veteran__c", userPreferences.getBoolean("checkbox__military", false));
            fields.put("Veteran__c", userPreferences.getBoolean("checkbox__military", false));
            fields.put("Minor_Children__c", userPreferences.getBoolean("checkbox_children", false));


            return fields;
        }

        /**
         * Helper that sends request to server and print result in text field.
         *
         * @param request - The request object that gets executed by the SF SDK
         * @param callback - The functions that get called when yhe response comes back
         *                   Modify UI elements here.
         */
        private void sendRequest(RestRequest request, RestClient.AsyncRequestCallback callback) {

            try {

                ((RegisterActivity) getActivity()).client.sendAsync(request, callback);

            } catch (Exception error) {
                Log.e("SF Request", error.toString());
            }
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
        manager.popBackStack(RegistrationScannerConfirmationFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

