package phc.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import java.util.HashMap;
import java.util.Map;


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
            boolean result = registerPerson();
            /* shows success toast */
            if (result) {
                recordScan();
                super.onClick(view);
            }

        }

        private boolean registerPerson() {
            boolean result = true;
            SharedPreferences searchResult;
            searchResult = getActivity().getSharedPreferences(SearchResultsFragment.SEARCH_RESULT, 0);

            if(!searchResult.getBoolean("Searched", false)) {
                result = newPerson();
            } else {
                result = updatePerson(searchResult.getString("SFID", null));
                searchResult.edit().clear().commit();
            }
            return result;
        }


        private boolean newPerson() {
            String apiVersion = getActivity().getResources().getString(R.string.api_version);
            String objectName = "Account";
            Map<String, Object> fields = getFields();

            try {
                RestRequest request = RestRequest.getRequestForCreate(apiVersion, objectName, fields);
                RestClient.AsyncRequestCallback callback = new RestClient.AsyncRequestCallback() {
                    @Override
                    public void onSuccess(RestRequest request, RestResponse response) {

                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("Insert Response Error", exception.getLocalizedMessage());
                    }
                };
                sendRequest(request, callback);
            } catch (Exception e) {
                Log.e("Person Insert Error", e.toString());
            }

            return true;
        }

        private boolean updatePerson(String Id) {
            String apiVersion = getActivity().getResources().getString(R.string.api_version);
            String objectName = "Account";
            Map<String, Object> fields = getFields();

            try {
                RestRequest request = RestRequest.getRequestForUpdate(apiVersion, objectName, Id, fields);
                RestClient.AsyncRequestCallback callback = new RestClient.AsyncRequestCallback() {
                    @Override
                    public void onSuccess(RestRequest request, RestResponse response) {

                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("Update Response Error", exception.getLocalizedMessage());
                    }
                };
                sendRequest(request, callback);
            } catch (Exception e) {
                Log.e("Person Update Error", e.toString());
            }

            return true;
        }

        private Map<String, Object> getFields() {
            HashMap<String, Object> fields = new HashMap<String, Object>();

            

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

