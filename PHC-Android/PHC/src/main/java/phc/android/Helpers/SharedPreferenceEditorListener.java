package phc.android.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Superclass for all Listeners that edit SharedPreferences.
 */
public class SharedPreferenceEditorListener {
    /** Client Info SharedPreferences file name. */
    public static final String CLIENT_INFO_PREFS_NAME = "ClientKey";
    /** Client Info SharedPreferences object. */
    protected SharedPreferences mClientPreferences;
    /** Client Info SharedPreferences editor object. */
    protected SharedPreferences.Editor mClientPreferencesEditor;
    /** Context of the listener (i.e. the Activity using it). */
    protected Context mContext;

    /**
     * Constructor takes in the context of the button, grabs the context's
     * SharedPreferences file to store the Client input.
     */
    public SharedPreferenceEditorListener(Context context) {
        mContext = context;
        mClientPreferences = context.getSharedPreferences(CLIENT_INFO_PREFS_NAME,
                Context.MODE_PRIVATE);
        mClientPreferencesEditor = mClientPreferences.edit();
    }
}
