package phc.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.auth.HttpAccess;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** Call with resultCode of 0 if providing a service.
 * Call with resultCode of 1 if scanning for registration.
 */
public class ServiceActivity extends Activity {

    /** Used to hold which service this user is scanning for. */
    public static String mServiceSelected;

    /** Created when selecting the provided service */
    public static AlertDialog mServiceDialog;

    /** Created to tell the user they submitted an invalid code */
    public static AlertDialog mFailureDialog;

    /** Used in error logs to identify this activity. */
    public static final String TAG = "ServiceActivity";

    /** The result returned to the calling activity through an Intent. */
    public static String mScanResult;

    /** A handle on the fragment that holds the camera to open and release it. */
    public ScannerFragment mScannerFragment;

    /** Holds the menu item generated in onPrepareOptionsMenu() */
    private MenuItem mServiceMenuItem;

    /** Used to hold scan results. */
    private SharedPreferences mSharedPreferences;

    /** String tag for scanned codes in shared preferences */
    private final String ALL_CODES = "scanned_codes";

    /** TextView that shows the user the service they provide */
    private TextView mServicePrompt;

    /** List of services, initialized when this activity
     * is called by the MainActivity.
     */
    private static CharSequence[] services;

    /** Contains both service keys and values to do
     * REST requests.
     */
    private static HashMap<String, String> servicesHashMap;

    /** MainActivity's event id */
    private String mEventID;

    /** MainActivity's api version */
    private String mApiVersion;

    /** Used to hold an instance of the MainActivity */
    private MainActivity mMainActivity;

    /**
     * As soon as the activity starts, this sets up
     * the camera preview and the listener for the camera
     * preview frame.
     * @param savedInstanceState is passed in by the calling
     * activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        mMainActivity = (MainActivity) MainActivity.getContext();
        mEventID = mMainActivity.getEventID();
        mApiVersion = mMainActivity.getApiVersion();

        if (findViewById(R.id.service_fragment_container) != null) {
            /* However, if we're being restored from a previous state,
             * then we don't need to do anything and should return or else
             * we could end up with overlapping fragments.
             */
            if (savedInstanceState == null) {
                Bundle bundle = getIntent().getExtras();
                int intention = (Integer) bundle.get("request_code");
                /* This cannot be null! */
                services = bundle.getCharSequenceArray("services_list");
                servicesHashMap = (HashMap<String, String>) bundle.getSerializable("services_hash");
                if (bundle.get("provided_service") == null && intention == MainActivity.FOR_SERVICE) {
                    showSelectServiceDialog(true);
                } else {
                    mServiceSelected = (String) bundle.get("provided_service");
                }
            } else {
                services = savedInstanceState.getCharSequenceArray("services_list");
                servicesHashMap = (HashMap<String, String>) savedInstanceState.getSerializable("services_hash");
                mServiceSelected = (String) savedInstanceState.getCharSequence("provided_service");
                return;
            }

            mSharedPreferences = getPreferences(MODE_PRIVATE);
            mScannerFragment = new ScannerFragment();
            mScannerFragment.setArguments(getIntent().getExtras());
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.add(R.id.service_fragment_container, mScannerFragment);
            t.commit();
        }
    }

    /**
     * Uses a DialogFragment to display an AlertDialog to the user.
     * The newInstance() method must be used to create a new instance.
     */
    public static class ServiceAlertDialogFragment extends DialogFragment {

        static boolean mustSelect = false;

        /**
         * Used to create a new instance of this fragment. Only one instance
         * should be used at any given time as this is a static class.
         * @param mustSelect is True if the user has not already selected a service,
         * False otherwise
         * @return
         */
        public static ServiceAlertDialogFragment newInstance(final boolean mustSelect) {
            ServiceAlertDialogFragment frag = new ServiceAlertDialogFragment();
            ServiceAlertDialogFragment.mustSelect = mustSelect;
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return createSelectServiceDialog(mustSelect);
        }

        /**
         * Override the onCancel method in the DialogFragment
         * to prevent cancellation when service has not been
         * set.
         * @param dialogInterface
         */
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if (mustSelect) {
                getActivity().onBackPressed();
            } else {
                dialogInterface.dismiss();
            }
        }

        /**
         *
         * @param mustSelect is True if the user has not already selected a service,
         * False otherwise
         * @return a Dialog that will be shown to the user.
         */
        private Dialog createSelectServiceDialog(final boolean mustSelect) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Provided Service");
            /* if service is already selected, pre select a button. */
            int prevIndex = -1;
            if (mServiceSelected != null) {
                for (int i = 0; i < services.length; i++) {
                    if (mServiceSelected.equals(services[i])) {
                        prevIndex = i;
                    }
                }
            }
            builder.setSingleChoiceItems(services, prevIndex, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int item) {
                    mServiceSelected = services[item].toString();
                    ((ServiceActivity) getActivity()).setServicePromptText();
                    mServiceDialog.dismiss();
                }
            });
            /* handle back button press */
            mServiceDialog = builder.create();
            mServiceDialog.setCanceledOnTouchOutside(false);
            return mServiceDialog;
        }
    }

    /**
     * Uses a DialogFragment to display an AlertDialog to the user.
     * The newInstance() method must be used to create a new instance.
     */
    public static class FailureAlertDialogFragment extends DialogFragment {

        static String message;

        /**
         * Used to create a new instance of this fragment. Only one instance
         * should be used at any given time as this is a static class.
         * @param message is the message to show the user
         * False otherwise
         * @return
         */
        public static FailureAlertDialogFragment newInstance(String message) {
            FailureAlertDialogFragment frag = new FailureAlertDialogFragment();
            FailureAlertDialogFragment.message = message;
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return createFailureAlertDialog(message);
        }

        /**
         * Override the onCancel method in the DialogFragment
         * to prevent cancellation when service has not been
         * set.
         * @param dialogInterface
         */
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            /** Do nothing for now, we may want to change this later */
        }

        /**
         *
         * @param message is the message to show the user.
         * @return a Dialog that will be shown to the user.
         */
        private Dialog createFailureAlertDialog(String message) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setTitle("Error Notice");
            builder.setMessage(message);
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mFailureDialog.dismiss();
                }
            });
            mFailureDialog = builder.create();
            mFailureDialog.setCanceledOnTouchOutside(false);
            return mFailureDialog;
        }
    }

    private void showFailureAlertDialog(String message) {
        DialogFragment newFrag = FailureAlertDialogFragment.newInstance(message);
        newFrag.show(getFragmentManager(), "FailureDialog");

    }

    /**
     * Displays a dialog box that prompts the user to select the service
     * that they provide.
     * @param mustSelect is True if the user has not already selected a service.
     */
    private void showSelectServiceDialog(final boolean mustSelect) {
        DialogFragment newFrag = ServiceAlertDialogFragment.newInstance(mustSelect);
        newFrag.show(getFragmentManager(), "ServiceDialog");
    }

    /**
     * Can be called by other activities or
     * fragments to store scan results.
     * Not currently used, but may be
     * useful later.
     * @param result String scan result
     */
    public void storeScanResult(String result) {
        /* TODO: Need a function that updates the
         * Salesforce object
         *
         * updateRegistration(result, mSelectedService)
         */
        Set<String> defaults = null;
        SharedPreferences prefs = getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> codes = prefs.getStringSet(ALL_CODES, defaults);
        if (codes == null) {
            Log.e(TAG, "codes are empty, creating new");
            codes = new HashSet<String>();
        }
        codes.add(result);
        editor.putStringSet(ALL_CODES, codes);
        editor.apply();
    }

    /**
     * Retrieves all scan results obtained thus far.
     * @return a Set<String> of all results obtained
     */
    public Set<String> getAllScanResults() {
        Set<String> defaults = null;
        SharedPreferences prefs = getSharedPreferences(TAG, MODE_PRIVATE);
        Set<String> codes = prefs.getStringSet(ALL_CODES, defaults);
        if (codes == null) {
            Log.e(TAG, "Could not access scanned codes");
        }
        return codes;
    }

    /**
    * Lets the calling activity know that a valid
    * QR code was received. This valid code may be
    * overwritten multiple times before it is
    * returned to the calling activity.
    * @param result is the decoded string
    * @return no return value, uses Intent to communicate
    */
    private void returnSuccessfulResult(String result) {
        Intent scanResult = new Intent();
        scanResult.putExtra("scan_result", result);
        scanResult.putExtra("new_provided_service", mServiceSelected);
        setResult(RESULT_OK, scanResult);
        finish();
    }

    /**
     * Lets the calling activity know that a valid
     * QR code was not received before the user
     * returned using the back button.
     * @return no return value, uses Intent to communicate
     */
    private void returnCanceledResult() {
        Intent scanResult = new Intent();
        scanResult.putExtra("new_provided_service", mServiceSelected);
        setResult(RESULT_CANCELED, scanResult);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        setServicePromptText();
    }

    /**
     * Updates the service prompt text view for the current
     * service selected.
     */
    private void setServicePromptText() {
        mServicePrompt = (TextView) findViewById(R.id.service_prompt_text);
        String prompt = getString(R.string.service_prompt);
        mServicePrompt.setText(Html.fromHtml(prompt + "<br />" + "<b>" + mServiceSelected + "<b>"));
    }

    /**
     * Dynamically clears the options menu and adds the option to
     * change a service.
     * @param menu is passed in by the library.
     * @return true so that menu is created.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        /* These parameters are the groupID, itemID, order, and text for the item */
        mServiceMenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.change_service);
        return true;
    }
    /**
     * Not currently used. onPrepareOptionsMenu is
     * used instead to add options.
     * @param menu is passed in by the library
     * @return must be true for menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * onBackPressed() overrides the default back button
     * functionality. It ensures that the calling activity
     * will receive the appropriate result if the user
     * returns using the back button.
     */
    @Override
    public void onBackPressed() {
        ScannerConfirmationFragment frag = (ScannerConfirmationFragment) getFragmentManager().
                findFragmentByTag(ScannerConfirmationFragment.TAG);
        if (frag != null && frag.isVisible()) {
            /* Return to the scanner fragment if
             * back button is pressed in confirmation
             * fragment
             */
            frag.whenBackPressed();
            return;
        } else {
            if (mScanResult == null) {
                returnCanceledResult();
            } else {
                returnSuccessfulResult(mScanResult);
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequenceArray("services_list", services);
        outState.putSerializable("services_hash", servicesHashMap);
        outState.putCharSequence("provided_service", mServiceSelected);
    }

    /**
     * Handles item selection in the menu.
     * @param item is the selected item
     * @return true if the action is consumed here,
     * false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         */
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == mServiceMenuItem.getItemId()) {
            /* User does not have to select another service option */
            showSelectServiceDialog(false);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void recordResult(String code) {
        getServiceStatus(getKeyByValue(servicesHashMap, mServiceSelected), code);
    }

    /**
     * Gets the status of a given service for a person with a specified qr code number.
     * Requires the current event id.
     *
     * @param serviceName: the SalesForce field name of the required service.
     * @param personNumber: the QR number of the person whose registration is needed.
     */
    private void getServiceStatus(final String serviceName, String personNumber) {
        RestRequest serviceRequest = null;
        String soql = "SELECT Id, " + serviceName + " FROM Event_Registration__c ";
        soql = soql + "WHERE PHC_Event__c = '" + mEventID + "' AND ";
        soql = soql + "Number__c = '" + personNumber + "'";
        try {
            serviceRequest = RestRequest.getRequestForQuery(mApiVersion, soql);

            RestClient.AsyncRequestCallback callback = new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    try {
                        JSONObject json = response.asJSONObject();
                        JSONObject item = (JSONObject) ((JSONArray)json.get("records")).get(0);
                        String serviceValue = item.getString(serviceName);
                        String registrationId = item.getString("Id");
                        //@TODO: Put logic using serviceValue here.
                        //@TODO: Save this id and serviceValue. We'll need it for the next step.
                        // May want to save this rather than calling checkinService
                        checkinService(serviceName, serviceValue, registrationId);

                    } catch (Exception e) {
                        Log.e("Service Value Response Error", e.getLocalizedMessage());
                        showFailureAlertDialog("The given code is invalid. Please enter a valid code and try again.");
                    }
                }

                @Override
                public void onError(Exception exception) {
                    if (exception.getLocalizedMessage() != null) {
                        Log.e("Service Value Response Error 2", exception.getLocalizedMessage());
                        if (exception.getCause() instanceof HttpAccess.NoNetworkException) {
                            showFailureAlertDialog("No network connection found. Please check the connection and try again.");
                        }
                    } else {
                        /** This is a Volley unexpected response code, most likely 400 **/
                        showFailureAlertDialog("The code you submitted does not exist on the server. Please check to make sure it is correct and retry.");
                    }
                }
            };

            mMainActivity.sendRequest(serviceRequest, callback);


        } catch (Exception e) {
            if (e.getLocalizedMessage() != null) {
                Log.e("Service Value Request Error", e.getLocalizedMessage());
            }
            showFailureAlertDialog("An unexpected error occurred while processing your request. Please contact technical support.");
        }
    }

    /**
     * Checks in a person to a given service by updating the service and its time field on the
     * associated Event Registration object.
     *
     * @param serviceName: The SalesForce field name of the service being updated.
     * @param currentStatus: The current value of the service being updated.
     * @param Id: The Id of the Event Registration object to be updated.
     */
    private void checkinService(String serviceName,
                                String currentStatus,
                                String Id) {

        RestRequest serviceRequest = null;
        HashMap<String, Object> fields = new HashMap<String, Object>();

        String newStatus;
        if (currentStatus.equals("Applied")) {
            newStatus = "Received";
        } else {
            newStatus = "Drop In";
        }

        fields.put(serviceName, newStatus);
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd\'T\'hh:mm:ss\'Z\'");
        String formattedDate = df.format(date);
        fields.put(serviceNameTimeHelper(serviceName), formattedDate);

        try {
            serviceRequest = RestRequest.getRequestForUpdate(mApiVersion, "Event_Registration__c", Id, fields);

            RestClient.AsyncRequestCallback callback = new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    showSuccessToast();
                }

                @Override
                public void onError(Exception exception) {
                    if (exception.getLocalizedMessage() != null) {
                        Log.e("Service Update Response Error", exception.toString());
                        if (exception.getCause() instanceof HttpAccess.NoNetworkException) {
                            showFailureAlertDialog("No network connection found. Please check the connection and try again.");
                            return;
                        }
                    }
                    showFailureAlertDialog("Your code is valid but there was an error updating the data. Please try again or contact support.");
                }
            };

            mMainActivity.sendRequest(serviceRequest, callback);


        } catch (Exception e) {
            Log.e("Service Update Request Error", e.toString());
            showFailureAlertDialog("An unexpected error occurred while processing your request. Please contact technical support.");
        }
    }

    /**
     * Helper for finding the appropriate key in the
     * services HashMap for a given value.
     * @param map should be the services HashMap
     * @param value is the value to get the key with
     * @return String key corresponding to value
     */
    public static String getKeyByValue(HashMap<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Helper to convert a SF Resource field name into its time field.
     *
     * @param serviceName: The field name of a given service
     * @return: The field name with "_Time" appended to it
     *          (e.g. Showers__c -> Showers_Time__c)
     */
    private String serviceNameTimeHelper(String serviceName) {
        String timeString = serviceName.substring(0, serviceName.length()-3);
        timeString = timeString + "_Time__c";
        return timeString;
    }

    /**
     * Displays a success message at the bottom of
     * the screen.
     */
    protected void showSuccessToast() {
        Context c = getApplicationContext();
        CharSequence message = getResources().getString(R.string.scan_success);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }

}
