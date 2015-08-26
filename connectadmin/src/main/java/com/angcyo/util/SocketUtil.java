package com.angcyo.util;

import com.angcyo.util.bean.ConnectBean;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by angcyo on 2015-04-14 014.
 */
public class SocketUtil {

    public static final int STATE_UNKNOWN = 0;//Socket状态: 未连接
    public static final int STATE_CONNECTED = 1;//Socket状态: 已连接
    public static final int STATE_DISCONNECT = 2;//Socket状态: 已断开
    public static final int STATE_EXCEPTION = 3;//Socket状态: 连接异常
    public static final int STATE_CONNECTING = 4;//Socket状态: 正在连接

    private SocketCallback socketCallback;
    private Socket socket;
    private DataInputStream dataRead;
    private DataOutputStream dataWrite;
    private int TIME_OUT = 1000 * 8;//超时的时间

    private String serviceIp;
    private int port;

    public SocketUtil(SocketCallback socketCallback) {
        if (socketCallback instanceof SocketCallback)
            this.socketCallback = socketCallback;
        else
            throw new IllegalArgumentException();
    }

    /**
     * 连接socket
     */
    public void connectSocket(String ip, int port) throws IOException {
        socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(ip, port);

        socket.connect(socketAddress, TIME_OUT);
        if (socket.isConnected()) {
            socketCallback.onSocketConnected(ConnectBean.create(socket.getInetAddress().toString().substring(1),
                    socket.getLocalAddress().toString().substring(1), socket.getPort()));

            socketCallback.onSocketStateChanged(STATE_CONNECTED);

            dataRead = new DataInputStream(socket.getInputStream());
            dataWrite = new DataOutputStream(socket.getOutputStream());
        }
    }

    /**
     * 断开socket
     */
    public void disconnectSocket() {
        try {
            if (socket != null) {
                if (!socket.isInputShutdown()) {
                    socket.shutdownInput();
                }
                if (!socket.isOutputShutdown()) {
                    socket.shutdownOutput();
                }

                if (dataRead != null) {
                    dataRead.close();
                }
                if (dataWrite != null) {
                    dataWrite.close();
                }
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            dataWrite = null;
            dataRead = null;
            socket = null;
            socketCallback.onSocketDisconnect();
            socketCallback.onSocketStateChanged(STATE_DISCONNECT);
        }
    }

    /**
     * 写入数据
     */
    public void write(byte[] data) throws IOException {
        if (dataWrite != null) {
            dataWrite.write(data);
            dataWrite.flush();
        }
    }

    /**
     * 读取数据
     */
    public void read() throws Exception{
        if (dataRead != null) {
            byte[] buffer = new byte[1024 * 1];//读取数据的最大容量
            byte[] temp;
            int len = 0;
            if ((len = dataRead.read(buffer)) > -1) {
                temp = new byte[len];
                System.arraycopy(buffer, 0, temp, 0, len);
                socketCallback.onSocketReceive(temp);//读取到数据
                temp = null;
            }
        }
    }
}
