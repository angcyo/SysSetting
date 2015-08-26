package com.angcyo.util;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by angcyo on 2015-03-25 025.
 */
public class Util {
    /**
     * 显示Toast消息
     *
     * @param msg 需要显示的内容
     */
    public static void showPostMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 去除字符串左右的字符
     */
    public static String trimMarks(String des) {
        return trimMarks(des, 1);
    }

    /**
     * 去除字符串左右指定个数的字符
     */
    public static String trimMarks(String des, int count) {
        if (des == null || count < 0 || des.length() < count + 1) {
            return des;
        }
        return des.substring(count, des.length() - count);
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.trim().length() == 0)
            return true;
        return false;
    }

    /**
     * 组装string
     */
    public static String groupString(String... str) {
        return groupString(false, str);
    }

    /**
     * 组装string
     */
    public static String groupString(boolean isSplit, String... str) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            stringBuilder.append(str[i]);
            if (isSplit && str.length > 2 && i < str.length - 1) {
                stringBuilder.append("|");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 返回现在的时间,不包含日期
     */
    public static String getNowTime() {
        return getNowTime("HH:mm:ss");
    }

    public static String getNowTime(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date());
    }

    /**
     * 判断是否有SD卡
     */
    public static boolean isSD() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 返回SD卡路径
     */
    public static String getSDPath() {
        return isSD() ? Environment
                .getExternalStorageDirectory().getPath() : Environment
                .getDownloadCacheDirectory().getPath();
    }

    /**
     * 格式化数据单位
     */
    public static String prettySize(long size) {
        if (size / (1024 * 1024 * 1024) > 0) {
            float tmpSize = (float) (size) / (float) (1024 * 1024 * 1024);
            DecimalFormat df = new DecimalFormat("#.##");
            return "" + df.format(tmpSize) + "GB";
        } else if (size / (1024 * 1024) > 0) {
            float tmpSize = (float) (size) / (float) (1024 * 1024);
            DecimalFormat df = new DecimalFormat("#.##");
            return "" + df.format(tmpSize) + "MB";
        } else if (size / 1024 > 0) {
            return "" + (size / (1024)) + "KB";
        } else
            return "" + size + "B";
    }

    public static String getSerIp(){
        return Tool.get("srvip", Util.getSDPath() + "/vsconfig/config2.ini");
    }
}

