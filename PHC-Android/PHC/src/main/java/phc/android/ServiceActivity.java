package phc.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/* Call with resultCode of 0 if providing a service
 * Call with resultCode of 1 if scanning for registration
 */
public class ServiceActivity extends Activity {

    /* Used to hold which service this user is scanning for */
    public String mServiceSelected;

    /* Created when selecting the provided service */
    public AlertDialog mServiceDialog;

    /* Used in error logs to identify this activity. */
    public static final String TAG = "ServiceActivity";

    /* The result returned to the calling activity through an Intent. */
    public static String mScanResult;

    /* A handle on the fragment that holds the camera to open and release it. */
    public ScannerFragment mScannerFragment;

    /* Holds the menu item generated in onPrepareOptionsMenu() */
    private MenuItem mServiceMenuItem;

    private CharSequence[] services;

    /* Used to display "SUCCESS" or "TRY AGAIN" */
    // TODO: Change to check and X assets.
    /* Not used right now, but we will take this out after moving to assets */
    TextView mResultText;

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
        setContentView(R.layout.service);
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.service_fragment_container) != null) {
            /* However, if we're being restored from a previous state,
             * then we don't need to do anything and should return or else
             * we could end up with overlapping fragments.
             */
            if (savedInstanceState != null) {
                return;
            }

            mScannerFragment = new ScannerFragment();
            mScannerFragment.setArguments(getIntent().getExtras());
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.add(R.id.service_fragment_container, mScannerFragment);
            t.commit();

            Bundle bundle = getIntent().getExtras();
            int intention = (Integer) bundle.get("request_code");
            /* This cannot be null! */
            services = bundle.getCharSequenceArray("services_list");
            if (bundle.get("provided_service") == null && intention == MainActivity.FOR_SERVICE) {
                showSelectServiceDialog(null, true);
            } else {
                mServiceSelected = (String) bundle.get("provided_service");
            }
        }
    }


    /**
     * Displays a dialog box that prompts the user to select the service
     * that they provide.
     * @param previousService is null if a service has not been
     * selected, the service name otherwise
     * @param mustSelect is True if the user has not already selected a service.
     */
    private void showSelectServiceDialog(String previousService, final boolean mustSelect) {
        final CharSequence[] services = getResources().getStringArray(R.array.services_array);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Provided Service");
        /* if service is already selected, pre select a button. */
        int prevIndex = -1;
        if (previousService != null) {
            for (int i = 0; i < services.length; i++) {
                if (previousService.equals(services[i])) {
                    prevIndex = i;
                }
            }
        }
        builder.setSingleChoiceItems(services, prevIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                mServiceSelected = services[item].toString();
                mServiceDialog.dismiss();
            }
        });
        /* handle back button press */
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mustSelect) {
                    onBackPressed();
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        mServiceDialog = builder.create();
        mServiceDialog.setCanceledOnTouchOutside(false);
        mServiceDialog.show();
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
        mServiceMenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.change_service);
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
     * Called when phone goes to sleep, user opens
     * another app, or pressed the home button.
     */
    @Override
    public void onPause() {
        super.onPause();
        releaseBackCamera();
    }

    /**
     * onBackPressed() overrides the default back button
     * functionality. It ensures that the calling activity
     * will receive the appropriate result if the user
     * returns using the back button.
     */
    @Override
    public void onBackPressed() {
        if (mScanResult == null) {
            returnCanceledResult();
        } else {
            returnSuccessfulResult(mScanResult);
        }
        super.onBackPressed();
    }

    /**
     * Called when activity is re opened.
     * Camera must be acquired again, and
     * the preview's camera handle should
     * be updated as well.
     */
    @Override
    public void onResume() {
        super.onResume();
        acquireBackCamera();
        //TODO: make sure this doesn't need to be updated!
        /* taken care of in fragment's lifecycle right now. */
        //mPreview.updateCamera(mBackCamera);
    }

    /**
     * Called when activity is finished or terminated by user.
     * Camera MUST be released so other activities can use it!
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseBackCamera();
    }

    /**
     * Called when activity is paused and destroyed
     * in order to release resources for other activities
     * to use.
     */
    public void releaseBackCamera() {
        if (mScannerFragment != null) {
            try {
                mScannerFragment.releaseBackCamera();
            } catch (Exception e) {
                //TODO: is this too general?
            }
        }
    }

    /**
     * Called to initially access camera, and after release()
     * to reinitialize a handle on the camera instance
     */
    public void acquireBackCamera() {
        if (mScannerFragment != null) {
            try {

                mScannerFragment.acquireBackCamera();
            } catch (Exception e) {
                System.exit(0);
            }
        }
    }


    /**
     * Handles item selection in the menu.
     * @param item is the selected item
     * @return true if the action is consumed here,
     * false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         */
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == mServiceMenuItem.getItemId()) {
            /* User does not have to select another service option */
            showSelectServiceDialog(mServiceSelected, false);
        }
        return super.onOptionsItemSelected(item);
    }
}
