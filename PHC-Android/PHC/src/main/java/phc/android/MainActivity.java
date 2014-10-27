package phc.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.salesforce.androidsdk.accounts.UserAccountManager;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.security.PasscodeManager;
import com.salesforce.androidsdk.util.UserSwitchReceiver;

public class MainActivity extends ActionBarActivity {

    private PasscodeManager passcodeManager;
    private String apiVersion;
    private RestClient client;
    private UserSwitchReceiver userSwitchReceiver;


    AlertDialog logoutConfirmationDialog;
    private static final int LOGOUT_CONFIRMATION_DIALOG_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Passcode manager
        passcodeManager = SalesforceSDKManager.getInstance().getPasscodeManager();

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
        }
        return super.onOptionsItemSelected(item);
    }

    //Handles the "Register" Button on the splash page
    public void openRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Helper that sends request to server and print result in text field
     *
     * @param request
     */
    private void sendRequest(RestRequest request, RestClient.AsyncRequestCallback callback) {

        try {

            sendFromUIThread(request, callback);
            // response is printed by RestCallTask:onPostExecute
        } catch (Exception error) {
            Log.e("SF Request Error", error.toString());
        }
    }

    /**
     * Send restRequest using RestClient's sendAsync method.
     * Note: Synchronous calls are not allowed from code running on the UI thread.
     * @param restRequest
     */
    private void sendFromUIThread(RestRequest restRequest, RestClient.AsyncRequestCallback callback) {
        client.sendAsync(restRequest, callback);
    }


    public void onLogoutClick(View v) {
        showDialog(LOGOUT_CONFIRMATION_DIALOG_ID);
    }


    /**
     * Refreshes the client if the user has been switched.
     */
    private void refreshIfUserSwitched() {
        if (passcodeManager.onResume(this)) {
            final String accountType = SalesforceSDKManager.getInstance().getAccountType();

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
                }
            });
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == LOGOUT_CONFIRMATION_DIALOG_ID) {
            logoutConfirmationDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.logout_title)
                    .setPositiveButton(R.string.logout_yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    SalesforceSDKManager.getInstance().logout(MainActivity.this);
                                }
                            })
                    .setNegativeButton(R.string.logout_cancel, null)
                    .create();
            return logoutConfirmationDialog;
        }
        return super.onCreateDialog(id);
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

}
