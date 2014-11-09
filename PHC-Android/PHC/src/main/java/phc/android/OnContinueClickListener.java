package phc.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * OnContinueClickListener stores all view inputs into SharedPreferences
 * when the continue button is clicked.
 */
public class OnContinueClickListener
        extends SharedPreferenceEditorListener implements View.OnClickListener{

    /* The next fragment to be opened */
    Fragment mNextFrag;

    /**
     * Calls constructor of superclass to create SharedPreference instance
     * and keeps note of the next fragment to be launched.
     */
    public OnContinueClickListener(Context context, Fragment fragment) {
        super(context);
        mNextFrag = fragment;
    }

    /**
     * When continue button is clicked, updates SharedPreferences and loads the next fragment.
     */
    public void onClick(View view) {
        updateSharedPreferences(view);
        loadNextFragment();
    }

    /**
     * Updates SharedPreferences with (string, boolean) key-value pairs for each checkbox
     * and (string, string) key-value pairs for each spinner and text entry view.
     */
    private void updateSharedPreferences(View checkboxView){
        View v;
        String name;

        mLayout = (ViewGroup) checkboxView.getParent();
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            v = mLayout.getChildAt(i);
            name = mContext.getResources().getResourceEntryName(v.getId());

            if (v instanceof CheckBox) {
                boolean checked = ((CheckBox) v).isChecked();
                mUserInfoEditor.putBoolean(name, checked);
            } else if (v instanceof EditText) {
                String text = ((EditText) v).getText().toString();
                mUserInfoEditor.putString(name, text);
            } else if (v instanceof Spinner) {
                String selection = ((Spinner) v).getSelectedItem().toString();
                mUserInfoEditor.putString(name, selection);
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
        transaction.replace(R.id.registration_fragment_container, mNextFrag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}