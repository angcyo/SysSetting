package com.angcyo.util;

import com.angcyo.util.bean.ConnectBean;

/**
 * Created by angcyo on 2015-04-14 014.
 */
public interface SocketCallback {
    /**
     * 网络连接上
     */
    void onSocketConnected(ConnectBean bean);

    /**
     * 网络获取到数据
     */
    void onSocketReceive(byte[] data);

    /**
     * 网络断开
     */
    void onSocketDisconnect();

    /**
     * 网络状态发生改变
     */
    void onSocketStateChanged(int newState);
}
