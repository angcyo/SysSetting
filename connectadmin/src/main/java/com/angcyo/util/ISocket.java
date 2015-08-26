package com.angcyo.util;

/**
 * Created by angcyo on 2015-04-15 015.
 */
public interface ISocket {
    void setServiceIp(String ip);

    void setPort(int port);

    void setDeviceName(String name);

    void sendMsg(String msg);
}
