package phc.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.james.mime4j.field.datetime.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity
                          implements SecurityKeyDialogFragment.SecurityKeyDialogListener{

    private String apiVersion;
    /** Used by ServiceActivity to perform REST requests */
    private static Context mContext;

    /** Hashmap of all services being offered at the event, where the Key is the Salesforce name
    of the service (e.g. "acupuncture__c") and the value is the converted name of the service (e.g.
    "accupuncture"). */
    private Map<String, String> resources = new HashMap<String, String>();
    private boolean initialized = false;

    /** SharedPreference file name for Security Key. */
    private static final String SECURITY_PREFS_NAME = "SecurityKey";
    /** SharedPreference object. */
    private SharedPreferences mSecurityKeyPreferences;
    /** SharedPreference editor object. */
    private SharedPreferences.Editor mSecurityKeyPreferencesEditor;
    /** Current stored Security Key. */
    private String mSecurityKey;

    /** Use to set resultCode when calling ServiceActivity to specify intention. */
    public static final int FOR_SERVICE = 0;
    public static final int FOR_REGISTRATION = 0;

    /** Holds the service provided by the user, selected in the ServiceActivity alert dialog. */
    private String mProvidedService;

    /** Holds the event Id of the most recently created PHC Event, treated in the app as the current
     * event. */
    private String mEventId = "";

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

        // ApiVersion
        apiVersion = getString(R.string.api_version);
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
        mContext = this;
        super.onCreate(savedInstanceState);
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkConnectivity();
        setServicesEnabled(initialized);
        setRegisterEnabled(initialized);
        setExitEnabled(initialized);
    }

    /**
     * Used to let the user know if the services list has
     * been initialized or not. Button presses are still
     * enabled and will display a toast rather than opening
     * services
     * @param enabled is True if initialized, False otherwise.
     */
    private void setServicesEnabled(boolean enabled) {
        Button servicesButton = (Button) findViewById(R.id.button_services);
        /* This could be null if not logged in,
         * in which case we just fail silently.
         */
        if (servicesButton == null) { return; }

        if (enabled) {
            servicesButton.setTextColor(getResources().getColor(R.color.button_text_color));
        } else {
            servicesButton.setTextColor(Color.GRAY);
        }
    }

    /**
     * Used to let the user know if the services list has
     * been initialized or not. Button presses are still
     * enabled and will display a toast rather than opening
     * services
     * @param enabled is True if initialized, False otherwise.
     */
    private void setRegisterEnabled(boolean enabled) {
        Button registerButton = (Button) findViewById(R.id.button_register);
        /* This could be null if not logged in,
         * in which case we just fail silently.
         */
        if (registerButton == null) { return; }

        if (enabled) {
            registerButton.setTextColor(getResources().getColor(R.color.button_text_color));
        } else {
            registerButton.setTextColor(Color.GRAY);
        }
    }

    /**
     * Used to let the user know if the services list has
     * been initialized or not. Button presses are still
     * enabled and will display a toast rather than opening
     * services
     * @param enabled is True if initialized, False otherwise.
     */
    private void setExitEnabled(boolean enabled) {
        Button exitButton = (Button) findViewById(R.id.button_exit);
        /* This could be null if not logged in,
         * in which case we just fail silently.
         */
        if (exitButton == null) { return; }

        if (enabled) {
            exitButton.setTextColor(getResources().getColor(R.color.button_text_color));
        } else {
            exitButton.setTextColor(Color.GRAY);
        }
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
     * Should log you out.
     */
    public void onLogoutClick() {

    }

    /** Handles the "Services" Button on the splash page. */
    public void openServices(View view) {
        Intent intent = new Intent(this, ServiceActivity.class);
        intent.putExtra("provided_service", mProvidedService);
        intent.putExtra("request_code", FOR_SERVICE);
        CharSequence[] services = null;
        HashMap<String, String> servicesHashMap = null;
        if (!initialized) {
            Log.d("MainActivity", "Resources list not initialized");
        } else {
            servicesHashMap = (HashMap<String, String>) getResourceList();
            services = servicesHashMap.values().toArray(new CharSequence[0]);
        }
        if (services != null) {
            intent.putExtra("services_list", services);
            intent.putExtra("services_hash", servicesHashMap);
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
        CharSequence message = getResources().getString(R.string.retry_services_toast);
        maybeShowToast(message, mDataFetchToast, Toast.LENGTH_SHORT, getApplication());
    }

    /**
     * Only shows a toast if it is not already being
     * shown.
     * @param toast
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
        intent.putExtra("services_hashmap", (HashMap) getResourceList());
        Log.d("Id", mEventId);
        intent.putExtra("event_id", mEventId);
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
         */

        if (requestCode == FOR_SERVICE) {
            if (resultCode == RESULT_CANCELED) {
                mProvidedService = data.getStringExtra("new_provided_service");
            }
            else if (resultCode == RESULT_OK) {
                mProvidedService = data.getStringExtra("new_provided_service");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("provided_service", mProvidedService);
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
        /*
        I left this is for reference on how to make a resource list.
        JSONObject json = response.asJSONObject();
        JSONObject item = (JSONObject) ((JSONArray)json.get("records")).get(0);
        String id = item.getString("Id");
        MainActivity.this.mEventId = id;
        MainActivity.this.describeResources(id);
        */
    }

    /**
     * Created a describe request for the PHC_Resource__c object in the Salesforce
     * backend. This will give us the list of fields we need to query in the next step.
     *
     * @param eventId: The eventId returned from the previous step. This will be used in
     *               the next step
     */
    private void describeResources(final String eventId){
        /* Reference */
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
        // Left for reference
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

    /**
     * Used by ServiceActivity to access
     * event ID
     * @return event id
     */
    protected String getEventID() {
        return mEventId;
    }

    /**
     * Used by ServiceActivity to access
     * api version
     * @return api version
     */
    protected String getApiVersion() {
        return apiVersion;
    }

   /**
    * Used by ServiceActivity to access
    * this activity's context
    * @return Context
    */
    protected static Context getContext() {
        return mContext;
    }

}
