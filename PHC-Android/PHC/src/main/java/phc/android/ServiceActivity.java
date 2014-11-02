package phc.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ServiceActivity extends Activity {

    // Used in error logs to identify this activity.
    public static final String TAG = "ServiceActivity";

    // The result returned to the calling activity through an Intent.
    public static String mScanResult;

    // A handle on the fragment that holds the camera to open and release it.
    public ScannerFragment mScannerFragment;

    // Holds an instance of the back camera on the device
    Camera mBackCamera;
    // Renders a preview for the user onto a FrameLayout
    CameraPreview mPreview;
    // Used to display "SUCCESS" or "TRY AGAIN"
    // TODO: Change to check and X assets.
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
        if (findViewById(R.id.service_fragment_container) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            //TODO: take care of these cases.
            // Create a new Fragment to be placed in the activity layout
            //AccountRegistrationFragment firstFragment = new AccountRegistrationFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            //firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            //FragmentTransaction t = getFragmentManager().beginTransaction();
            //t.add(R.id.registration_fragment_container, (Fragment) firstFragment);
            //t.commit();

            mScannerFragment = new ScannerFragment();
            mScannerFragment.setArguments(getIntent().getExtras());
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.add(R.id.service_fragment_container, (Fragment) mScannerFragment);
            t.commit();

        }
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
        setResult(RESULT_CANCELED, scanResult);
        finish();
    }

    /**
     * Not currently implemented. This will eventually
     * have the following options:
     * - change the service
     *
     * @param menu is passed in by the library
     * @return must be true for menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        // taken care of in fragment's lifecycle right now.
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
