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

    private final String mFragmentName;
    private final Fragment mNextFrag;

    /**
     * Calls constructor of superclass to create SharedPreference instance
     * and keeps note of the next fragment to be launched.
     */
    public OnContinueClickListener(Context context, Fragment fragment, String fragmentName) {
        super(context);
        mNextFrag = fragment;
        mFragmentName = fragmentName;
    }

    /**
     * When continue button is clicked, updates SharedPreferences and loads the next fragment.
     */
    public void onClick(View view) {
        updateSharedPreferences((ViewGroup) view.getParent());
        loadNextFragment();
    }

    /**
     * Updates SharedPreferences with (string, boolean) key-value pairs for each checkbox
     * and (string, string) key-value pairs for each spinner and EditText view.
     * Ignores TextView objects.
     */
    private void updateSharedPreferences(ViewGroup mLayout){
        View v;
        String name;

        for (int i = 0; i < mLayout.getChildCount(); i++) {
            v = mLayout.getChildAt(i);

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
                mUserInfoEditor.putString(name, selection);
            } else if (v instanceof ViewGroup) {
                updateSharedPreferences((ViewGroup) v);
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
        transaction.replace(R.id.registration_fragment_container, mNextFrag, mFragmentName);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
