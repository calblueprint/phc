package phc.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * RegisterActivity is the main activity for registering a client.
 * It calls all FormFragments.
 */
public class RegisterActivity extends Activity {

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
}
