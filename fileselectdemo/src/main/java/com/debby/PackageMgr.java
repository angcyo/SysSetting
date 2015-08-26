package com.debby;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class PackageMgr {

	/**
	 * 
	 * 根据包名,获取应用程序信息
	 * 
	 * @param packageName
	 *            程序的包名
	 * @return
	 */
	public static ApplicationInfo getAppInfo(Context context, String packageName) {
		if (packageName == null) {
			return null;
		}
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(
					packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (appInfo == null) {
			return null;
		}
		return appInfo;
	}

	/**
	 * 返回应用程序的名称
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static String getApplicationLabel(Context context, String packageName) {
		ApplicationInfo appInfo = getAppInfo(context, packageName);
		return appInfo.loadLabel(context.getPackageManager()).toString();
	}

	/**
	 * 返回应用程序的图标
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static Drawable getApplicationIco(Context context, String packageName) {
		ApplicationInfo appInfo = getAppInfo(context, packageName);
		return appInfo.loadIcon(context.getPackageManager());
	}

	/**
	 * 得到当前运行的组件信息,如果系统为棒棒糖,则返回当前程序的组件信息
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ComponentName getCurrentActivityInfo(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		ComponentName componentInfo = null;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			List<ActivityManager.RunningTaskInfo> taskInfo = am
					.getRunningTasks(1);

			componentInfo = taskInfo.get(0).topActivity;

			return componentInfo;
		}
		componentInfo = new ComponentName(context.getPackageName(), null);
		return componentInfo;
	}

	/**
	 * 得到当前运行程序的Activity名称,包含包名+Activity的一个字符串
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentActivityName(Context context) {

		ComponentName componentInfo = getCurrentActivityInfo(context);

		// Log.i("应用程序标签名",
		// getApplicationLabel(componentInfo.getPackageName()));
		// Log.i("ComponentName", componentInfo.getPackageName());
		// Log.i("ComponentName", componentInfo.toString());
		// Log.i("ComponentName", componentInfo.toShortString());

		return componentInfo.getClassName();

	}

	/**
	 * 得到当前应用程序的标签
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentAppLabel(Context context) {
		ComponentName componentInfo = getCurrentActivityInfo(context);
		return getApplicationLabel(context, componentInfo.getPackageName());
	}

	/**
	 * 得到当前应用程序的图标
	 * 
	 * @param context
	 * @return
	 */
	public static Drawable getCurrentAppIco(Context context) {
		ComponentName componentInfo = getCurrentActivityInfo(context);
		return getApplicationIco(context, componentInfo.getPackageName());
	}

	/**
	 * 得到当前组件的包名
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentAppPackName(Context context) {
		ComponentName componentInfo = getCurrentActivityInfo(context);
		return componentInfo.getPackageName();
	}

	public static List<AppInfo> getInstalledPackages(Context context) {
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		List<PackageInfo> packInfos = context.getPackageManager()
				.getInstalledPackages(0);

		for (PackageInfo packageInfo : packInfos) {
			AppInfo appInfo = new AppInfo();
			appInfo.setStrAppLabel(packageInfo.applicationInfo.loadLabel(
					context.getPackageManager()).toString());
			appInfo.setStrPackName(packageInfo.packageName);
			appInfo.setStrVerName(packageInfo.versionName);
			appInfo.setnVerCode(packageInfo.versionCode);
			appInfo.setDwAppIco(packageInfo.applicationInfo.loadIcon(context
					.getPackageManager()));
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appInfo.setSysApp(true);
			}
			appInfos.add(appInfo);
		}

		return appInfos;
	}

}
