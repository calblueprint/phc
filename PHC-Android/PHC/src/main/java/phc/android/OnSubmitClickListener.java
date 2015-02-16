package phc.android;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;

/**
 * OnSubmitClickListener writes all entries to SalesForce DB and clears SharedPreferences.
 */
public class OnSubmitClickListener
        extends SharedPreferenceEditorListener implements View.OnClickListener {

    /**
     * Calls constructor of superclass to create SharedPreference instance.
     */
    public OnSubmitClickListener(Context context) {
        super(context);
    }

    /**
     * When submit button is clicked, writes SharedPreferences data to Salesforce,
     * clears SharedPreferences, and loads next fragment.
     */
    public void onClick(View view) {
        loadNextFragmentNew();
    }

    /**
     * Launches the success fragment.
     * TODO: prevent user from backtracking after success fragment loads.
     */
    private void loadNextFragmentNew(){
        FragmentTransaction transaction =
                ((Activity) mContext).getFragmentManager().beginTransaction();
        transaction.replace(R.id.registration_fragment_container, new SuccessFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
