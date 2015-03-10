package phc.android.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

import phc.android.Checkin.CheckinActivity;
import phc.android.Checkout.CheckoutActivity;
import phc.android.Helpers.Utils;
import phc.android.Networking.RequestManager;
import phc.android.R;
import phc.android.Services.ServicesActivity;

public class MainActivity extends Activity {

    // User credentials
    // Key for user shared preferences
    private static final String USER_AUTH_PREFS_NAME = "UserKey";
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
    private static final String TAG = "MainActivity";

    /*BUTTONS*/
    /** Button leading to Services Activity. */
    private Button mServicesButton;
    /** Button leading to Checkin Activity. */
    private Button mCheckinButton;
    /** Button leading to Checkout Activity. */
    private Button mCheckoutButton;

    // Network request objects
    private static RequestManager sRequestManager;
    private static RequestQueue sRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        setContentView(R.layout.activity_main);

        //Set up Volley request framework
        sRequestQueue = Volley.newRequestQueue(this);
        sRequestManager = new RequestManager(TAG, sRequestQueue);

        getServices();

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
        // Todo: Remove once Checkout is fixed
        mCheckoutButton.setVisibility(View.GONE);

        if (!mInitialized){
            disableAllButtons();
        }
        else{
            enableAllButtons();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        // TODO: Uncomment once checkout is fixed
//        setButtonEnabled(mCheckoutButton, CheckoutActivity.class);
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
        if (id == R.id.action_logout) {
            onLogoutClick();
            return true;
        }
//        } else if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    private void getServices() {
        // Get userId and authToken
        mUserPreferences = getSharedPreferences(USER_AUTH_PREFS_NAME,
                Context.MODE_PRIVATE);
        final String userId = mUserPreferences.getString("user_id", null);
        final String authToken = mUserPreferences.getString("auth_token", null);

        sRequestManager.requestServices(
                userId,
                authToken,
                new ServicesResponseListener(),
                new ServicesErrorListener());
    }

    private class ServicesResponseListener implements Response.Listener<JSONArray> {

        @Override
        public void onResponse(JSONArray jsonArray) {
            try {
                sSalesforceNames = new String[jsonArray.length()];
                sDisplayNames = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    sSalesforceNames[i] = (String) jsonArray.get(i);
                    sDisplayNames[i] = Utils.fieldNameHelper((String) jsonArray.get(i));
                    sOfferedServices.put(sSalesforceNames[i], sDisplayNames[i]);
                }
                mInitialized = true;
                refreshButtons();
            } catch (JSONException e ) {
                Log.e(TAG, "Error parsing JSON");
                e.printStackTrace();
            }
        }
    }

    private class ServicesErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if (volleyError.getLocalizedMessage() != null) {
                Log.e(TAG, volleyError.toString());
            }

            Toast toast = Toast.makeText(getApplicationContext(), "Error getting services", Toast.LENGTH_SHORT);
            toast.show();
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
        mUserPreferences = getSharedPreferences(USER_AUTH_PREFS_NAME,
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
    }

    public String getEventID() {
        return mEventId;
    }

    public static Context getContext() {
        return mContext;
    }

    public HashMap<String, String> getOfferedServices() { return sOfferedServices; }

    public String[] getSalesforceNames() { return sSalesforceNames; }

    public String[] getDisplayNames() { return sDisplayNames; }
}
