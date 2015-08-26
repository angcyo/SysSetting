package com.angcyo.fragment.task;

import android.net.wifi.ScanResult;
import android.os.AsyncTask;

import com.angcyo.util.wifi.WifiAdminEx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angcyo on 2015-03-19 019.
 */
public abstract class AsyncScanWifi extends AsyncTask<WifiAdminEx, Void, WifiNote> {

    @Override
    protected WifiNote doInBackground(WifiAdminEx... params) {
        WifiAdminEx wifiAdmin = params[0];
        WifiNote wifiNote = new WifiNote();
        if (wifiAdmin == null) {
            return null;
        }
        int wifiState = wifiAdmin.checkState();
        wifiNote.wifiState = wifiState;
//        Logger.e("Wifi 的状态:::" + wifiState);
        if (wifiState == 2 || wifiState == 3) {
            wifiAdmin.startScan();
            wifiNote.wifiList = wifiAdmin.getWifiList();
            wifiNote.curWifiSSID = wifiAdmin.getSSID();

//            Logger.e("可用Wifi数量:::" + wifiNote.wifiList.size());
           /* if (wifiNote.wifiList != null) {
                for (ScanResult result : wifiNote.wifiList) {
                    Logger.e("BSSID:" + result.BSSID + " SSID:" + result.SSID + " level:" + result.level + " capabilities:" + result.capabilities + " frequency:" + result.frequency);
                }
            }*/
        }
        return wifiNote;
    }

    @Override
    protected void onPostExecute(WifiNote wifiNote) {
        super.onPostExecute(wifiNote);
        List<ScanResult> newWifiList = new ArrayList<>();

        if (wifiNote == null || wifiNote.wifiList == null) {
            onScanResults(null);
            return;
        }

        for (int i = 0; i < wifiNote.wifiList.size(); i++) {
            ScanResult result = wifiNote.wifiList.get(i);
            if (result.SSID != null && result.SSID.length() > 1) {
                newWifiList.add(result);
            }
        }
        wifiNote.wifiList = newWifiList;
        onScanResults(wifiNote);
    }

    public abstract void onScanResults(WifiNote note);
}
