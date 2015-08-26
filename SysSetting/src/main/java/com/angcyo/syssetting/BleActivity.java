package com.angcyo.syssetting;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ViewSwitcher;

import com.angcyo.syssetting.base.BaseFragmentActivity;
import com.angcyo.util.Logger;
import com.angcyo.util.ble.BleUtile;

/**
 * Created by angcyo on 2015-03-31 031.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleActivity extends BaseFragmentActivity implements BluetoothAdapter.LeScanCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

       switchView = (ViewSwitcher) findViewById(R.id.id_switch_view);
        switchView.setInAnimation(this, R.anim.abc_fade_in);
        switchView.setOutAnimation(this, R.anim.abc_fade_out);
//        Logger.e(BleUtile.isSysSupportBle(this) ? "支持BLE" : "不支持BLE");
    }

    public void openBle(View view){
        new BleUtile().openBle();
    }

    public void closeBle(View view){
        new BleUtile().closeBle();
    }

    public void getAllBle(View view){
        new BleUtile().getAllBondedDevices();
    }

//    public void (View view){
//
//    }

    BleUtile ble = new BleUtile();

    public void startScanner(View view){
        ble.startScanner(this);
    }

    public void stopScanner(View view){
        ble.stopScanner(this);
    }

    //蓝牙搜索回调
    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logger.e("BleName:" + device.getName() + "\nBleAddress:" + device.getAddress());
            }
        });

    }

    ViewSwitcher switchView;

    public void onPrevView(View view){
        switchView.showNext();
    }

    public  void onNextView(View view){
        switchView.showPrevious();
    }
}
