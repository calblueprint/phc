package phc.android.Checkin;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import phc.android.Main.MainActivity;
import phc.android.R;

/**
 * CheckinActivity is the main activity for checking in a client.
 */
public class CheckinActivity extends Activity {

//    /*PASSED FROM MAIN ACTIVITY*/
//    /** Used to hold an instance of the MainActivity */
//    private MainActivity mMainActivity;
//    /** The id of the current PHC Event.*/
//    private String mEventId;
//    /** MainActivity's api version */
//    private String mApiVersion;
//
//    /** Hashmap of all services being offered at the event. */
//    private Map<String, String> mOfferedServices = new HashMap<String, String>();
//    /** Alphabetized array of Salesforce names for all services. */
//    private String[] mSalesforceNames;
//    /** Alphabetized array of display names for all services. */
//    private String[] mDisplayNames;

    /** Used to keep track of what kind of user we are modifying **/
    public static enum RegistrationState {NEW_USER, RETURNING_USER};
    public static RegistrationState currentState;
    protected RestClient client;

    /**
     * On creation of the activity, gets name of services from MainActivity,
     * launches the first fragment, and creates SharedPreferences file to store input data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
//        mMainActivity = (MainActivity) MainActivity.getContext();
//        mEventId = mMainActivity.getEventID();
//        mApiVersion = mMainActivity.getApiVersion();
//        mOfferedServices = mMainActivity.getOfferedServices();
//        mSalesforceNames = mMainActivity.getSalesforceNames();
//        mDisplayNames = mMainActivity.getDisplayNames();

        setContentView(R.layout.activity_checkin);
        currentState = RegistrationState.NEW_USER;

        SelectionFragment firstFragment = new SelectionFragment();
//        firstFragment.setArguments(getIntent().getExtras());
        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.add(R.id.checkin_fragment_container, firstFragment, getResources().getString(R.string.sidebar_selection));
        t.commit();
    }

    /**
     * Handles the setup of the Salesforce RestClient, allowing fragments in this activity to
     * make requests to the backend.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Login options
        String accountType = SalesforceSDKManager.getInstance().getAccountType();

        // Get a rest client
        new ClientManager(this, accountType, SalesforceSDKManager.getInstance().getLoginOptions(),
                SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {

            @Override
            public void authenticatedRestClient(RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(CheckinActivity.this);
                    return;
                }
                CheckinActivity.this.client = client;
            }
        });
    }

    public static RegistrationState getCurrentState() {

        return currentState;

    }

    public static void setCurrentState(RegistrationState currentState) {

        CheckinActivity.currentState = currentState;

    }
}
