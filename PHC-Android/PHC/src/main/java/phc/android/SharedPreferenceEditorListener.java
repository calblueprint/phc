package phc.android;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Superclass for all Listeners that edit SharedPreferences.
 */
public class SharedPreferenceEditorListener {
    /* User Input SharedPreferences file name. */
    public static final String USER_PREFS_NAME = "UserInputFile";
    /* User Input SharedPreferences object. */
    protected SharedPreferences mUserInfo;
    /* User Input SharedPreferences editor object. */
    protected SharedPreferences.Editor mUserInfoEditor;
    /* Context of the listener (i.e. the Activity using it)*/
    protected Context mContext;

    /**
     * Constructor takes in the context of the button, grabs the context's
     * SharedPreferences file to store the user input.
     */
    public SharedPreferenceEditorListener(Context context) {
        mContext = context;
        mUserInfo = context.getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE);
        mUserInfoEditor = mUserInfo.edit();
    }
}
