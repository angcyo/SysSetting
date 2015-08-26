package com.angcyo.connectadmin.control;

import com.angcyo.util.Logger;
import com.angcyo.util.SocketCallback;
import com.angcyo.util.SocketUtil;
import com.angcyo.util.Util;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by angcyo on 2015-04-14 014.
 */
public class SocketControl implements Runnable {
    private static final Vector<byte[]> sendDatas = new Vector<byte[]>();//需要发送的消息
    private String serviceIpAddress = "192.168.2.105";//服务器ip地址
    private int port = 9925;//端口号
    private String localMacAddress = "";//本地mac地址
    private String deviceName = "android_" + System.currentTimeMillis();//设备名
    //    private WriteRunnable writeRunnable = new WriteRunnable();
    private ReadRunnable readRunnable = new ReadRunnable();
    private int SLEEP_TIME = 100;
    private int HEART_TIME = 10000;//心跳的间隔时间
    private int heartCount = 0;//心跳的次数
    private SocketUtil socketUtil;
    private int SOCKET_STATE = SocketUtil.STATE_UNKNOWN;//socket的状态
    private boolean isRun = true;//是否运行
    private SocketCallback socketCallback;
    private boolean isWrite = true;//是否写入数据
    private boolean isRead = true;//是否读取数据
    private int runCmd = CMD_NORMAL;//需要执行的命令
    public static final int CMD_NORMAL = 0;//正常执行
    public static final int CMD_RECONNECT = 2;//重新连接网络

    public SocketControl(SocketCallback socketCallback) {
        this.socketCallback = socketCallback;
        this.socketUtil = new SocketUtil(socketCallback);
    }

    @Override
    public void run() {
        if (serviceIpAddress == null || port <= -1) {
            throw new NullPointerException("没有配置服务器信息: IP and Port");
        }

        if (connectSocket()) {
        }
        new Thread(readRunnable).start();//开始数据读取线程
//        new Thread(writeRunnable).start();//开始数据写入线程
        while (isRun) {//
            synchronized (this) {
                handlerSocket();
            }
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 执行的操作是通过控制runCmd变量执行的.
     */
    public void setRunCmd(int cmd) {
        runCmd = cmd;
    }

    /**
     * 线程最主要的处理函数,根据网络状态,处理
     */
    private void handlerSocket() {
//        Logger.e("网络状态:" + SOCKET_STATE);
        switch (runCmd) {
            case CMD_RECONNECT:
                if (reconnectSocket()) {
                    runCmd = CMD_NORMAL;
                }
                return;
            default:
                break;
        }

        switch (SOCKET_STATE) {
            case SocketUtil.STATE_CONNECTED://网络已连接
                writeHeart();
                sendData();
                break;
            case SocketUtil.STATE_DISCONNECT://网络已断开
            case SocketUtil.STATE_UNKNOWN://未知状态
            case SocketUtil.STATE_EXCEPTION://网络异常
                clearAllData();
                if (reconnectSocket()) {
                    setDeviceName(deviceName);
                }
                break;
            case SocketUtil.STATE_CONNECTING://网络正在连接
                clearAllData();
                break;
            default:
                break;
        }
    }

    public String getDeviceName() {
        return deviceName;
    }

    /**
     * 写入心跳消息
     */
    private void writeHeart() {
//        Logger.e("心跳..." + (SLEEP_TIME * heartCount) % HEART_TIME);
        if (((SLEEP_TIME * heartCount) % HEART_TIME) == 0) {
            writeToVector("heart|" + localMacAddress + "|" + Util.getNowTime() + "\n\r");//心跳数据
            if (heartCount > 0x11111100) {
                heartCount = 0;
            }
        }
        heartCount++;
    }

    /**
     * 将未发送的数据,发送出去
     */
    private void sendData() {
        //如果有数据,就处理
        while (sendDatas.size() > 0) {
//            Logger.e("需要写入的数据量..." + sendDatas.size());
            byte[] data = sendDatas.remove(0);//获取并删除第一条数据
            if (isWrite) {
                write(data);//发送数据
            }
        }

    }

    /**
     * 设置网络连接的一些信息, 请在run之前调用...否则无效
     */
    public void setConnectInfo(String localMacAddress, String serviceIP, int port, String deviceName) {
        this.serviceIpAddress = serviceIP;
        this.localMacAddress = localMacAddress;
        this.port = port;
        this.deviceName = deviceName;
    }

    /**
     * 设置pc端显示的名称
     */
    public void setDeviceName(String name) {
        deviceName = name;
        write(("androiduser|" + localMacAddress + "|" + deviceName + "\n\r").getBytes());//设置设备的名字
    }

    /**
     * 断开网络
     */

    public synchronized void disconnectSocket() {
        SOCKET_STATE = SocketUtil.STATE_DISCONNECT;
        isRun = false;
        resetSocket();
        socketCallback.onSocketStateChanged(SOCKET_STATE);
    }

    /**
     * 连接网络
     */
    public synchronized boolean connectSocket() {
        try {
            socketUtil.connectSocket(serviceIpAddress, port);
            SOCKET_STATE = SocketUtil.STATE_CONNECTED;
            setDeviceName(deviceName);
            socketCallback.onSocketStateChanged(SOCKET_STATE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            SOCKET_STATE = SocketUtil.STATE_EXCEPTION;
            socketCallback.onSocketStateChanged(SOCKET_STATE);
            return false;
        }
    }

    /**
     * 将数据写入到队列
     */
    public void writeToVector(String data) {
        writeToVector(false, data);
    }

    /**
     * Write to vector.
     *
     * @param newLine the new line
     * @param data    the data
     */
    public void writeToVector(boolean newLine, String data) {
        if (SOCKET_STATE == SocketUtil.STATE_CONNECTED) {
            if (newLine) {
                sendDatas.add("\r\n".getBytes());
            }
            sendDatas.add(data.getBytes());
        }
    }

    /**
     * Write to vector.
     *
     * @param newLine 是否换行
     * @param split   是否插入分割符"|"
     * @param data    the data
     */
    public void writeToVector(boolean newLine, boolean split, String... data) {
        if (SOCKET_STATE == SocketUtil.STATE_CONNECTED) {
            if (newLine) {
                sendDatas.add("\r\n".getBytes());
            }
            String newData = Util.groupString(split, data);
            sendDatas.add(newData.getBytes());
        }
    }

    public void writeToVector(boolean split, String... data) {
        writeToVector(false, split, data);
    }

    /**
     * 将数据写到socket
     */
    private boolean write(byte[] data) {
        try {
            socketUtil.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            resetSocket();
            SOCKET_STATE = SocketUtil.STATE_EXCEPTION;
            socketCallback.onSocketStateChanged(SOCKET_STATE);
        }
        return false;
    }

    /**
     * 返回网络状态
     */
    public int getSocketState() {
        return SOCKET_STATE;
    }

    /**
     * 返回网络状态
     */
    public void setSocketState(int state) {
        SOCKET_STATE = state;
    }

    public void stop() {
        this.isRun = false;
        this.isRead = false;
        this.isWrite = false;
    }

    public boolean isRun() {
        return isRun;
    }

    public void setIsRun(boolean isRun) {
        this.isRun = isRun;
    }

    /**
     * 清空数据队列,所有未发送的消息
     */
    private void clearAllData() {
        sendDatas.clear();
    }

    private void resetSocket() {
        socketUtil.disconnectSocket();//
//        socketUtil.connectSocket();
    }

    private boolean reconnectSocket() {
        resetSocket();
        return connectSocket();
    }

//    /**
//     * socket 数据写入线程
//     */
//    private class WriteRunnable implements Runnable {
//
//        @Override
//        public void run() {
//            synchronized (this) {
////                Logger.e("写入线程已启动...");
//                while (isWrite) {
//                    Logger.e("write...");
//                    if (sendDatas.size() <= 0) {//如果没有数据,就等待
//                        try {
//                            this.wait();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                            continue;
//                        }
//                    } else {
//                        while (sendDatas.size() > 0) {
//                            Logger.e("需要写入的数据量..." + sendDatas.size());
//                            byte[] data = sendDatas.remove(0);//获取并删除第一条数据
//                            if (isWrite) {
//                                write(data);//发送数据
//                            } else {
//                                this.notify();
//                            }
//                        }
//                    }
//
//                }
//            }
//        }
//    }

    static long SLEEP_WAIT = 300;

    /**
     * socket 数据读取线程,通过回调传递数据
     */
    private class ReadRunnable implements Runnable {

        @Override
        public void run() {
            Logger.e("读取线程已启动...");
            while (isRead) {
                if (SOCKET_STATE == SocketUtil.STATE_CONNECTED) {
                    try {
                        socketUtil.read();
                    } catch (Exception e) {
                        e.printStackTrace();
                        SOCKET_STATE = SocketUtil.STATE_EXCEPTION;
                        socketCallback.onSocketStateChanged(SOCKET_STATE);
                        Logger.e("read error");
                    }
                } else {
                    try {
                        Thread.sleep(SLEEP_WAIT);
                        SLEEP_WAIT += 100;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

