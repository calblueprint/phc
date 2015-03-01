package phc.android.Main;

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
import android.widget.Button;
import android.widget.Toast;

import com.salesforce.androidsdk.accounts.UserAccountManager;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.util.UserSwitchReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phc.android.Checkin.CheckinActivity;
import phc.android.Checkout.CheckoutActivity;
import phc.android.Helpers.Utils;
import phc.android.R;
import phc.android.Services.ServicesActivity;

public class MainActivity extends Activity
                          implements SecurityKeyDialogFragment.SecurityKeyDialogListener{

    /*SALESFORCE SETUP*/
    private String apiVersion;
    /** Allows app to send HTTP requests to Salesforce server. */
    private RestClient client;
    /** Broadcast Receiver that listens for the user switch event. */
    private UserSwitchReceiver userSwitchReceiver;

    /*SECURITY KEY*/
    /** SharedPreference file name for Security Key. */
    private static final String SECURITY_PREFS_NAME = "SecurityKey";
    /** SharedPreference object. */
    private SharedPreferences mSecurityKeyPreferences;
    /** SharedPreference editor object. */
    private SharedPreferences.Editor mSecurityKeyPreferencesEditor;
    /** Current stored Security Key. */
    private String mSecurityKey;

    // User credentials
    // Key for user shared preferences
    private static final String USER_PREFS_NAME = "UserKey";
    // Shared Preferences
    private SharedPreferences mUserPreferences;
    // SharedPreference editor object
    private SharedPreferences.Editor mUserPreferencesEditor;

    /*RETRIEVING SERVICES (USED BY ALL ACTIVITIES)*/
    /** Hashmap of all services being offered at the event, where the Key is the Salesforce name
     of the service (e.g. "acupuncture__c") and the value is the display name of the service (e.g.
     "acupuncture"). */
    private static HashMap<String, String> sOfferedServices = new HashMap<String, String>();
    /** Alphabetized array of Salesforce names for all services. (keys of mOfferedServices). */
    private static String[] sSalesforceNames;
    /** Alphabetized array of display names for all services. (values of mOfferedServices). */
    private static String[] sDisplayNames;

    /** Indicates whether mOfferedServices has been retrieved and initialized yet. */
    private boolean mInitialized = false;
    /** Holds a toast that shows the services data retrieval incomplete message. */
    private Toast[] mToasts = { null };

    /*SERVICES ACTIVITY*/
    /** Used by ServicesActivity to perform REST requests */
    private static Context mContext;

    /*OTHER*/
    /** Holds the event Id of the most recently created PHC Event,
     * treated in the app as the current event. */
    private String mEventId = "";

    /*BUTTONS*/
    /** Button leading to Services Activity. */
    private Button mServicesButton;
    /** Button leading to Checkin Activity. */
    private Button mCheckinButton;
    /** Button leading to Checkout Activity. */
    private Button mCheckoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkConnectivity();
        mContext = this;

        // Salesforce Setup
        apiVersion = getString(R.string.api_version);
        //@TODO: Remove this line before production
        SalesforceSDKManager.getInstance().getLoginServerManager().useSandbox();
        userSwitchReceiver = new PHCUserSwitchReceiver(); //launches initial Salesforce login page.
        registerReceiver(userSwitchReceiver, new IntentFilter(UserAccountManager.USER_SWITCH_INTENT_ACTION));

        // Security Key AlertDialog
        mSecurityKeyPreferences = this.getSharedPreferences(SECURITY_PREFS_NAME,
                Context.MODE_PRIVATE);
        mSecurityKeyPreferencesEditor = mSecurityKeyPreferences.edit();
        mSecurityKey = mSecurityKeyPreferences.getString("security_key", null);
        // TODO: replace SecurityKeyDialogFragment.SECURITY_KEY with actual SF security key
        if (mSecurityKey == null || !mSecurityKey.equals(SecurityKeyDialogFragment
                .SECURITY_KEY)){
            showSecurityKeyDialog();
        }
        else{
            setContentView(R.layout.activity_main);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkConnectivity();
        refreshButtons();
    }

    /**
     * If service list has not been initialized, disable all buttons
     * and login to Salesforce to retrieve the list.
     */
    public void refreshButtons(){
        Log.d("refreshed", "refreshed");
        // Buttons
        mServicesButton = (Button) findViewById(R.id.button_services);
        mCheckinButton = (Button) findViewById(R.id.button_checkin);
        mCheckoutButton = (Button) findViewById(R.id.button_checkout);

        if (!mInitialized){
            disableAllButtons();
            loginSalesforce(); //will enable buttons if successful
        }
        else{
            enableAllButtons();
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(userSwitchReceiver);
        super.onDestroy();
    }

    /**
     * Acts on the user switch event.
     */
    private class PHCUserSwitchReceiver extends UserSwitchReceiver {

        /**
         * Refreshes the client if the user has been switched, reopening the login.
         */
        @Override
        protected void onUserSwitch() {
            mInitialized = false;
            loginSalesforce();
        }
    }

    /**
     * Opens login screen. If successful login, initializes services,
     * sets mInitialized to true, and enables buttons.
     */
    private void loginSalesforce() {
        // Login options
        String accountType = SalesforceSDKManager.getInstance().getAccountType();

        // Get a rest client
        new ClientManager(this, accountType, SalesforceSDKManager.getInstance().getLoginOptions(),
                SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).
                getRestClient(this, new ClientManager.RestClientCallback() {

            @Override
            public void authenticatedRestClient(RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(MainActivity.this);
                    return;
                }
                MainActivity.this.client = client;
                MainActivity.this.initServicesList();
            }
        });
    }

    /**
     * Creates and shows the security key dialog.
     */
    public void showSecurityKeyDialog(){
        DialogFragment dialog = new SecurityKeyDialogFragment();
        dialog.show(getFragmentManager(), "SecurityKeyDialogFragment");
    }

    /**
     * When correct security key is entered, writes security key to SharedPreferences,
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

    /**
     * Set the button and text to red. Button presses are enabled.
     */
    private void setButtonEnabled(Button button, final Class activity) {
        // This could be null if not logged in yet, in which case we just fail silently.
        if (button == null) { return; }
        button.setTextAppearance(getApplicationContext(), R.style.EnabledButtonText);
        button.setBackgroundResource(R.drawable.enabled_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    openActivity(activity);
                }
            });
    }

    /**
     * Starts the next Activity corresponding to the button.
     * @param activity: the next Activity to be started
     */
    private void openActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    /**
     * Set the button and text to gray. Button presses are still
     * enabled, but they will display a toast rather than opening the activity.
     */
    private void setButtonDisabled(Button button) {
        // This could be null if not logged in yet, in which case we just fail silently.
        if (button == null) { return; }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayRetryToast();
            }
        });
    }

    private void enableAllButtons(){
        setButtonEnabled(mServicesButton, ServicesActivity.class);
        setButtonEnabled(mCheckinButton, CheckinActivity.class);
        setButtonEnabled(mCheckoutButton, CheckoutActivity.class);
    }

    private void disableAllButtons(){
        setButtonDisabled(mServicesButton);
        setButtonDisabled(mCheckinButton);
        setButtonDisabled(mCheckoutButton);
    }

    /**
     * Tells the user that the Salesforce query
     * to get the service resources list has
     * not been completed.
     */
    private void displayRetryToast() {
        String message = getResources().getString(R.string.toast_retry_services);
        maybeShowToast(message, mToasts, Toast.LENGTH_SHORT, getApplication());
    }

    /**
     * Only shows a toast if it is not already being shown.
     */
    public static void maybeShowToast(String message, Toast[] toast, int duration,
                                      Context context) {
        if (toast[0] == null || toast[0].getView() == null) {
            toast[0] = Toast.makeText(context, message, duration);
        } else {
            toast[0].setText(message);
        }
        toast[0].show();
    }

    @Override
    /** Inflate the menu; this adds items to the action bar if it is present. */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
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

    /**
     * Helper that sends request to server and print result in text field.
     *
     * @param request - The request object that gets executed by the SF SDK
     * @param callback - The functions that get called when the response comes back
     *                   Modify UI elements here.
     */
    protected void sendRequest(RestRequest request, AsyncRequestCallback callback) {

        try { client.sendAsync(request, callback); }
        catch (Exception error) {
            Log.e("SF Request", error.toString());
        }
    }

    /**
     * Run this when the app starts up. This will create the object that contains
     * all services offered at the most recently created event object. Calls
     * describeServices when it receives a successful response.
     */
    private void initServicesList() {
        //Salesforce Object Query Language
        String soql = "SELECT id FROM PHC_EVENT__c ORDER BY createddate DESC LIMIT 1";
        try {
            RestRequest idRequest = RestRequest.getRequestForQuery(apiVersion, soql);

            AsyncRequestCallback callback = new AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    try {
                        JSONObject json = response.asJSONObject();
                        JSONObject item = (JSONObject) ((JSONArray)json.get("records")).get(0);
                        String id = item.getString("Id");
                        MainActivity.this.mEventId = id;
                        MainActivity.this.describeServices(id);
                    } catch (Exception e) {
                        Log.e("EventId Response Error", e.toString());
                    }
                }

                @Override
                public void onError(Exception exception) {
                    if (exception.getLocalizedMessage() != null) {
                        Log.e("EventId Response Error", exception.toString());
                    }
                }
            };

            sendRequest(idRequest, callback);


        } catch (Exception e) {
            Log.e("EventId Request Error", e.toString());
        }
    }

    /**
     * Created a describe request for the PHC_Resource__c object in the Salesforce
     * backend. This will give us the list of fields we need to query in the next step.
     *
     * @param eventId: The eventId returned from the previous step. This will be used in
     *               the next step
     */
    private void describeServices(final String eventId){
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

                        MainActivity.this.getServiceValues(eventId, fields);

                    } catch (Exception e) {
                        Log.e("Field Response Error", e.toString());
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e("Field Response Error 2", exception.toString());
                }
            };

            sendRequest(fieldRequest, callback);

        } catch (Exception e) {
            Log.e("Field Request Exception", e.toString());
        }
    }

    /**
     * Final step: uses the eventId to find the right PHC_Resource object and queries the
     * fields we found in Step 2 to find which services are available at the given event.
     * Places these into a Map where the key is the column name and the value is its display
     * value. Enables buttons if successful.
     *
     * @param eventId: The id of the event associated with the desired resource
     * @param fields: The fields we need to query, since SF doesn't support * notation... -_-
     */
    private void getServiceValues(final String eventId, final List<String> fields){
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
                                MainActivity.this.sOfferedServices.put(
                                        field, Utils.fieldNameHelper((field)));
                            }
                        }
                        Log.d("initialized", "initialized");
                        MainActivity.this.mInitialized = true;

                        sSalesforceNames = sOfferedServices.keySet().toArray(new
                                String[sOfferedServices.size()]);
                        sDisplayNames = sOfferedServices.values().toArray(new
                                String[sOfferedServices.size()]);
                        Arrays.sort(sSalesforceNames);
                        Arrays.sort(sDisplayNames);

                        enableAllButtons();

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
     * Checks the internet connection.  If there is none, it will create
     * an AlertDialogue that will force the user to reconnect.
     */
    public void checkConnectivity() {
        if(!isNetworkAvailable(this.getBaseContext())) {
            connectivityDialogue();
        }
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
     * Removes user credentials and returns to LoginActivity
     * Should be used when the session is over.
     */
    public void onLogoutClick() {
        mUserPreferences = getSharedPreferences(USER_PREFS_NAME,
                Context.MODE_PRIVATE);
        mUserPreferencesEditor = mUserPreferences.edit();
        mUserPreferencesEditor.remove("user_id");
        mUserPreferencesEditor.remove("auth_token");
        mUserPreferencesEditor.apply();

        // Go to LoginActivity
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);

        // Close this activity
        finish();

        //TODO: Remove this line when we remove salesforce sdk
        //SalesforceSDKManager.getInstance().logout(this);
    }

    public String getEventID() {
        return mEventId;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public static Context getContext() {
        return mContext;
    }

    public HashMap<String, String> getOfferedServices() { return sOfferedServices; }

    public String[] getSalesforceNames() { return sSalesforceNames; }

    public String[] getDisplayNames() { return sDisplayNames; }
}
