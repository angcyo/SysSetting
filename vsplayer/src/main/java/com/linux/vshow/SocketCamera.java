package com.linux.vshow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;

public class SocketCamera implements Camera.PreviewCallback {

	private Camera mCamera;
	private Camera.Parameters parameters;
	/** ���������� */
	private String username = "";
	private String ip;
	private int port;
	/** ��Ƶ���� ���߻Ῠ */
	private int VideoQuality = 40;
	/** ������Ƶ�ֱ��ʿ�� */
	private int VideoWidth = 240;
	/** ������Ƶ�ֱ��ʸ߶� */
	private int VideoHeight = 320;
	/** ��Ƶ��ʽ���� */
	private int VideoFormatIndex = 0;

	public SocketCamera(String username, String ip, int port) {
		username = username.replace(":", "-");
		this.username = username;
		this.ip = ip;
		this.port = port;
	}

	public void startMonitoring() {
		try {
			Thread th = new MySendCommondThread("PHONECONNECT|" + username
					+ "|");
			th.start();
			mCamera = Camera.open();
			parameters = mCamera.getParameters();
			Size size = parameters.getPreviewSize();
			VideoWidth = size.width;
			VideoHeight = size.height;
			VideoFormatIndex = parameters.getPreviewFormat();
			mCamera.setPreviewCallback(this);
			mCamera.startPreview();
		} catch (Exception e) {
		}
	}

	public void stopMonitoring() {
		if (null != mCamera) {
			// �Ͽ�����
			Thread th = new MySendCommondThread("PHONEDISCONNECT|" + username + "|");
			th.start();
			try {
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				mCamera.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		mCamera = null;
	}

	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		try {
			if (data != null) {
				YuvImage image = new YuvImage(data, VideoFormatIndex,
						VideoWidth, VideoHeight, null);
				if (image != null) {
					ByteArrayOutputStream outstream = new ByteArrayOutputStream();
					// �ڴ�����ͼƬ�ĳߴ������
					image.compressToJpeg(
							new Rect(0, 0, VideoWidth, VideoHeight),
							VideoQuality, outstream);
					outstream.flush();
					// �����߳̽�ͼ�����ݷ��ͳ�ȥ
					Thread th = new MySendFileThread(outstream, username, ip,
							port);
					th.start();
				}
			}
		} catch (IOException e) {

		}

	}

	/** ���������߳� */
	class MySendCommondThread extends Thread {
		private String commond;

		public MySendCommondThread(String commond) {
			this.commond = commond;
		}

		public void run() {
			Socket socket = null;
			PrintWriter out = null;
			// ʵ����Socket
			try {
				socket = new Socket(ip, port);
				out = new PrintWriter(socket.getOutputStream());
				out.println(commond);
				out.flush();
			} catch (Exception e) {
			}
			try {
				out.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				socket.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	/** �����ļ��߳� */
	class MySendFileThread extends Thread {
		private String username;
		private String ipname;
		private int port;
		private byte byteBuffer[] = new byte[1024];
		private OutputStream outsocket;
		private ByteArrayOutputStream myoutputstream;

		public MySendFileThread(ByteArrayOutputStream myoutputstream,
				String username, String ipname, int port) {
			this.myoutputstream = myoutputstream;
			this.username = username;
			this.ipname = ipname;
			this.port = port;
			try {
				myoutputstream.close();
			} catch (IOException e) {

			}
		}

		public void run() {
			Socket tempSocket = null;
			try {
				// ��ͼ������ͨ��Socket���ͳ�ȥ
				tempSocket = new Socket(ipname, port);
				outsocket = tempSocket.getOutputStream();
				// д��ͷ��������Ϣ
				String msg = java.net.URLEncoder.encode("PHONEVIDEO|"
						+ username + "|", "utf-8");
				byte[] buffer = msg.getBytes();
				outsocket.write(buffer);
				ByteArrayInputStream inputstream = new ByteArrayInputStream(
						myoutputstream.toByteArray());
				int amount;
				while ((amount = inputstream.read(byteBuffer)) != -1) {
					outsocket.write(byteBuffer, 0, amount);
				}
				myoutputstream.flush();
			} catch (IOException e) {

			}
			try {
				myoutputstream.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				outsocket.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				tempSocket.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
