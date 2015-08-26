package com.angcyo.util;

import com.angcyo.connectadmin.service.SocketService;
import com.angcyo.util.bean.DownTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * 下载线程
 * Created by angcyo on 2015-04-17 017.
 */
public class DownloadRunnable implements Runnable {
    private boolean isDown = true;//是否下载
    private static final Vector<DownTask> dlTask = new Vector<DownTask>();// 待下载数据队列
    private boolean isExit = false;
    private IDownload downloadCallback;

    private DownTask curTask;//当前正在进行的下载任务

    /**
     * Instantiates a new Download runnable.
     *
     * @param downloadCallback the download callback
     */
    public DownloadRunnable(IDownload downloadCallback) {
        this.downloadCallback = downloadCallback;
    }

    @Override
    public void run() {
        while (!isExit()) {
            if (isDown()) {
                if (!Util.isSD()) {
                    downloadCallback.onDownError("无可用SD卡");
                }
                handlerDownload();//循环处理任务
            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handler download.
     */
    private void handlerDownload() {
        if (!Util.isSD()) {
            downloadCallback.onDownError("无可用SD卡");
            return;
        }
        try {
            if (dlTask.size() < 1) {//没有下载任务
                return;
            }
            curTask = dlTask.remove(0);//获取第一条任务
            String serviceIp = curTask.getServiceIp();
            int port = curTask.getPort();
            String fileName = curTask.getDownFileName();

            String url;
            if (Util.isEmpty(curTask.getUrl())) {
                url = "http://" + serviceIp + ":" + port + "/" + fileName;//组装成一个下载链接
            } else {
                url = curTask.getUrl();
            }
//            String url = "\"http://" + serviceIp + ":" + port + "/" + fileName + "\"";//组装成一个下载链接

            downloadCallback.onDownStart(curTask, url);
            startDownloadToSd(curTask, url);//开始下载
        } catch (Exception e) {
            e.printStackTrace();
            downloadCallback.onDownError(e.toString());
        }
    }

    /**
     * 开始下载文件到SD卡
     */
    private synchronized void startDownloadToSd(DownTask task, String urlDown) {
        try {
            //打开连接
            URL url = new URL(urlDown);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();

            //创建本地文件
            StringBuilder downFilePath = new StringBuilder(Util.getSDPath())
                    .append("/")
                    .append("zckj_down");
            File file = new File(downFilePath.toString());
            if (!file.exists()) {
                file.mkdirs();
            }
            downFilePath.append("/").append(task.getDownFileName());
            file = new File(downFilePath.toString());
            file.createNewFile();

            //打开写入流
            FileOutputStream fileOut = new FileOutputStream(file);
            BufferedOutputStream bufferOut = new BufferedOutputStream(fileOut);

            int BUFFER_SIZE = 4096;
//            //解决编码问题,如地址包含空格,中文等
//            String strUrl = Uri.encode(urlDown, "utf-8")
//                    .replaceAll("%3A", ":").replaceAll("%2F", "/");

            int fileLength = urlConnection.getContentLength();
            int downCount = 0;
            byte[] readBuffer = new byte[BUFFER_SIZE];
            int count;

            long startTimeSpeed = System.currentTimeMillis();//保存开始下载的时间,用于计算下载速度
            long startTime = startTimeSpeed;
            long speed = 0;//速率
            long readSize = 0;//一个计速周期内,读取的数据大小
            while ((count = inputStream.read(readBuffer, 0, BUFFER_SIZE)) != -1) {//开始读写数据
                bufferOut.write(readBuffer, 0, count);
                downCount += count;

                long time = System.currentTimeMillis();
                if ((time - startTimeSpeed) <= 300) {//计算速率的时间间隔
                    readSize += count;
                } else {
                    speed = (readSize * 1000) / (time - startTimeSpeed);
                    readSize = 0;
                    startTimeSpeed = time;
                }
                downloadCallback.onDownProgress(speed, downCount * 100 / fileLength);
            }
            downloadCallback.onDownEnd(task, urlDown, file.getPath(), (System.currentTimeMillis() - startTime) / 1000);
            if (task.isInstall()) {
                if (!CmdUtil.installApk(file.getPath())) {//静态安装失败,调用手动安装
                    downloadCallback.onDownError("静默安装失败,调用手动安装...");
                    CmdUtil.startInstallApk(SocketService.service, file);
                } else {
                    downloadCallback.onDownError("静默安装成功.");
                }
            }

            curTask = null;
            bufferOut.flush();
            bufferOut.close();
            inputStream.close();
            urlConnection.disconnect();
        } catch (Exception e) {//异常Exception
            e.printStackTrace();
            downloadCallback.onDownError(e.toString());
            curTask = null;
        }
    }

    /**
     * 添加下载任务
     */
    public synchronized void addDownloadTask(DownTask task) {
        if (curTask != null && curTask.getDownFileName().equalsIgnoreCase(task.getDownFileName())) {
            downloadCallback.onDownError("下载任务正在进行...");
        } else {
            for (DownTask tk : dlTask) {
                if (tk.getDownFileName().equalsIgnoreCase(task.getDownFileName())) {
                    downloadCallback.onDownError("下载任务已存在");
                    return;
                }
            }
            dlTask.add(task);
        }
    }

    /**
     * Is exit.
     *
     * @return the boolean
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Sets exit.
     *
     * @param exit the exit
     */
    public void setExit(boolean exit) {
        isExit = exit;
    }

    /**
     * Is down.
     *
     * @return the boolean
     */
    public boolean isDown() {
        return isDown;
    }

    /**
     * Sets is down.
     *
     * @param isDown the is down
     */
    public void setIsDown(boolean isDown) {
        this.isDown = isDown;
    }
}
