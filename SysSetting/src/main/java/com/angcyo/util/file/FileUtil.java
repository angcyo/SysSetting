package com.angcyo.util.file;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.angcyo.fragment.adapter.node.FileNote;
import com.angcyo.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by angcyo on 2015-03-19 019.
 */
public class FileUtil {

    /**
     * 返回SD卡路径,如果没有返回默认的下载缓存路径
     *
     * @return the sD path
     */
    public static String getSDPath() {
        return isExternalStorageWritable()
                ? Environment.getExternalStorageDirectory().getPath()
                : Environment.getDownloadCacheDirectory().getPath();
    }

    /**
     * 判断是否有SD卡
     *
     * @return the boolean
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static FileNote getFileNote(String filePath) {
        if (filePath == null)
            return null;

        FileNote fileNote = new FileNote();
        fileNote.currentFilePath = filePath;

        File f = new File(filePath);
        if (!f.exists())
            return null;

        File[] files = f.listFiles();
        if (files == null)
            return null;

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            fileNote.fileName.add(file.getName());
            fileNote.filePath.add(file.getPath());

            // 过滤文件夹
            if (file.isDirectory() && !f.getName().startsWith(".")
                    && file.canRead() && file.canWrite()
                    && !file.isHidden()) {
                fileNote.fileFolderName.add(file.getName());
                fileNote.fileFolderPath.add(file.getPath());
//
//                List<File> listFolder = listFolder(file.getPath());
//                if (listFolder == null || listFolder.size() < 1) {
//                    fileNote.hasChildFolder.add(Boolean.FALSE);
//                } else {
//
//                    Logger.e("路径" + file.getPath() + "  含有" + listFolder.size());
//                    fileNote.hasChildFolder.add(Boolean.TRUE);
//                }

            }
        }

        //默认的排序方式
        Collections.sort(fileNote.fileName);
        Collections.sort(fileNote.filePath);
        Collections.sort(fileNote.fileFolderName);
        Collections.sort(fileNote.fileFolderPath);

//        Logger.e("当前路径:::" + fileNote.currentFilePath + ":::文件夹数量::::" + fileNote.fileFolderPath.size());

        return fileNote;
    }

    /**
     * 枚举指定路径下的所有文件夹
     */
    public static List<File> listFolder(String path) {
        if (Util.isEmpty(path)) {
            return null;
        }

        File file = new File(path);
        if (!file.exists() || !file.isDirectory() || !file.canRead() || file.isHidden()) {
            return null;
        }

        File[] files = file.listFiles();
        if (files == null || files.length < 1) {
            return null;
        }

        List<File> listFolder = new ArrayList<>();
        for (File temp : files) {
            if (temp.isDirectory() && !file.isHidden()) {
                listFolder.add(temp);
            }
        }

        return listFolder;
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return null;
        }
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    /**
     * 获取文件名,不包括扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileName(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1 && index != 0) {
            return fileName.substring(0, index);
        } else {
            return "";
        }
    }

    /**
     * 获取APP根目录
     */
    public static String getAppRootPath(Context context) {
        return getCurrentPathPrev(context.getFilesDir().toString());
    }

    /**
     * 获得上一级目录
     */
    public static String getCurrentPathPrev(String path) {
        if (path == null || path.length() == 0 || path.equals("/"))
            return "/";
//		String[] paths = path.split("/", 1);//至少返回1个
        String prevPath = path.substring(0, path.lastIndexOf("/") == 0 ? 1 : path.lastIndexOf("/"));//上一级路径
//		String basePath = "/storage/emulated";
//		if (prevPath.equals(basePath))
//			return basePath.concat("/0");
        return prevPath;
    }

    /**
     * 计算SD卡的剩余空间
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String status = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                freeSpace = availableBlocks * blockSize / 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return (freeSpace);
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取文件大小
     *
     * @param size 字节
     * @return
     */
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
        float temp = (float) size / 1024;
        if (temp >= 1024) {
            return df.format(temp / 1024) + "M";
        } else {
            return df.format(temp) + "K";
        }
    }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        long size = 0;

        File file = new File(filePath);
        if (file != null && file.exists()) {
            size = file.length();
        }
        return size;
    }
}
