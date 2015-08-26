package com.angcyo.util.bean;

/**
 * Created by angcyo on 2015-04-14 014.
 */
public class ConnectBean {
    public String serviceAddress;
    public String localAddress;
    public int port;

    private ConnectBean(String serviceAddress, String localAddress, int port) {
        this.serviceAddress = serviceAddress;
        this.localAddress = localAddress;
        this.port = port;
    }

    public static ConnectBean create(String serviceAddress, String localAddress, int port) {
        return new ConnectBean(serviceAddress, localAddress, port);
    }
}
