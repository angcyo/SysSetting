package com.angcyo.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by angcyo on 2015-04-09 009.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder holder;
    Camera myCamera;

    public MySurfaceView(Context context) {
        super(context);
        holder = getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
        holder.setKeepScreenOn(true);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (myCamera == null) {
            myCamera = Camera.open(0);//开启相机,不能放在构造函数中，不然不会显示画面.
            try {
                myCamera.setDisplayOrientation(90);
                myCamera.setPreviewDisplay(holder);
//                Camera.Parameters parameters = myCamera.getParameters();
//                parameters.setPreviewSize(320, 240);//不能随便设置
//                myCamera.setParameters(parameters);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        myCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myCamera.stopPreview();//停止预览
        myCamera.release();//释放相机资源
        myCamera = null;
    }
}
