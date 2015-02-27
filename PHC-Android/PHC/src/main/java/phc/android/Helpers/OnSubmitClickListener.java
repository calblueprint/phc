package phc.android.Helpers;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;

import phc.android.R;
import phc.android.SharedFragments.SuccessFragment;

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
        SuccessFragment successFragment = new SuccessFragment();
        successFragment.setType(SuccessFragment.SuccessType.CHECKIN_SUCCESS);
        transaction.replace(R.id.checkin_fragment_container, successFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
