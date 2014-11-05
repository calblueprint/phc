package phc.android;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

/**
 * OnDynamicCheckboxClickListener stores the checked state for each
 * checkbox on the "Select Services" page to SharedPreferences when clicked.
 */

public class OnDynamicCheckboxClickListener
        extends SharedPreferenceEditorListener implements View.OnClickListener{

    /* Checkbox name (name of the field that dynamically created the checkbox). */
    String mName;

    /**
     * Calls constructor of superclass to create SharedPreference instance.
     */
    public OnDynamicCheckboxClickListener(Context context, String name) {
        super(context);
        mName = name;
    }

    /**
     * Updates SharedPreferences with a (string, boolean) key-value pair
     * for each checkbox.
     * TODO: CHANGE STRING TO BE THE NAME OF THE SALESFORCE KEY.
     */
    public void onClick(View view){
        boolean checked = ((CheckBox) view).isChecked();
        mUserInfoEditor.putBoolean(mName, checked);
        mUserInfoEditor.commit();
    }
}
