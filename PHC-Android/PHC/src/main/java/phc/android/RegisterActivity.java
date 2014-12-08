package phc.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * RegisterActivity is the main activity for registering a client.
 */
public class RegisterActivity extends Activity {
    /** Hashmap of all services being offered at the event. */
    private HashMap<String,String> mServices;
    /** Sorted array of all service salesforce names (keys of the hashmap). */
    private String[] mServiceSFNames;

    /**
     * On creation of the activity, gets name of services from MainActivity,
     * launches the first fragment, and creates SharedPreferences file to store input data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mServices = (HashMap<String,String>) intent.getSerializableExtra("services_hashmap");
        mServiceSFNames = mServices.keySet().toArray(new String[0]);
        Arrays.sort(mServiceSFNames);

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
     * Static method that returns a map with all of the resources for the most recent event.
     * @return HashMap of resources. Key = Salesforce Field name; Value = Display Name. ;
     */
    public HashMap<String, String> getServices() {
        return this.mServices;
    }

    /** Static method that returns a string array of salesforce names for all services. */
    public String[] getServiceSFNames() { return this.mServiceSFNames; }
}
