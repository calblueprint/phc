package phc.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.security.PasscodeManager;

/**
 * RegisterActivity is the main activity for registering a client.
 * It calls all FormFragments.
 */
public class RegisterActivity extends Activity {

    protected RestClient client;
    private PasscodeManager passcodeManager;

    /**
     * On creation of the activity, launches the first fragment,
     * creates SharedPreferences file to store input data, and
     * initializes checkbox fields to false.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        // Passcode manager
        Log.d("Passcode Manager", "new");
        passcodeManager = SalesforceSDKManager.getInstance().getPasscodeManager();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.registration_fragment_container) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            // Create a new Fragment to be placed in the activity layout
            SelectionFragment firstFragment = new SelectionFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.add(R.id.registration_fragment_container, firstFragment, getResources().getString(R.string.sidebar_selection));
            t.commit();
        }
    }

    /**
     * Handles the setup of the Salesforce RestClient, allowing fragments in this activity to
     * make requests to the backend.
     */
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
                        SalesforceSDKManager.getInstance().logout(RegisterActivity.this);
                        return;
                    }
                    RegisterActivity.this.client = client;
                }
            });
        }
    }
}
