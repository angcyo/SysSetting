package com.angcyo.camerademo;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by angcyo on 2015-04-09 009.
 */
public class Preview extends ViewGroup implements SurfaceHolder.Callback {


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
//        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//        parameters.setPreviewSize(400, 600);
        requestLayout();
//        mCamera.setParameters(parameters);

        // Important: Call startPreview() to start updating the preview surface.
        // Preview must be started before you can take a picture.
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;

    public Preview(Context context) {
//        super(context);
        this(context, null);
    }

    public Preview(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSurfaceView = new SurfaceView(context);
        mSurfaceView.setBackgroundColor(Color.parseColor("#FF0000"));
        addView(mSurfaceView,400, 600);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    Camera mCamera;
    List<Camera.Size> mSupportedPreviewSizes;

    public void setCamera(Camera camera) {
        if (mCamera == camera) {
            return;
        }

        stopPreviewAndFreeCamera();

        mCamera = camera;

        if (mCamera != null) {
            List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPreviewSizes = localSizes;
            for (Camera.Size size : localSizes) {
                Log.e("size", "w:" + size.width + " h:" + size.height);
            }

            requestLayout();
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            mCamera.startPreview();
        }
    }

    /**
     * 初始化一个相机的动作，总是从停止预览开始的。
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();
            mCamera = null;
        }
    }
}
