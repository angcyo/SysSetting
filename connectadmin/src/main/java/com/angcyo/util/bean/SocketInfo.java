package com.angcyo.util.bean;

import com.angcyo.util.SocketUtil;

import java.io.Serializable;

/**
 * Created by angcyo on 2015-04-15 015.
 */
public class SocketInfo implements Serializable{
    private String deviceName = "-";
    private String serviceIp = "-.-.-.-";
    private String localIp = "-.-.-.-";
    private String localMac = "-.-.-.-";
    private int port = -1;
    private int socketState = SocketUtil.STATE_UNKNOWN;

    public SocketInfo() {
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getLocalMac() {
        return localMac;
    }

    public void setLocalMac(String localMac) {
        this.localMac = localMac;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSocketState() {
        return socketState;
    }

    public void setSocketState(int socketState) {
        this.socketState = socketState;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
