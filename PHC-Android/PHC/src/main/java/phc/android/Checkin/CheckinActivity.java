package phc.android.Checkin;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;

import phc.android.R;

/**
 * CheckinActivity is the main activity for checking in a client.
 */
public class CheckinActivity extends Activity {

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

        setContentView(R.layout.activity_checkin);
        currentState = RegistrationState.NEW_USER;

        if (savedInstanceState == null) {
            SelectionFragment firstFragment = new SelectionFragment();
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.add(R.id.checkin_fragment_container, firstFragment, getResources().getString(R.string.sidebar_selection));
            t.commit();
        }
    }

    public static RegistrationState getCurrentState() {

        return currentState;

    }

    public static void setCurrentState(RegistrationState currentState) {

        CheckinActivity.currentState = currentState;

    }
}
