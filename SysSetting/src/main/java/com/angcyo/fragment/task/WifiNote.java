package com.angcyo.fragment.task;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by angcyo on 2015-03-20 020.
 */
public class WifiNote {
    public int wifiState;//wifi 的状态
    public List<ScanResult> wifiList;//wifi 列表
    public String curWifiSSID;// 已经连接上的Wifi SSID

    public WifiNote() {
//        this.wifiState = 1;
//        this.wifiList = new ArrayList<>();
    }
}
