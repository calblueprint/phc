package phc.android;

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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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

public class ScannerActivity extends ActionBarActivity {

    public static final String TAG = "ScannerActivity";

    public static String mScanResult = null;
    private static final int ORIENTATION_90 = 1;
    private static final int ORIENTATION_0 = 0;
    private static final int ORIENTATION_180 = 2;
    private static final int ORIENTATION_270 = 3;

    public static class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        Camera mCamera;
        SurfaceHolder mHolder;
        Camera.Size mPreviewSize;
        FrameLayout mFrame;
        public CameraPreview(Camera camera, Context context, FrameLayout frame) {
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);
            mFrame = frame;
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        public void updateCamera(Camera c) {
            this.mCamera = c;
        }
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
            catch (IOException e) {
                Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
            }
        }
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // TODO: Implement for change/ rotate preview functionality
            boolean portrait = false;
            if (mHolder.getSurface() == null) {
                return;
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                //ignore, tried to stop non-existent preview
            }
            //TODO: FIX SCREEN ORIENTATION ISSUE!
            switch (getResources().getConfiguration().orientation) {
                case ORIENTATION_180:
                    mCamera.setDisplayOrientation(180);
                    break;
                case ORIENTATION_270:
                    mCamera.setDisplayOrientation(270);
                    break;
                case ORIENTATION_90:
                    mCamera.setDisplayOrientation(90);
                    portrait = true;
                    break;
                default:
                    portrait = true;
                    break;
            }
            Camera.Parameters parameters = mCamera.getParameters();
            //parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            int last = parameters.getSupportedPictureSizes().size() -1;
            Camera.Size picSize = parameters.getSupportedPictureSizes().get(7);//getSmallestPictureSize(w,h);
            parameters.setPictureSize(picSize.width,picSize.height);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            ViewGroup.LayoutParams slp = this.getLayoutParams();
            if (!portrait) {
                slp.width = mPreviewSize.width;
                slp.height = mPreviewSize.height;
            } else {
                slp.height = mPreviewSize.width;
                slp.width = mPreviewSize.height;
            }
            this.setLayoutParams(slp);
            mCamera.setParameters(parameters);
            try {
                mCamera.startPreview();
            } catch (Exception e) {
                Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
        private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio=(double)h / w;
            if (sizes == null) return null;
            Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;
            int targetHeight = h;
            for (Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);
            List<Camera.Size> mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            if (mSupportedPreviewSizes != null) {
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            }
        }
        private Camera.Size getBestPreviewSize(int width, int height)
        {
            Camera.Size result=null;
            Camera.Parameters p = mCamera.getParameters();
            for (Camera.Size size : p.getSupportedPreviewSizes()) {
                if (size.width<=width && size.height<=height) {
                    if (result==null) {
                        result=size;
                    } else {
                        int resultArea=result.width*result.height;
                        int newArea=size.width*size.height;
                        if (newArea<resultArea) {
                            result=size;
                        }
                    }
                }
            }
            return result;
        }
        private Camera.Size getSmallestPictureSize(int width, int height)
        {
            Camera.Size result=null;
            Camera.Parameters p = mCamera.getParameters();
            for (Camera.Size size : p.getSupportedPictureSizes()) {
                if (size.width<=width && size.height<=height) {
                    if (result==null) {
                        result=size;
                    } else {
                        int resultArea=result.width*result.height;
                        int newArea=size.width*size.height;
                        if (newArea<resultArea) {
                            result=size;
                        }
                    }
                }
            }
            return result;
        }
    }
    Camera mBackCamera;
    CameraPreview mPreview;
    TextView mResultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        // TODO: move camera open to async task
        // We should do this as soosn as the app starts
        mResultText = (TextView) findViewById(R.id.confirm_scan);
        FrameLayout fl = (FrameLayout) findViewById(R.id.camera_preview);
        acquireBackCamera();
        mPreview = new CameraPreview(mBackCamera, this, fl);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackCamera.startPreview();
                mBackCamera.takePicture(null,null,mPicture);
            }
        });
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }
    private PictureCallback mPicture = new PictureCallback() {
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
    private void returnSuccessfulResult(String result) {
        Intent scanResult = new Intent();
        scanResult.putExtra("scan_result", result);
        setResult(RESULT_OK, scanResult);
        finish();
    }
    private void returnCanceledResult() {
        Intent scanResult = new Intent();
        setResult(RESULT_CANCELED, scanResult);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public void onPause() {
        super.onPause();
        releaseBackCamera();
    }
    @Override
    public void onBackPressed() {
        if (mScanResult == null) {
            returnCanceledResult();
        } else {
            returnSuccessfulResult(mScanResult);
        }
        super.onBackPressed();
    }
    @Override
    public void onResume() {
        super.onResume();
        acquireBackCamera();
        mPreview.updateCamera(mBackCamera);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseBackCamera();
    }
    public void releaseBackCamera() {
        if (mBackCamera != null) {
            try {
                mBackCamera.stopPreview();
                mBackCamera.release();
            } catch (Exception e) {}
        }
    }
    public void acquireBackCamera() {
        try {
            mBackCamera = Camera.open();
        }
        catch (Exception e) {
// Already have camera? If so then continue, else throw error.
            if (mBackCamera == null) {
                System.exit(0);
            }
        }
    }
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
