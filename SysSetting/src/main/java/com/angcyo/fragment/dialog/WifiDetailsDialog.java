package com.angcyo.fragment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.angcyo.fragment.adapter.node.WifiDetailsNode;
import com.angcyo.fragment.port.OnClearWifi;
import com.angcyo.syssetting.R;

/**
 * Created by angcyo on 2015-03-31 031.
 */
public class WifiDetailsDialog extends Dialog implements View.OnClickListener {
    int netId = -1;

    WifiDetailsNode node;
    OnClearWifi clearWifi;
    TextView txSSID, txState, txLinkSpeed,
            txIP, txMac, txDns1, txDns2,
            txGateway, txNetmask, txServerIP;

    Button btCancel, btClear;

    public WifiDetailsDialog(Context context, int theme, WifiDetailsNode node, OnClearWifi listener) {
        super(context, theme);

        this.node = node;
        this.clearWifi = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        initDialogLayout();
    }

    private void initDialogLayout() {
        txSSID = (TextView) findViewById(R.id.id_wifi_ssid);
        txState = (TextView) findViewById(R.id.id_wifi_state);
        txLinkSpeed = (TextView) findViewById(R.id.id_wifi_linkspeed);
        txIP = (TextView) findViewById(R.id.id_wifi_ip);
        txMac = (TextView) findViewById(R.id.id_wifi_mac);
        txDns1 = (TextView) findViewById(R.id.id_wifi_dns1);
        txDns2 = (TextView) findViewById(R.id.id_wifi_dns2);
        txGateway = (TextView) findViewById(R.id.id_wifi_gateway);
        txNetmask = (TextView) findViewById(R.id.id_wifi_netmask);
        txServerIP = (TextView) findViewById(R.id.id_wifi_serverip);

        btCancel = (Button) findViewById(R.id.id_wifi_cancel);
        btClear = (Button) findViewById(R.id.id_wifi_clear);

        btCancel.setOnClickListener(this);
        btClear.setOnClickListener(this);
        initDialogData();
    }

    private void initDialogData() {
        netId  = node.netId;
        txSSID.setText(node.ssid);
        txState.setText(node.state);
        txLinkSpeed.setText(node.linkSpeed + "Mbps");
        txIP.setText(node.ip);
        txMac.setText(node.mac);
        txDns1.setText(node.dns1);
        txDns2.setText(node.dns2);
        txGateway.setText(node.gateway);
        txNetmask.setText(node.netmask);
        txServerIP.setText(node.serverAddress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_wifi_clear:
                if (clearWifi!=null)
                    clearWifi.onClearWifi(netId);
                cancel();
                break;
            case R.id.id_wifi_cancel:
                cancel();
                break;
            default:
                break;
        }
    }
}
