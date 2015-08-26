package com.angcyo.connectadmin.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.angcyo.connectadmin.control.SocketControl;
import com.angcyo.util.CmdUtil;
import com.angcyo.util.DownloadRunnable;
import com.angcyo.util.IDownload;
import com.angcyo.util.ISocket;
import com.angcyo.util.Logger;
import com.angcyo.util.Setting;
import com.angcyo.util.SocketCallback;
import com.angcyo.util.SocketUtil;
import com.angcyo.util.Util;
import com.angcyo.util.bean.AppInfo;
import com.angcyo.util.bean.ConnectBean;
import com.angcyo.util.bean.DownTask;
import com.angcyo.util.bean.SocketInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by angcyo on 2015-04-14 014.
 */
public class SocketService extends Service {
    public static final String ACTION_UPDATE_SOCKET_INFO = "com.angcyo.update.socket.info";//更新信息的广播
    public static final String ACTION_REBOOT_SOCKET_SERVICE = "com.angcyo.reboot.socket.service";//重启服务的广播
    public static final String ACTION_START_MAIN_ACTIVITY = "com.angcyo.start.main.activity";//重启服务的广播
    public static final String ACTION_RECEIVE_INFO = "com.angcyo.receive.info";//收到信息的广播
    public static final String KEY_UPDATE_INFO = "key.update.info";//
    public static final String KEY_RECEIVE_INFO = "key.receive.info";//

    public static final String CMD_IS_RUN = "isrun";//是否运行
    public static final String CMD_START_APP = "start";//开启远程程序
    public static final String CMD_DEL_FILE = "delfile";//删除文件,文件夹
    public static final String CMD_KILL_APP = "kill";//杀进程
    public static final String CMD_INSTALL = "install";//安装apk
    public static final String CMD_UNINSTALL = "uninstall";//卸载apk
    public static final String CMD_DOWN_FILE = "file";//下载文件
    public static final String CMD_RESUME_APP = "reset";//重启远程程序
    public static final String CMD_COPY_FILE = "copyfile";//复制文件
    public static Service service;
    private SocketControl socketControl;//用于控制Socket
    private String serviceIp = "-.-.-.-";//服务器IP,默认
    private String localIp = "-.-.-.-";
    private int port = 9925;//端口,默认
    private String deviceName;
    private Thread socketControlThread;
    private boolean isThreadRun = false;
    private Handler handler = new Handler();
    private long DELAY_UPDATE_INFO = 400;
    /**
     * 每隔一定时间,发送一次更新状态的广播
     */
    Runnable mUpdateSocketStateInfo = new Runnable() {
        @Override
        public void run() {
            sendUpdateInfo();
            handler.postDelayed(mUpdateSocketStateInfo, DELAY_UPDATE_INFO);
        }
    };
    private String receiveMsg;//接收到的消息,用于分析里面包含的命令
    private DownloadRunnable downloadRunnable;//下载线程
    private boolean isCopying = false;//是否正在复制

    private static String loadFileAsString(String filePath)
            throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Logger.e("Service->" + Thread.currentThread().getId());

        flags = START_STICKY;
        connectSocket();
        downloadRunnable = new DownloadRunnable(new IDownload() {//下载回调
            @Override
            public void onDownStart(DownTask task, String downUrl) {
                Logger.e("开始下载:" + downUrl);
                socketControl.writeToVector("开始下载..." + downUrl);
            }

            @Override
            public void onDownProgress(long speed, int progress) {
                Logger.e("下载速度:" + Util.prettySize(speed) + "/s" + "  下载进度:" + progress + "%");
//                socketControl.writeToVector("下载速度:" + Util.prettySize(speed) + "/s" + "  下载进度:" + progress + "%");
            }

            @Override
            public void onDownEnd(DownTask task, String downUrl, String filePath, long downTime) {
                Logger.e("文件保存在:" + filePath + " 下载用时:" + downTime + "s");
                socketControl.writeToVector("文件保存在:" + filePath + " 下载用时:" + downTime + "s");
            }

            @Override
            public void onDownError(String error) {
                Logger.e("下载结束:" + error);
                socketControl.writeToVector("下载结束:" + error);
            }
        });
        new Thread(downloadRunnable).start();//启动下载线程
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(ACTION_REBOOT_SOCKET_SERVICE);
        sendBroadcast(intent);//发送重启服务的广播

        disconnectSocket();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = this;

        //手动设置的IP,优先使用,如果未手动设置ip, 读取配置
        String serIp = Setting.getServiceIp(this);
        if (Util.isEmpty(serIp)) {
            serIp = Util.getSerIp();
            if (Util.isEmpty(serIp)) {
                serIp = "192.168.1.100";
            }
        }

        serviceIp = serIp;
        port = Setting.getPort(this);
        deviceName = Setting.getDeviceName(this);

        //构造一个Socket控制类,传递一个回调参数
        socketControl = new SocketControl(new SocketCallback() {
            @Override
            public void onSocketConnected(ConnectBean bean) {//网络连接上时
                Logger.e("网络已连接");
                serviceIp = bean.serviceAddress;
                localIp = bean.localAddress;
                port = bean.port;
                sendUpdateInfo();
            }

            @Override
            public void onSocketReceive(byte[] data) {//网络数据返回时
                receiveMsg = new String(data);
//                Logger.e("收到消息:" + receiveMsg);
                final String[] cmd = receiveMsg.split("\\|");//分割数据
                handlerReceiveCmd(cmd);//处理命令请求...

                sendReceiveInfo(data);
            }

            @Override
            public void onSocketDisconnect() {//网络断开时
//                Logger.e("网络断开");
            }

            @Override
            public void onSocketStateChanged(int newState) {//网络状态改变
//                Logger.e("网络状态改变:" + newState);

                sendUpdateInfo();
            }
        });
    }

    /**
     * 作为主要的处理方法,分析后台发送来的命令,进行响应
     */
    private void handlerReceiveCmd(final String[] cmd) {
        if (!Util.isEmpty(cmd[0])) {
            if (cmd[0].equalsIgnoreCase(CMD_IS_RUN)) {
                handlerCmdIsRun(cmd);
            } else if (cmd[0].equalsIgnoreCase(CMD_START_APP)) {
                handlerCmdStartApp(cmd);
            } else if (cmd[0].equalsIgnoreCase(CMD_DEL_FILE)) {
                handlerCmdDelFile(cmd);
            } else if (cmd[0].equalsIgnoreCase(CMD_KILL_APP)) {
                handlerCmdKillApp(cmd);
            } else if (cmd[0].equalsIgnoreCase(CMD_INSTALL)) {
                handlerCmdInstall(cmd);
            } else if (cmd[0].equalsIgnoreCase(CMD_UNINSTALL)) {
                handlerCmdUninstall(cmd);
            } else if (cmd[0].equalsIgnoreCase(CMD_DOWN_FILE)) {
                handlerCmdDownFile(cmd);
            } else if (cmd[0].equalsIgnoreCase(CMD_RESUME_APP)) {
                handlerCmdResumeApp(cmd);
            } else if (cmd[0].equalsIgnoreCase(CMD_COPY_FILE)) {
                handlerCmdCopyFile(cmd);
            }
        }
    }

    private void handlerCmdCopyFile(String[] cmd) {
        if (showHelpCmd(cmd, "支持的参数:\r\n  help -> 显示帮助\r\n")) {
        } else {
            if (isCopying) {
                socketControl.writeToVector(true, true, socketControl.getDeviceName(), cmd[1], "请等待复制完成,再操作.");
            } else {
                new Thread(new CopyFile(cmd[1], cmd[2])).start();
            }

        }
    }

    /**
     * 重启程序, 先kill进程,在启动app
     */
    private void handlerCmdResumeApp(String[] cmd) {

        if (Util.isEmpty(cmd[2]) || "help".equalsIgnoreCase(cmd[2])) {
            socketControl.writeToVector(true, socketControl.getDeviceName(), cmd[0],
                    "支持的参数:\r\n  help -> 显示帮助\r\n  showAll -> 显示正在运行的程序\r\n");
        } else if ("showAll".equalsIgnoreCase(cmd[2])) {
            List<ActivityManager.RunningTaskInfo> taskInfos = CmdUtil.getAllRunningTask(this);
            List<ResolveInfo> resolveInfoList = CmdUtil.loadAllInstallApps(this);
            String label = "";
            for (ActivityManager.RunningTaskInfo info : taskInfos) {
                label = CmdUtil.getPackageNameLabel(this, info.baseActivity.getPackageName(), resolveInfoList);
                socketControl.writeToVector(true, true, socketControl.getDeviceName(), label, info.topActivity.getPackageName());
            }
        } else {
            List<PackageInfo> apps = CmdUtil.loadAllApps(this);
            boolean isHave = false;
            for (PackageInfo app : apps) {
                if (app.packageName.equalsIgnoreCase(cmd[2])) {
                    isHave = true;
                    break;
                }
            }
            if (!isHave) {
                socketControl.writeToVector(true, true, socketControl.getDeviceName(), cmd[2], "程序未安装");
                return;
            }
            final String packName = cmd[2];

            CmdUtil.killApp(packName);//一秒之后再启动,,,
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CmdUtil.startApp(SocketService.this, packName);
                }
            }, 1000);

        }
    }

    private void handlerCmdDownFile(String[] cmd) {
        if (showHelpCmd(cmd, "支持的参数:\r\n  help -> 显示帮助\r\n")) {

        } else {
            DownTask task = new DownTask();
            task.setUrl(cmd[1]);
            task.setDownFileName(cmd[2]);

            downloadRunnable.addDownloadTask(task);
        }
    }

    /**
     * 卸载APK
     */
    private void handlerCmdUninstall(String[] cmd) {
        if (showHelpCmd(cmd, "支持的参数:\r\n  help -> 显示帮助\r\n  showAll -> 显示所有安装的APP\r\n")) {

        } else if ("showAll".equalsIgnoreCase(cmd[1])) {
            List<ResolveInfo> apps = CmdUtil.loadAllInstallApps(this);
            for (ResolveInfo app : apps) {
                socketControl.writeToVector(true, true,
                        socketControl.getDeviceName(),
                        app.activityInfo.loadLabel(this.getPackageManager()) + "",
                        app.activityInfo.packageName);
            }

        } else {
            List<PackageInfo> apps = CmdUtil.loadAllApps(this);
            boolean isHave = false;
            for (PackageInfo app : apps) {
                if (app.packageName.equalsIgnoreCase(cmd[1])) {
                    isHave = true;
                    break;
                }
            }
            if (isHave) {
                if (!CmdUtil.uninstallApk(cmd[1])) {
                    socketControl.writeToVector(true, socketControl.getDeviceName(),
                            cmd[1],
                            "正在尝试手动卸载...");
                    CmdUtil.startInstallApk(SocketService.service, cmd[1]);
                } else {
                    socketControl.writeToVector(true, socketControl.getDeviceName(),
                            cmd[1],
                            "静默卸载成功");
                }
            } else {
                socketControl.writeToVector(true, socketControl.getDeviceName(),
                        cmd[1],
                        "没有安装此程序");
            }
        }
    }

    /**
     * 安装apk, 使用静默的方法,如果失败调用手动安装
     */
    private void handlerCmdInstall(String[] cmd) {
        if (showHelpCmd(cmd, "支持的参数:\r\n  help -> 显示帮助\r\n")) {

        } else {
            socketControl.writeToVector("准备下载中..." + cmd[1]);

            DownTask task = new DownTask();
            task.setServiceIp(serviceIp);
            task.setPort(Integer.valueOf(cmd[2]));
            task.setDownFileName(cmd[1]);
            task.setInstall(true);//安装,下载完成后,安装

            downloadRunnable.addDownloadTask(task);
        }
    }

    /**
     * 杀进程
     */
    private void handlerCmdKillApp(String[] cmd) {
        if (showHelpCmd(cmd, "支持的参数:\r\n  help -> 显示帮助\r\n  showAll -> 显示所有进程\r\n")) {

        } else if ("showAll".equalsIgnoreCase(cmd[1])) {
            List<AppInfo> appInfos = CmdUtil.getAllRunningProcess(this);
            for (AppInfo app : appInfos) {
                socketControl.writeToVector(true, true,
                        socketControl.getDeviceName(),
                        app.getLabel(),
                        app.getPackageName());
            }
        } else {
            if (!CmdUtil.initProcess()) {
                socketControl.writeToVector(true, socketControl.getDeviceName(), cmd[1], "申请root权限失败");
                return;
            }
            if (!CmdUtil.killProcess(cmd[1])) {
                socketControl.writeToVector(true, socketControl.getDeviceName(), cmd[1], "请检查参数");
                return;
            }
            socketControl.writeToVector(true, socketControl.getDeviceName(), cmd[1], "进程已杀");
        }
    }

    /**
     * 启动程序
     */
    private void handlerCmdStartApp(String[] cmd) {
        if (showHelpCmd(cmd, "支持的参数:\r\n  help -> 显示帮助\r\n  showAll -> 显示所有安装的APP\r\n")) {

        } else if ("showAll".equalsIgnoreCase(cmd[1])) {
            List<ResolveInfo> apps = CmdUtil.loadAllInstallApps(this);
            for (ResolveInfo app : apps) {
                socketControl.writeToVector(true, Util.groupString(true,
                        socketControl.getDeviceName(),
                        app.activityInfo.loadLabel(this.getPackageManager()) + "",
                        app.activityInfo.packageName));
            }
        } else {
            try {
                CmdUtil.startApp(this, cmd[1]);
                socketControl.writeToVector(false, true, socketControl.getDeviceName(), cmd[1], "已启动");
            } catch (Exception e) {
                socketControl.writeToVector(false, true, socketControl.getDeviceName(), cmd[1], "请检查参数");
            }
        }


    }

    /**
     * 删除文件
     */
    private void handlerCmdDelFile(String[] cmd) {
        if (showHelpCmd(cmd, "支持的参数:\r\n  help -> 显示帮助\r\n")) {

        } else {
            try {
                File file = new File(cmd[1]);
                if (!file.exists()) {
                    socketControl.writeToVector(false, true, socketControl.getDeviceName(), cmd[1], "文件/文件夹不存在");
                    return;
                }

                if (CmdUtil.deleteFiles(file)) {
                    socketControl.writeToVector(false, true, socketControl.getDeviceName(), cmd[1], "删除成功");
                } else {
                    socketControl.writeToVector(false, true, socketControl.getDeviceName(), cmd[1], "删除失败");
                }

            } catch (Exception e) {
                socketControl.writeToVector(false, true, socketControl.getDeviceName(), cmd[1], "请检查参数");
            }
        }
    }

    /**
     * 检测程序是否运行
     */
    private void handlerCmdIsRun(String[] cmd) {
        //判断参数
        if (showHelpCmd(cmd, "支持的参数:\r\n  help -> 显示帮助\r\n  showAll -> 显示所有正在运行的APP\r\n")) {

        } else if ("showAll".equalsIgnoreCase(cmd[1])) {
            List<ActivityManager.RunningTaskInfo> taskInfos = CmdUtil.getAllRunningTask(this);
            List<ResolveInfo> resolveInfoList = CmdUtil.loadAllInstallApps(this);
            String label = "";
            for (ActivityManager.RunningTaskInfo info : taskInfos) {
                label = CmdUtil.getPackageNameLabel(this, info.baseActivity.getPackageName(), resolveInfoList);
                socketControl.writeToVector(true, Util.groupString(true, socketControl.getDeviceName(), label, info.topActivity.getPackageName()));
            }
        } else {
            try {
                if (CmdUtil.isRunningApp(this, cmd[1])) {
                    socketControl.writeToVector(Util.groupString(socketControl.getDeviceName(), "|", cmd[1], " 正在运行"));
                } else {
                    socketControl.writeToVector(Util.groupString(socketControl.getDeviceName(), "|", cmd[1], " 没有运行"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                socketControl.writeToVector(Util.groupString(socketControl.getDeviceName(), "|", cmd[1], " 请检查参数"));
            }
        }
    }

    /**
     * 是否显示帮助
     */
    private boolean showHelpCmd(String[] cmd) {
        if (Util.isEmpty(cmd[1]) || "help".equalsIgnoreCase(cmd[1]) || "help.apk".equalsIgnoreCase(cmd[1])) {
            return true;
        }
        return false;
    }

    private boolean showHelpCmd(String[] cmd, String help) {
        if (showHelpCmd(cmd)) {
            socketControl.writeToVector(Util.groupString(true, socketControl.getDeviceName(), cmd[0], help));
            return true;
        }
        return false;
    }

    private void connectSocket() {
//        socketControl.connectSocket(serviceIp, port);
        if (!isThreadRun) {
            socketControlThread = new Thread(socketControl);
            socketControl.setConnectInfo(getMacAddress(), serviceIp, port, deviceName);
            socketControlThread.start();
            isThreadRun = true;
            handler.postDelayed(mUpdateSocketStateInfo, DELAY_UPDATE_INFO);
        }
    }

    private void resetConnectSocket() {
//        if (socketControl.isRun()) {
//            socketControl.stop();
//            handler.removeCallbacks(mUpdateSocketStateInfo);
//            isThreadRun = false;
        socketControl.setSocketState(SocketUtil.STATE_CONNECTING);
        socketControl.setConnectInfo(getMacAddress(), serviceIp, port, deviceName);
        socketControl.setRunCmd(SocketControl.CMD_RECONNECT);//设置需要执行的命令
        sendUpdateInfo();
//        }
//        connectSocket();
    }

    private void disconnectSocket() {
        socketControl.disconnectSocket();
        handler.removeCallbacks(mUpdateSocketStateInfo);
    }

    /**
     * 创建一个最新的网络状态信息,用于发送给界面,更新
     */
    private SocketInfo createLatestSocketInfo() {
        SocketInfo info = new SocketInfo();
        info.setLocalIp(localIp);
        info.setLocalMac(getMacAddress());
        info.setPort(port);
        info.setServiceIp(serviceIp);
        info.setSocketState(socketControl.getSocketState());
        info.setDeviceName(socketControl.getDeviceName());
        return info;
    }

    /**
     * 发送一个更新UI数据的广播
     */
    private void sendUpdateInfo(Bundle bundle) {
        Intent intent = new Intent(ACTION_UPDATE_SOCKET_INFO);
        intent.putExtras(bundle);
        sendBroadcast(intent);//发送更新界面信息的广播
    }

    private void sendUpdateInfo() {
        SocketInfo info = createLatestSocketInfo();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_UPDATE_INFO, info);
        sendUpdateInfo(bundle);
    }

    /**
     * 将收到的消息,通过广播发送出去
     */
    private void sendReceiveInfo(byte[] msg) {
        Intent intent = new Intent(ACTION_RECEIVE_INFO);
        Bundle bundle = new Bundle();
        bundle.putByteArray(KEY_RECEIVE_INFO, msg);
        intent.putExtras(bundle);
        sendBroadcast(intent);//发送更新界面信息的广播
    }

    /**
     * 获取wifi网卡的mac地址
     */
    private String getMacAddress() {
        try {
            return loadFileAsString("/sys/class/net/wlan0/address")
                    .toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    class CopyFile implements Runnable {
        String from;
        String to;

        public CopyFile(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            isCopying = true;

            File srcFile = new File(from);//源文件
            File desFile = new File(to);//目标文件

            if (!srcFile.exists()) {
                socketControl.writeToVector(true, socketControl.getDeviceName(), from,
                        "源文件不存在,已终止复制");
                isCopying = false;
                return;
            }
            try {
                if (srcFile.isFile()) {
                    CmdUtil.copyFile2(srcFile, desFile);
                }
                if (srcFile.isDirectory()) {
                    CmdUtil.copyDirectiory(from, to);
                }
                socketControl.writeToVector(true, socketControl.getDeviceName(), from,
                        "复制成功");
                isCopying = false;
            } catch (IOException e) {
                e.printStackTrace();
                socketControl.writeToVector(true, socketControl.getDeviceName(), from,
                        "复制异常:" + e.toString());
                isCopying = false;
            }
        }
    }

    /**
     * 提供给Activity 界面,操作的接口
     */
    public class MyBinder extends Binder implements ISocket {

        @Override
        public void setServiceIp(String ip) {
            serviceIp = ip;
            resetConnectSocket();
        }

        @Override
        public void setPort(int port) {
            SocketService.this.port = port;
            resetConnectSocket();
        }

        @Override
        public void setDeviceName(String name) {
            try {
                socketControl.setDeviceName(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sendMsg(String msg) {
            try {
                socketControl.writeToVector(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
