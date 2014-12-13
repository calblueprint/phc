package phc.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;

/**
 * OnContinueClickListener stores all view inputs into SharedPreferences
 * when the continue button is clicked.
 */
public class OnContinueClickListener
        extends SharedPreferenceEditorListener implements View.OnClickListener{
    /** Current fragment holding the continue button. */
    private Fragment mCurrFrag;
    /** Layout whose view inputs will be stored to SharedPreferences. */
    private ViewGroup mLayout;
    /** Next fragment to navigate to. */
    private Fragment mNextFrag;
    /** Name of next fragment to navigate to. */
    private String mNextFragName;
    /** List of spinner descriptions. */
    private static String[] sSpinnerNames = {"Gender Identity...", "Ethnicity...",
                "Primary Language...", "Neighborhood of Residence..."};

    /**
     * Calls constructor of superclass to create SharedPreference instance
     * and keeps note of the next fragment to be launched.
     */
    public OnContinueClickListener
            (Context context, Fragment currFrag, ViewGroup currLayout, Fragment nextFrag,
             String nextFragName) {
        super(context);
        mCurrFrag = currFrag;
        mLayout = currLayout;
        mNextFrag = nextFrag;
        mNextFragName = nextFragName;
    }

    /**
     * When continue button is clicked, updates SharedPreferences and loads the next fragment.
     * SelectServicesFragment is handled by a different method because it has dynamically
     * added checkbox views.
     */
    public void onClick(View view) {
        if (mCurrFrag instanceof SelectServicesFragment) {
            updateSharedPreferencesServices(mLayout);
        } else {
            updateSharedPreferences(mLayout);
        }
        loadNextFragment();
    }

    /**
     * Updates SharedPreferences with (string, boolean) key-value pairs for each checkbox
     * and (string, string) key-value pairs for each spinner and EditText view.
     * Ignores TextView objects.
     */
    private void updateSharedPreferences(ViewGroup layout){
        View v;
        String name;

        for (int i = 0; i < layout.getChildCount(); i++) {
            v = layout.getChildAt(i);

            if (v instanceof CheckBox) {
                name = mContext.getResources().getResourceEntryName(v.getId());
                boolean checked = ((CheckBox) v).isChecked();
                mUserInfoEditor.putBoolean(name, checked);
            } else if (v instanceof EditText) {
                name = mContext.getResources().getResourceEntryName(v.getId());
                String text = ((EditText) v).getText().toString();
                mUserInfoEditor.putString(name, text);
            } else if (v instanceof Spinner) {
                name = mContext.getResources().getResourceEntryName(v.getId());
                String selection = ((Spinner) v).getSelectedItem().toString();
                if (!Arrays.asList(this.sSpinnerNames).contains(selection)) {
                    mUserInfoEditor.putString(name, selection);
                }
            } else if (v instanceof ViewGroup) {
                updateSharedPreferences((ViewGroup) v);
            }
        }
        mUserInfoEditor.commit();
    }

    /**
     * Updates SharedPreferences with (string, boolean) key-value pairs
     * for each service checkbox, where the key is the Salesforce name of the service.
     */
    private void updateSharedPreferencesServices(ViewGroup layout){
        String[] sorted_service_names = ((SelectServicesFragment) mCurrFrag).getServiceSFNames();

        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);

            if (v instanceof CheckBox) {
                boolean checked = ((CheckBox)v).isChecked();
                mUserInfoEditor.putBoolean(sorted_service_names[i], checked);
            }
        }
        mUserInfoEditor.commit();
    }

    /**
     * Loads next fragment onto the current stack.
     */
    private void loadNextFragment(){
        FragmentTransaction transaction =
                ((Activity) mContext).getFragmentManager().beginTransaction();
        transaction.replace(R.id.registration_fragment_container, mNextFrag, mNextFragName);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
