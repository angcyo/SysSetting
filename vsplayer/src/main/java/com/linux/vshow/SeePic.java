package com.linux.vshow;

import java.io.FileOutputStream;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

public class SeePic {

	private Camera mCamera;
	private String filename;

	public SeePic() {
		try {
			mCamera = Camera.open();
			Camera.Parameters params = mCamera.getParameters();
			params.setJpegQuality(100);
			mCamera.setParameters(params);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	// ���շ���
	public void takePicture(String filename) {
		try {
			this.filename = filename;
			mCamera.takePicture(null, null, mPicture);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// PictureCallback�ص�����ʵ��
	private PictureCallback mPicture = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				FileOutputStream fos = new FileOutputStream(filename);
				fos.write(data);
				fos.close();
			} catch (Exception e) {

			}
		}
	};

	// �ͷ�Camera����
	public void releaseCamera() {
		if (mCamera != null) {
			try {
				mCamera.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		mCamera = null;
	}
}
