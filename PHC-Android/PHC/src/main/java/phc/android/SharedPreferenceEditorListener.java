package phc.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;

/**
 * Superclass for all Listeners that edit SharedPreferences.
 */
public class SharedPreferenceEditorListener {
    /* SharedPreference file name. */
    protected static final String PREFS_NAME = "UserInputFile";
    /* SharedPreference object. */
    protected SharedPreferences mUserInfo;
    /* SharedPreference editor object. */
    protected SharedPreferences.Editor mUserInfoEditor;
    /* Context of the listener (i.e. the Activity using it)*/
    protected Context mContext;
    /* The ViewGroup (e.g. LinearLayout) that the button belongs to. */
    protected ViewGroup mLayout;

    /**
     * Constructor takes in the context of the button, grabs the context's
     * SharedPreferences file to store the user input.
     */
    public SharedPreferenceEditorListener(Context context) {
        mContext = context;
        mUserInfo = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mUserInfoEditor = mUserInfo.edit();
    }
}
