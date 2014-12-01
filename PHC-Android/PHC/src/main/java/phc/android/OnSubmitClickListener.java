package phc.android;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.Map;

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
        writeToSalesforce();
        loadNextFragmentNew();
    }

    /**
     * Writes each entry in SharedPreferences to Salesforce and clears all entries when done.
     */
    private void writeToSalesforce(){
        Map<String, ?> keys = mUserInfo.getAll();

        //write to SF database.
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d(entry.toString(), entry.getValue().toString());
            //(convertToSalesForceKey(entry.toString()), entry.getValue().toString());
        }

        //clear SharedPreferences.
        mUserInfoEditor.clear();
        mUserInfoEditor.commit();
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