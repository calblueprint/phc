package phc.android;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    /* The camera instance shared between the activity and this class */
    Camera mCamera;
    /* Used for the camera to render to this surface */
    SurfaceHolder mHolder;
    /* Holds the best preview size for the camera parameters */
    Camera.Size mPreviewSize;
    /* The XML layout frame that contains this preview */
    FrameLayout mFrame;
    /* tag for error logs */
    public final String TAG = "CameraPreview";

    public CameraPreview(Camera camera, Context context, FrameLayout frame) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mFrame = frame;
        /* Used in previous Android versions. */
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * Called after Camera.open() in order to keep instance references current.
     * @param c is the Camera instance passed in by ServiceActivity
     */
    public void updateCamera(Camera c) {
        this.mCamera = c;
    }

    /**
     * surfaceCreated() is called immediately after the
     * surface is first created, further initial rendering
     * code should be put here.
     * @param holder is automatically created and passed in
     * by the Android library code.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }
        catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    /**
     * @param holder is automatically created and passed in by the Android library code
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        /* Not implemented. If necessary, implement to stop
         * preview when replacing or destroying this surface.
         */
    }

    /**
     * Takes care of screen orientation and preview frame
     * resizing. Currently we are only changing the dimensions
     * of the FrameLayout to give the camera preview
     * the correct dimensions.
     * All params are passed in by the Android library code
     * @param holder holds the changed surface
     * @param format is the new PixelFormat of the surface
     * @param w is the new surface width
     * @param h is the new surface height
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // TODO: Implement for change/ rotate preview functionality
        boolean portrait = false;
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (mHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            //ignore, tried to stop non-existent preview
        }
        //TODO: FIX SCREEN ORIENTATION ISSUE!
        switch (manager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_180:
                mCamera.setDisplayOrientation(270);
                portrait = true;
                break;
            case Surface.ROTATION_270:
                mCamera.setDisplayOrientation(180);
                break;
            case Surface.ROTATION_90:
                mCamera.setDisplayOrientation(0);
                break;
            default:
                mCamera.setDisplayOrientation(90);
                portrait = true;
                break;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        //parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        //TODO: fix picture size!
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
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * Finds the best ratio of size that matches the
     * given height and width
     * @param sizes are passed in by the caller
     * @param w is the suggested min width
     * @param h is the suggested min height
     * @return the best camera size found
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double)h / w;
        if (sizes == null) {
            return null;
        }
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

    /**
     * Called when surface is changed or initialized
     * to set preview size.
     * @param widthMeasureSpec is passed in by library code
     * @param heightMeasureSpec is passed in by library code
     */
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

    /**
     * Currently not used. May eventually be used to
     * find the best preview size for the camera
     * preview frame.
     * Takes in a width and height and returns the found size
     */
    private Camera.Size getBestPreviewSize(int width, int height) {
        Camera.Size result = null;
        Camera.Parameters p = mCamera.getParameters();
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width*result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) {
                        result = size;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Currently not used. May eventually be used to
     * find the best picture size to pass to the zxing
     * library.
     * Takes in a width and height and returns the found size
     */
    private Camera.Size getSmallestPictureSize(int width, int height) {
        Camera.Size result = null;
        Camera.Parameters p = mCamera.getParameters();
        for (Camera.Size size : p.getSupportedPictureSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea=result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) {
                        result = size;
                    }
                }
            }
        }
        return result;
    }
}
