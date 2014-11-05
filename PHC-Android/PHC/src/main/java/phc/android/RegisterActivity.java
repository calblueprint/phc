package phc.android;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * RegisterActivity is the main activity for registering a client.
 * It calls all FormFragments.
 */
public class RegisterActivity extends ActionBarActivity{

    /**
     * On creation of the activity, launches the first fragment,
     * creates SharedPreferences file to store input data, and
     * initializes checkbox fields to false.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.registration_fragment_container, new AccountRegistrationFragment());
        transaction.commit();
    }

    /**
     * Takes in a string ID and converts it to key format
     * used in the SalesForce database via the following changes:
     *   - add "__c" to the end of the field
     *   - replace nonalphanumeric characters (e.g. "/" and "-") with "_".
     */
    public String keyToKeyConverter(String key){
        key = key.replaceAll("[\\W\\s]","_") + "__c";
        return key;
    }
}
