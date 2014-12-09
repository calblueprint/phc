package phc.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.salesforce.androidsdk.accounts.UserAccountManager;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.security.PasscodeManager;
import com.salesforce.androidsdk.util.UserSwitchReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity
                          implements SecurityKeyDialogFragment.SecurityKeyDialogListener{

    private PasscodeManager passcodeManager;
    private String apiVersion;
    private RestClient client;
    private UserSwitchReceiver userSwitchReceiver;
    private Map<String, String> resources = new HashMap<String, String>();
    private boolean initialized = false;

    /* SharedPreference file name for Security Key. */
    private static final String SECURITY_PREFS_NAME = "SecurityKeyFile";
    /* SharedPreference object. */
    private SharedPreferences mSecurityKeyPreferences;
    /* SharedPreference editor object. */
    private SharedPreferences.Editor mSecurityKeyPreferencesEditor;
    /* Current stored Security Key. */
    private String mSecurityKey;

    /* Use to set resultCode
     * when calling ServiceActivity
     * to specify intention.
     */
    public static final int FOR_SERVICE = 0;
    public static final int FOR_REGISTRATION = 0;

    // Holds the service provided by the user, selected in the
    // ServiceActivity alert dialog.
    private String mProvidedService;

    /* Holds a toast that shows the data retrieval
     * incomplete message.
     */
    private Toast[] mDataFetchToast = { null };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkConnectivity();
        if (savedInstanceState != null) {
            mProvidedService = savedInstanceState.getString("provided_service");
        }
        // Passcode manager
        passcodeManager = SalesforceSDKManager.getInstance().getPasscodeManager();

        //@TODO: Remove this line before production
        SalesforceSDKManager.getInstance().getLoginServerManager().useSandbox();

        // ApiVersion
        apiVersion = getString(R.string.api_version);

        userSwitchReceiver = new PHCUserSwitchReceiver();
        registerReceiver(userSwitchReceiver, new IntentFilter(UserAccountManager.USER_SWITCH_INTENT_ACTION));

        // Security Key AlertDialog
        mSecurityKeyPreferences = this.getSharedPreferences(SECURITY_PREFS_NAME,
                Context.MODE_PRIVATE);
        mSecurityKeyPreferencesEditor = mSecurityKeyPreferences.edit();
        mSecurityKey = mSecurityKeyPreferences.getString("security_key", null);

        // TODO: replace SecurityKeyDialogFragment.SECURITY_KEY with actual SF security key
        if (mSecurityKey == null || !mSecurityKey.equals(SecurityKeyDialogFragment.SECURITY_KEY)){
            showSecurityKeyDialog();
        }
        else{
            setContentView(R.layout.activity_main);
        }
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates and shows the security key dialog.
     */
    public void showSecurityKeyDialog(){
        DialogFragment dialog = new SecurityKeyDialogFragment();
        dialog.show(getFragmentManager(), "SecurityKeyDialogFragment");
    }

    /** When correct security key is entered, writes security key to SharedPreferences,
     * dismisses the alert dialog, and sets the main activity layout.
     * @param dialog: the AlertDialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        mSecurityKeyPreferencesEditor.putString("security_key",
                ((SecurityKeyDialogFragment)dialog).mInputString);
        mSecurityKeyPreferencesEditor.commit();
        dialog.dismiss();
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onPause() {
        super.onPause();
        passcodeManager.onPause(this);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(userSwitchReceiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkConnectivity();
        if (!initialized) {
            loginSalesforce(true);
        } else {
            loginSalesforce();
        }

    }

    private void loginSalesforce() {
        loginSalesforce(false);
    }

    private void loginSalesforce(final boolean first) {
        // Bring up passcode screen if needed
        if (passcodeManager.onResume(this)) {
            // Login options
            String accountType = SalesforceSDKManager.getInstance().getAccountType();

            // Get a rest client
            new ClientManager(this, accountType, SalesforceSDKManager.getInstance().getLoginOptions(),
                SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {

            @Override
            public void authenticatedRestClient(RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(MainActivity.this);
                    return;
                }
                MainActivity.this.client = client;

                if (first) {
                    MainActivity.this.initResourceList();
                }
                }
            });
        }
    }

    @Override
    // Inflate the menu; this adds items to the action bar if it is present.
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            onLogoutClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Calls Salesforce SDK to log out client.
     * Should be used when the session is over.
     */
    public void onLogoutClick() {
        SalesforceSDKManager.getInstance().logout(this);
    }

    /** Handles the "Services" Button on the splash page. */
    public void openServices(View view) {
        Intent intent = new Intent(this, ServiceActivity.class);
        intent.putExtra("provided_service", mProvidedService);
        intent.putExtra("request_code", FOR_SERVICE);
        CharSequence[] services = null;
        if (!initialized) {
            Log.d("MainActivity", "Resources list not initialized");
        } else {
            services = getResourceList().values().toArray(new CharSequence[0]);
        }
        if (services != null) {
            intent.putExtra("services_list", services);
            /* Called with forResult so we can record the provided service if
             * the user goes back to the MainActivity.
             */
            startActivityForResult(intent, FOR_SERVICE);
        } else {
            displayRetryToast();
        }
    }

    /**
     * Tells the user that the Salesforce query
     * to get the service resources list has
     * not been completed.
     */
    private void displayRetryToast() {
        CharSequence message = getResources().getString(R.string.retry_services);
        maybeShowToast(message, mDataFetchToast, Toast.LENGTH_SHORT, getApplication());
    }

    /**
     * Only shows a toast if it is not already being
     * shown.
     * @param toast
     */
    public static void maybeShowToast(CharSequence message, Toast[] toast, int duration, Context context) {
        if (toast[0] == null || toast[0].getView() == null) {
            toast[0] = Toast.makeText(context, message, duration);
        } else {
            toast[0].setText(message);
        }
        toast[0].show();
    }


    /** Handles the "Register" Button on the splash page. */
    public void openRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public  void openExit(View view) {
        Intent intent = new Intent(this, ExitActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* These are currently only used after calling the ServiceActivity. Make
         * sure result codes are distinct if returning from another activity!
         *
         * Not currently used as scan results are recorded from the service activity.
         * May be used in the future.
         *
         * TODO: remove later if necessary.
         */

        /*
        if (requestCode == FOR_SERVICE) {
            if (resultCode == RESULT_CANCELED) {
                mProvidedService = data.getStringExtra("new_provided_service");
            }
            else if (resultCode == RESULT_OK) {
                mProvidedService = data.getStringExtra("new_provided_service");
            }
        }
        */
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("provided_service", mProvidedService);
    }

    /**
     * Helper that sends request to server and print result in text field.
     *
     * @param request - The request object that gets executed by the SF SDK
     * @param callback - The functions that get called when yhe response comes back
     *                   Modify UI elements here.
     */
    private void sendRequest(RestRequest request, AsyncRequestCallback callback) {

        try {

            client.sendAsync(request, callback);

        } catch (Exception error) {
            Log.e("SF Request", error.toString());
        }
    }

    /**
     * Refreshes the client if the user has been switched.
     */
    private void refreshIfUserSwitched() {
       loginSalesforce();
    }

    /**
     * Acts on the user switch event.
     *
     * @author bhariharan
     */
    private class PHCUserSwitchReceiver extends UserSwitchReceiver {

        @Override
        protected void onUserSwitch() {
            refreshIfUserSwitched();
        }
    }

    /**
     * Static method that returns a map with all of the resources for the most recent event.
     *
     * @return Map of resources. Key = Salesforce Field name; Value = Display Name;
     */
    public Map<String, String> getResourceList() {
        return this.resources;
    }

    /**
     * Run this when the app starts up. This will create the object that contains
     * all resources offered at the most recently created event object. Calls
     * describeResources when it receives a successful response.
     */
    private void initResourceList() {
        RestRequest idRequest = null;
        String soql = "SELECT id FROM PHC_EVENT__c ORDER BY createddate DESC LIMIT 1";
        try {
            idRequest = RestRequest.getRequestForQuery(apiVersion, soql);

            AsyncRequestCallback callback = new AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    try {
                        JSONObject json = response.asJSONObject();
                        JSONObject item = (JSONObject) ((JSONArray)json.get("records")).get(0);
                        String id = item.getString("Id");
                        MainActivity.this.describeResources(id);
                    } catch (Exception e) {
                        Log.e("Id Response Error", e.getLocalizedMessage());
                    }


                }

                @Override
                public void onError(Exception exception) {
                    if (exception.getLocalizedMessage() != null) {
                        Log.e("Id Response Error 2", exception.getLocalizedMessage());
                    }
                }
            };

            sendRequest(idRequest, callback);


        } catch (Exception e) {
            Log.e("Id Request Error", e.getLocalizedMessage());
        }
    }

    /**
     * Created a describe request for the PHC_Resource__c object in the Salesforce
     * backend. This will give us the list of fields we need to query in the next step.
     *
     * @param eventId: The eventId returned from the previous step. This will be used in
     *               the next step
     */
    private void describeResources(final String eventId){
        RestRequest fieldRequest = null;
        final ArrayList<String> fields = new ArrayList<String>();

        try {
            fieldRequest = RestRequest.getRequestForDescribe(apiVersion, "PHC_Resource__c");

            AsyncRequestCallback callback = new AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    try {
                        JSONObject json = response.asJSONObject();
                        JSONArray fieldArray = json.getJSONArray("fields");

                        for (int i = 0; i < fieldArray.length(); i++) {
                            JSONObject field = fieldArray.getJSONObject(i);
                            if (field.getBoolean("custom")) {
                                fields.add(field.getString("name"));
                            }
                        }

                        MainActivity.this.getResourceValues(eventId, fields);

                    } catch (Exception e) {
                        Log.e("Field Response Error", e.getLocalizedMessage());
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e("Field Response Error 2", exception.getLocalizedMessage());
                }
            };

            sendRequest(fieldRequest, callback);

        } catch (Exception e) {
            Log.e("Field Request Exception", e.getLocalizedMessage());
        }
    }

    /**
     *Final step: uses the eventId to find the right PHC_Resource object and queries the
     * fields we found in Step 2 to find which services are available at the given event.
     * Places these into a Map where the key is the column name and the value is its display
     * value.
     *
     * @param eventId: The id of the event associated with the desired resource
     * @param fields: The fields we need to query, since SF doesn't support * notation... -_-
     */
    private void getResourceValues(final String eventId, final List<String> fields){
        fields.remove("Event__c");
        String fieldsString = fields.toString();
        fieldsString = fieldsString.substring(1, fieldsString.length()-1);
        String soql = "SELECT " + fieldsString + " FROM PHC_Resource__c WHERE event__c = '" + eventId + "'";


        try {
            RestRequest valueRequest = RestRequest.getRequestForQuery(apiVersion, soql);
            AsyncRequestCallback callback = new AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    try {
                        JSONObject json = response.asJSONObject();
                        JSONArray records = json.getJSONArray("records");
                        JSONObject item = records.getJSONObject(0);

                        for (String field : fields) {
                            Boolean hasField = item.getBoolean(field);
                            if (hasField) {
                                MainActivity.this.resources.put(field, MainActivity.fieldNameHelper(field));
                            }
                        }
                        MainActivity.this.initialized = true;

                    } catch (Exception e) {
                        Log.e("Value Response Error 2", e.toString());
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e("Value Response Error", exception.toString());
                }
            };
            sendRequest(valueRequest, callback);

        } catch (Exception e) {
            Log.e("Value Request Error", e.toString());
        }
    }


    /**
     * Takes in the name of a salesforce object attribute and returns the human-
     * readable version. Truncates the last 3 characters to get rid of "__c" and
     * replaces remaining underscores ("_") with spaces (" ").
     *
     * @param columnName: the salesforce column name to be converted
     * @return human readable version of columnName
     */
    private static String fieldNameHelper(String columnName) {
        columnName = columnName.substring(0, columnName.length()-3);
        columnName = columnName.replace("_", " ");
        return columnName;
    }

    /** Checks to see if internet connection is available.
     *
     * @param context: the context of the current activity
     * @return true iff the app has access to internet connection.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Creates an alert dialogue to tell the user that the app is not connected
     * to the internet.
     */
    public void connectivityDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                checkConnectivity();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setTitle("No internet connection");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Checks the internet connection.  If there is none, it will create
     * an AlertDialogue that will force the user to reconnect.
     */
    public void checkConnectivity() {
        if(!isNetworkAvailable(this.getBaseContext())) {
            connectivityDialogue();
        }
    }

}
