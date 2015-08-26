package com.angcyo.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Setting {
	static final String SHARED_FILE_NAME = "ConnectAdmin";

	public static final String KEY_FIRST_RUN = "key_first_run";//首次运行
	public static final String KEY_SERVICE_IP = "key_service_ip";//服务器IP
	public static final String KEY_DEVICE_NAME = "key_device_name";//设备名称
	public static final String KEY_PORT = "key_port";//端口

	public static SharedPreferences getSP(Context context) {
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SHARED_FILE_NAME, Context.MODE_MULTI_PROCESS);
		return sp;
	}

	/**
	 * 是否是首次运行
	 */
	public static boolean isFirstRun(Context context) {
		return getSP(context).getBoolean(KEY_FIRST_RUN, true);
	}

	public static void setFirstRun(Context context, boolean b) {
		getSP(context).edit().putBoolean(KEY_FIRST_RUN, b).commit();
	}

	/**
	 * 获取服务器的IP
	 */
	public static String getServiceIp(Context context) {
		return getSP(context).getString(KEY_SERVICE_IP, "");
	}

	public static void setServiceIp(Context context, String ip) {
		getSP(context).edit().putString(KEY_SERVICE_IP, ip).commit();
	}

	/**
	 * 设备的名称
	 */
	public static String getDeviceName(Context context) {
		return getSP(context).getString(KEY_DEVICE_NAME, "android_" + System.currentTimeMillis());
	}

	public static void setDeviceName(Context context, String name) {
		getSP(context).edit().putString(KEY_DEVICE_NAME, name).commit();
	}

	/**
	 * 端口
	 */
	public static int getPort(Context context) {
		return getSP(context).getInt(KEY_PORT, 9925);
	}

	public static void setPort(Context context, int port) {
		getSP(context).edit().putInt(KEY_PORT, port).commit();
	}
}
