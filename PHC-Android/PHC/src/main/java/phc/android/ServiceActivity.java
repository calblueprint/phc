package phc.android;

import android.app.Activity;
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

public class ServiceActivity extends Activity {

    // Used in error logs to identify this activity.
    public static final String TAG = "ServiceActivity";

    // The result returned to the calling activity through an Intent.
    public static String mScanResult;

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
        setContentView(R.layout.activity_scanner);
        // TODO: move camera open to async task
        // We should do this as soon as the app starts
        mResultText = (TextView) findViewById(R.id.confirm_scan);
        FrameLayout fl = (FrameLayout) findViewById(R.id.camera_preview);
        acquireBackCamera();
        mPreview = new CameraPreview(mBackCamera, this, fl);
        mPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    mBackCamera.takePicture(null, null, mPicture);
                } catch (Exception e) {
                    //TODO: Find a better way to lock this!
                    // This handles the case where the user double
                    // taps the preview frame, so takePicture() fails
                    // since the preview has not been started.
                }
            }

        });

        //FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        fl.addView(mPreview);
    }

    /**
     * This is where we interface with the zxing library.
     */
    private PictureCallback mPicture = new PictureCallback() {

        /**
         * Once a picture is taken, this callback saves the file
         * to a local cache. Then, it is converted to a BinaryBitmap
         * so it can be passed to a MultiFormatReader in the zxing library
         * to obtain a string result.
         * @param data is the actual camera information
         * @param camera is the camera instance
         */
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            final int MEDIA_TYPE_IMAGE = 1;
            final int MEDIA_TYPE_VIDEO = 2;
            File pictureFile;
            //TODO: catch NPE and delete from cache!
            File outputDir = getApplicationContext().getCacheDir();
            try {
                pictureFile = File.createTempFile("prefix", "extension", outputDir);
            } catch (IOException e) {
                Log.d(TAG, "Error creating media file, check storage permissions: " + e.getMessage());
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            Bitmap bmpImage = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
            int pixels[] = new int[bmpImage.getHeight()*bmpImage.getWidth()];
            bmpImage.getPixels(
                    pixels,
                    0,
                    bmpImage.getWidth(),
                    0,
                    0,
                    bmpImage.getWidth() - 1,
                    bmpImage.getHeight() - 1);
            LuminanceSource source = new RGBLuminanceSource(
                    bmpImage.getWidth(),
                    bmpImage.getHeight(),
                    pixels);
            BinaryBitmap bitmap = new BinaryBitmap(
                    new HybridBinarizer(source));
            Reader reader = new MultiFormatReader();
            try{
                Result result = reader.decode(bitmap);
                mScanResult = result.getText();
                mResultText.setTextColor(Color.GREEN);
                mResultText.setText("SUCCESS! Result: " + mScanResult);
            } catch (ChecksumException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                mResultText.setTextColor(Color.RED);
                mResultText.setText("X TRY AGAIN");
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }
            mBackCamera.startPreview();
        }
    };

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
        mPreview.updateCamera(mBackCamera);
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
        if (mBackCamera != null) {
            try {
                mBackCamera.stopPreview();
                mBackCamera.release();
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
        try {
            // will access an instance of the back camera by default
            mBackCamera = Camera.open();
        }
        catch (Exception e) {
            // Already have camera? If so then continue, else throw error.
            if (mBackCamera == null) {
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
