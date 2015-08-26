package com.angcyo.fragment.adapter.node;

/**
 * Created by angcyo on 2015-03-31 031.
 */
public class WifiConnectInfo {
    public String SSID;
    public int security;

    public WifiConnectInfo(String SSID, int security) {
        this.SSID = SSID;
        this.security = security;
    }
}
