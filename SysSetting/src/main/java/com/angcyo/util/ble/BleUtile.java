package com.angcyo.util.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.angcyo.util.Logger;

import java.util.Set;
import java.util.UUID;

/**
 * 蓝牙需要的权限
 * <p/>
 * <uses-permission android:name="android.permission.BLUETOOTH"/>
 * <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
 * <p/>
 * Created by angcyo on 2015-03-31 031.
 */
public class BleUtile {

    BluetoothAdapter adapter;

    public BleUtile() {
        //如果 Adapter,说明设备不支持蓝牙
        adapter = BluetoothAdapter.getDefaultAdapter();//API 5
//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public static boolean isSysSupportBle(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // 满足条件，表示手机支持
            return true;
        }
        return false;
    }

    //是否已经启动蓝牙
    public boolean isOpen(){
        return adapter.isEnabled();
    }

    //打开蓝牙
    public void openBle() {
        adapter.enable();//打开蓝牙
    }

    //关闭蓝牙
    public void closeBle() {
        adapter.disable();//关闭蓝牙
    }

    //得到所有已经适配过的设备
    public Set<BluetoothDevice> getAllBondedDevices() {
        Set<BluetoothDevice> paireDevices = adapter.getBondedDevices();
        if (paireDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : paireDevices) {
                Logger.e("Ble Name:" + bluetoothDevice.getName() + "\nBle Address:" + bluetoothDevice.getAddress());
            }
        } else {
            Logger.e("没有配对过的设备");
        }
        return paireDevices;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startScanner(BluetoothAdapter.LeScanCallback callback){
        adapter.startLeScan(callback);
        Logger.e("开始搜索蓝牙设备...");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopScanner(BluetoothAdapter.LeScanCallback callback){
        adapter.stopLeScan(callback);
        Logger.e("已停止搜索蓝牙设备");
    }

    //搜索指定的UUID 蓝牙设备
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startScanner(UUID uuid[], BluetoothAdapter.LeScanCallback callback){
        adapter.startLeScan(uuid, callback);

    }

    //连接到指定的Ble设备,参数为 设备的MAC地址
    public BluetoothGatt connectBle(Context context, String address, BluetoothGattCallback callback) {
        if (adapter == null || address == null) {
            Logger.e("BluetoothAdapter not initialized or unspecified address.");
            return null;
        }
//        //以前连接过,调用重连
//        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
//                && mBluetoothGatt != null) {
//            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
//            if (mBluetoothGatt.connect()) {
//                mConnectionState = STATE_CONNECTING;
//                return true;
//            } else {
//                return false;
//            }
//        }
        final BluetoothDevice device = adapter.getRemoteDevice(address);
        if (device == null) {
            Logger.e( "Device not found.  Unable to connect.");
            return null;
        }
        // 不希望自动连接,第二个参数设置为false,表示直连
        BluetoothGatt mBluetoothGatt = device.connectGatt(context, false, callback);
        Logger.e("Trying to create a new connection.");
        return mBluetoothGatt;
    }

}
