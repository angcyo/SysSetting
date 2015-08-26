package com.zckj.connect;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class download {
	/**
	 * 连接url
	 */
	private String urlstr;
	/**
	 * sd卡目录路径
	 */
	private String sdcard;
	/**
	 * http连接管理类
	 */
	private HttpURLConnection urlcon;

	public download(String url) {
		this.urlstr = url;
		//获取设备sd卡目录
		this.sdcard = Environment.getExternalStorageDirectory() + "/";
		urlcon = getConnection();
	}

	/*
     * 读取网络文本
     */
	public String downloadAsString() {
		StringBuilder sb = new StringBuilder();
		String temp = null;
		try {
			InputStream is = urlcon.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/*
     * 获取http连接处理类HttpURLConnection
     */
	private HttpURLConnection getConnection() {
		URL url;
		HttpURLConnection urlcon = null;
		try {
			url = new URL(urlstr);
			urlcon = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return urlcon;
	}

	/*
     * 获取连接文件长度。
     */
	public int getLength() {
		return urlcon.getContentLength();
	}

	/*
     * 写文件到sd卡 demo
     * 前提需要设置模拟器sd卡容量，否则会引发EACCES异常
     * 先创建文件夹，在创建文件
     */
	public String down2sd(String dir, String filename, downhandler handler) {
		int BUFFER = 4096;
		BufferedOutputStream dest = null; //缓冲输出流
		StringBuilder sb = new StringBuilder(sdcard)
				.append(dir);
		File file = new File(sb.toString());
		if (!file.exists()) {
			file.mkdirs();
			//创建文件夹
			Log.d("log", sb.toString());
		} else {

		}
		//获取文件全名
		sb.append(filename);
		file = new File(sb.toString());
		int count;
		byte data[] = new byte[BUFFER];
		FileOutputStream fos = null;
		try {
			InputStream is = urlcon.getInputStream();
			//创建文件
			file.createNewFile();
			fos = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			dest = new BufferedOutputStream(fos, BUFFER);
			int allcount = getLength();
			int downcount = 0;
			while ((count = is.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, count);
				downcount += count;
				handler.setSize(downcount * 100 / allcount);
			}
			dest.flush();
			dest.close();
		} catch (Exception e) {
			return "";
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}


	public static void unzip(String zipFile, String targetDir) {
		if (!targetDir.endsWith("/")) {
			targetDir += "/";
		}
		int BUFFER = 4096; //这里缓冲区我们使用4KB，
		String strEntry; //保存每个zip的条目名称

		try {
			BufferedOutputStream dest = null; //缓冲输出流
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry; //每个zip条目的实例

			while ((entry = zis.getNextEntry()) != null) {
				try {
					Log.i("Unzip: ", "=" + entry);
					int count;
					byte data[] = new byte[BUFFER];
					strEntry = entry.getName();

					File entryFile = new File(targetDir + strEntry);
					if (!strEntry.contains(".")) {
						continue;
					}

					File entryDir = new File(entryFile.getParent());
					if (!entryDir.exists()) {
						entryDir.mkdirs();
					}

					FileOutputStream fos = new FileOutputStream(entryFile);
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = zis.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			zis.close();
		} catch (Exception cwj) {
			cwj.printStackTrace();
		}
	}

	/*
     * 内部回调接口类
     */
	public abstract class downhandler {
		public abstract void setSize(int size);
	}
}
