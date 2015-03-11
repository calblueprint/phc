package phc.android.Checkin;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.salesforce.androidsdk.rest.RestClient;

import phc.android.R;

/**
 * CheckinActivity is the main activity for checking in a client.
 */
public class CheckinActivity extends Activity {

    // Used to keep track of whether or not to save form data
    public static enum FormDataState {
        CLEAR_DATA, SAVE_DATA
    };
    public static FormDataState currentState;
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
        // Anticipate to clear form data since we are starting a new check in
        currentState = FormDataState.CLEAR_DATA;

        if (savedInstanceState == null) {
            SelectionFragment firstFragment = new SelectionFragment();
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.add(R.id.checkin_fragment_container, firstFragment, getResources().getString(R.string.sidebar_selection));
            t.commit();
        }
    }

    public static FormDataState getCurrentState() {
        return currentState;
    }

    public static void setCurrentState(FormDataState currentState) {
        CheckinActivity.currentState = currentState;
    }
}
