package com.angcyo.fragment.port;

import com.angcyo.fragment.adapter.node.WifiStaticIpNode;

/**
 * Created by angcyo on 2015-03-31 031.
 */
public interface OnWifiConnect {
    void onConnect(String SSID, String password, int security);
    void onConnectStaticIp(String SSID, String password, int security, WifiStaticIpNode node);
}
