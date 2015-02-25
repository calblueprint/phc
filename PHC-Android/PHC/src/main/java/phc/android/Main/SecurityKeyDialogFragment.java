package phc.android.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import phc.android.R;

/**
 * SecurityKeyDialogFragment holds an AlertDialog that prompts the user for a security key to
 * access the rest of the app. The dialog appears only when the security key that is stored
 * in SharedPreferences is either null or no longer correct. If the input key is incorrect,
 * the dialog will trigger a cycle of new dialogs until the right key is entered.
 */
public class SecurityKeyDialogFragment extends DialogFragment {
    /** The Security Key. */
    //TODO: change to real security key
    public static final String SECURITY_KEY = "PHC++";
    /** Current activity that the fragment is in. */
    private Context mContext;
    /** Current activity that the fragment is in, typecasted into a Listener. */
    private SecurityKeyDialogListener mListener;
    /** String input into the alert dialog. */
    String mInputString;

    /** Define interface that MainActivity must implement to receive event callbacks. */
    public interface SecurityKeyDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    /** Override the Fragment.onAttach() method to instantiate the SecurityKeyDialogListener. */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SecurityKeyDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SecurityKeyDialogListener");
        }
    }

    /** Shows the alert dialog by calling showDialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        return showDialog();
    }

    /**
     * Creating and dismissing a dialog does not call the Activity's
     * on Create and onResume, so MainActivity's onResume behavior.
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d("dismissed", "dismissed");
        ((MainActivity)mContext).refreshButtons();
    }

    /**
     * Creates an alert dialog prompting user to input security key.
     * If the security key is wrong, opens another alert dialog saying "error" and a retry button
     * that takes the user to the security key prompt dialog again.
     */
    public Dialog showDialog() {
        //Creates LinearLayout for EditText for alert dialog
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(25,0,25,0);

        //Creates EditText for alert dialog
        final EditText input = new EditText(mContext);
        input.setLayoutParams(params);
        input.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        layout.addView(input, params);

        //Creates alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(layout)
               .setTitle(R.string.title_security)
               .setMessage(R.string.prompt_security)
               .setPositiveButton(R.string.button_submit, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       mInputString = input.getText().toString();
                       //Allow user to proceed when string input matches security key
                       if (mInputString.equals(SECURITY_KEY)) {
                           mListener.onDialogPositiveClick(SecurityKeyDialogFragment.this);
                       }
                       //Otherwise, create alert dialog showing "error" and a button to try again
                       else {
                           AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                           builder.setTitle(R.string.title_error);
                           builder.setMessage(R.string.text_incorrect_security);
                           builder.setPositiveButton(R.string.button_retry,
                                   new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int id) {
                                           showDialog();
                                       }
                                   });
                           builder.create().show();
                       }
                   }
               });

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
        return alertDialog;
    }

}
