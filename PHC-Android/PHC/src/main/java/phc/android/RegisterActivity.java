package phc.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

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
        setContentView(R.layout.register);
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
            t.add(R.id.registration_fragment_container, (Fragment) firstFragment, getResources().getString(R.string.sidebar_selection));
            t.commit();
        }
    }


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_foster:
                if (checked){}
                break;
            case R.id.checkbox_military:
                if (checked){}
                break;
            case R.id.checkbox_doctor:
                if (checked){}
                break;
            case R.id.checkbox_children:
                if (checked){}
                break;
            case R.id.checkbox_homeless:
                if (checked){}
                break;
        }
    }

    //Responding to user selections for a Spinner object
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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
