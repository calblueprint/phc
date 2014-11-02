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

public class MainActivity extends Activity{

    private PasscodeManager passcodeManager;
    private String apiVersion;
    private RestClient client;
    private UserSwitchReceiver userSwitchReceiver;



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
        loginSalesforce();
    }

    private void loginSalesforce() {
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

}
