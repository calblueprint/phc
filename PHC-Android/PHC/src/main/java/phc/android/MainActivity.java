package phc.android;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity{

    private PasscodeManager passcodeManager;
    private String apiVersion;
    private RestClient client;
    private UserSwitchReceiver userSwitchReceiver;
    private Map<String, String> resources = new HashMap<String, String>();
    private boolean initialized = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Passcode manager
        passcodeManager = SalesforceSDKManager.getInstance().getPasscodeManager();

        //@TODO: Remove this line before production
        SalesforceSDKManager.getInstance().getLoginServerManager().useSandbox();

        // ApiVersion
        apiVersion = getString(R.string.api_version);

        userSwitchReceiver = new PHCUserSwitchReceiver();
        registerReceiver(userSwitchReceiver, new IntentFilter(UserAccountManager.USER_SWITCH_INTENT_ACTION));

        super.onCreate(savedInstanceState);
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
        if (!initialized) {
            loginSalesforce(true);
            initialized = true;
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

    //Calls Salesforce SDK to log out client.
    //Should be used when the session is over.
    public void onLogoutClick() {
        SalesforceSDKManager.getInstance().logout(this);
    }

    //Handles the "Services" Button on the splash page
    public void openServices(View view) {
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivityForResult(intent, 0);
    }
    //Handles the "Register" Button on the splash page
    public void openRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
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

    public Map<String, String> getResourceList() {
        return this.resources;
    }

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
                    Log.e("Id Response Error 2", exception.getLocalizedMessage());
                }
            };

            sendRequest(idRequest, callback);


        } catch (Exception e) {
            Log.e("Id Request Error", e.getLocalizedMessage());
        }
    }

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

    private static String fieldNameHelper(String columnName) {
        columnName = columnName.substring(0, columnName.length()-3);
        columnName = columnName.replace("_", " ");
        return columnName;
    }




}
