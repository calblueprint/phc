package phc.android.Services;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.auth.HttpAccess;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import phc.android.SharedFragments.ScannerConfirmationFragment;
import phc.android.SharedFragments.ScannerFragment;
import phc.android.Main.MainActivity;
import phc.android.R;

/**
 * ServicesActivity is the main activity for registering users for services.
 */
public class ServicesActivity extends Activity {

    /*TAKEN FROM MAIN ACTIVITY*/
    /** Used to hold an instance of the MainActivity */
    private MainActivity mMainActivity;
    /** The id of the current PHC Event.*/
    private String mEventId;
    /** MainActivity's api version */
    private String mApiVersion;

    /** Hashmap of all services being offered at the event. */
    private static HashMap<String, String> sOfferedServices = new HashMap<String, String>();
    /** Alphabetized array of display names for all services. */
    private static String[] sDisplayNames;
    // Button used to change services
    private Button mChangeServiceButton;

    /*SERVICE DETAILS*/
    /** TextView showing the user their service. */
    private TextView mServicePrompt;
    /** Holds which service this user is scanning for. */
    public static String mServiceSelected;
    /** Used in error logs to identify this activity. */
    public static final String TAG = "ServicesActivity";

    /*DIALOGS AND SCANNERS*/
    /** Created when selecting the provided service */
    public static AlertDialog mServiceDialog;
    /** Created to tell the user they submitted an invalid code */
    public static AlertDialog mFailureDialog;
    /** The result returned to the calling activity through an Intent. */
    public static String mScanResult;
    /** Holds the menu item generated in onPrepareOptionsMenu(). */
    private MenuItem mServiceMenuItem;

    /*SHARED PREFERENCES*/
    /** Used to hold scan results. */
    private SharedPreferences mSharedPreferences;
    /** String tag for scanned codes in shared preferences */
    private final String ALL_CODES = "scanned_codes";


    /**
     * As soon as the activity starts, this sets up
     * the camera preview and the listener for the camera
     * preview frame.
     * @param savedInstanceState is passed in by the calling
     * activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        mMainActivity = (MainActivity) MainActivity.getContext();
        mEventId = mMainActivity.getEventID();
        mApiVersion = mMainActivity.getApiVersion();
        sOfferedServices = mMainActivity.getOfferedServices();
        sDisplayNames = mMainActivity.getDisplayNames();

        Bundle intent = getIntent().getExtras();

        if (savedInstanceState != null) {
            mServiceSelected = (String) savedInstanceState.getCharSequence("provided_service");
            return;
        }
        else if (intent != null && intent.get("provided_service") != null) {
            mServiceSelected = (String) intent.get("provided_service");
        }
        else {
            showSelectServiceDialog(true);
        }

        mSharedPreferences = getPreferences(MODE_PRIVATE);
        ScannerFragment scannerFragment = new ScannerFragment();
        scannerFragment.setType(ScannerFragment.FlowType.SERVICES);
        scannerFragment.setArguments(getIntent().getExtras());
        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.add(R.id.service_fragment_container, scannerFragment);
        t.commit();

        mChangeServiceButton = (Button) findViewById(R.id.change_service_button);
        mChangeServiceButton.setOnClickListener(new ChangeServiceOnClickListener());
    }

    /**
     * Uses a DialogFragment to display an AlertDialog to the user.
     * The newInstance() method must be used to create a new instance.
     */
    public static class ServiceAlertDialogFragment extends DialogFragment {

        static boolean mustSelect = false;
        private String selected;

        /**
         * Used to create a new instance of this fragment. Only one instance
         * should be used at any given time as this is a static class.
         * @param mustSelect is True if the user has not already selected a service,
         * False otherwise
         * @return
         */
        public static ServiceAlertDialogFragment newInstance(final boolean mustSelect) {
            ServiceAlertDialogFragment frag = new ServiceAlertDialogFragment();
            ServiceAlertDialogFragment.mustSelect = mustSelect;
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return createSelectServiceDialog();
        }

        /**
         * Override the onCancel method in the DialogFragment
         * to prevent cancellation when service has not been
         * set.
         * @param dialogInterface
         */
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if (mustSelect) {
                getActivity().onBackPressed();
            } else {
                dialogInterface.dismiss();
            }
        }

        /**
         * @return a Dialog that will be shown to the user.
         */
        private Dialog createSelectServiceDialog() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Provided Service");
            /* if service is already selected, pre select a button. */
            int prevIndex = -1;

            if (mServiceSelected != null) {
                for (int i = 0; i < sDisplayNames.length; i++) {
                    if (mServiceSelected.equals(sDisplayNames[i])) {
                        prevIndex = i;
                    }
                }
            }
            builder.setSingleChoiceItems(sDisplayNames, prevIndex, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int item) {
                    mServiceSelected = sDisplayNames[item].toString();
                    ((ServicesActivity) getActivity()).setServicePromptText();
                    mServiceDialog.dismiss();
                }
            });
            /* handle back button press */
            mServiceDialog = builder.create();
            mServiceDialog.setCanceledOnTouchOutside(false);
            return mServiceDialog;
        }
    }

    /**
     * Displays a dialog box that prompts the user to select the service
     * that they provide.
     * @param mustSelect is True if the user has not already selected a service.
     */
    public void showSelectServiceDialog(final boolean mustSelect) {
        DialogFragment newFrag = ServiceAlertDialogFragment.newInstance(mustSelect);
        newFrag.show(getFragmentManager(), "ServiceDialog");
    }

    /**
    * Lets the calling activity know that a valid
    * QR code was received. This valid code may be
    * overwritten multiple times before it is
    * returned to the calling activity.
    * @param result is the decoded string
    * @return no return value, uses Intent to communicate
    */
    private void returnSuccessfulResult(String result) {
        Intent scanResult = new Intent();
        scanResult.putExtra("scan_result", result);
        scanResult.putExtra("new_provided_service", mServiceSelected);
        setResult(RESULT_OK, scanResult);
        finish();
    }

    /**
     * Lets the calling activity know that a valid
     * QR code was not received before the user
     * returned using the back button.
     * @return no return value, uses Intent to communicate
     */
    private void returnCanceledResult() {
        Intent scanResult = new Intent();
        scanResult.putExtra("new_provided_service", mServiceSelected);
        setResult(RESULT_CANCELED, scanResult);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        setServicePromptText();
    }

    /**
     * Updates the service prompt text view for the current
     * service selected.
     */
    private void setServicePromptText() {
        mServicePrompt = (TextView) findViewById(R.id.service_prompt_text);
        String prompt = getString(R.string.text_welcome_service);
        mServicePrompt.setText(Html.fromHtml(prompt + "<br />" + "<b>" + mServiceSelected + "<b>"));
    }

    /**
     * Dynamically clears the options menu and adds the option to
     * change a service.
     * @param menu is passed in by the library.
     * @return true so that menu is created.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        /* These parameters are the groupID, itemID, order, and text for the item */
        mServiceMenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.button_change_service);
        return true;
    }
    /**
     * Not currently used. onPrepareOptionsMenu is
     * used instead to add options.
     * @param menu is passed in by the library
     * @return must be true for menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * onBackPressed() overrides the default back button
     * functionality. It ensures that the calling activity
     * will receive the appropriate result if the user
     * returns using the back button.
     */
    @Override
    public void onBackPressed() {
        ScannerConfirmationFragment frag = (ScannerConfirmationFragment) getFragmentManager().
                findFragmentByTag(ScannerConfirmationFragment.TAG);
        if (frag != null && frag.isVisible()) {
            /* Return to the scanner fragment if
             * back button is pressed in confirmation
             * fragment
             */
            frag.whenBackPressed();
            return;
        } else {
            if (mScanResult == null) {
                returnCanceledResult();
            } else {
                returnSuccessfulResult(mScanResult);
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("provided_service", mServiceSelected);
    }

    /**
     * Displays a success message at the bottom of
     * the screen.
     */
    protected void showSuccessToast() {
        Context c = getApplicationContext();
        CharSequence message = getResources().getString(R.string.toast_scan_success);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }

    /** Shows the previous registration status through
     * a MESSAGE for the service TITLE. */
    private void showPreviousState(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Listens for clicks to the change service button
     */
    private class ChangeServiceOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            showSelectServiceDialog(false);
        }
    }

    public String getServiceSelected(){ return mServiceSelected; }

}
