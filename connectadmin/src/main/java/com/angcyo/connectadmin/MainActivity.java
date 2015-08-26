package com.angcyo.connectadmin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.angcyo.connectadmin.service.SocketService;
import com.angcyo.connectadmin.view.IPEditText;
import com.angcyo.util.CmdUtil;
import com.angcyo.util.Logger;
import com.angcyo.util.Setting;
import com.angcyo.util.SocketUtil;
import com.angcyo.util.Util;
import com.angcyo.util.bean.SocketInfo;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {

    Intent socketServiceIntent;//后台服务
    SocketServiceConnection socketServiceConnection;//用于绑定服务
    IPEditText ipEditText;
    EditText etDeviceName, etSendMsg;
    Button btServiceIp, btDeviceName, btSendMsg, btSubmit;
    TextView txServiceIp, txLocalIp, txLocalMac, txPort, txSocketState, txDeviceName;
    ListView listMsg;
    List<String> listMsgData = new ArrayList<String>();//保存收到的消息
    ListMsgAdapter listMsgAdapter = new ListMsgAdapter();
    private SocketService.MyBinder serviceBinder;//用于控制服务命令
    private BroadcastReceiver mUpdateSocketInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equalsIgnoreCase(SocketService.ACTION_UPDATE_SOCKET_INFO)) {//更新界面的广播
                SocketInfo info = (SocketInfo) intent.getExtras().getSerializable(SocketService.KEY_UPDATE_INFO);
                updateSocketInfo(info);
//                Logger.e("更新广播::" + info.getServiceIp() + ":" + info.getPort());
            } else if (action.equalsIgnoreCase(SocketService.ACTION_RECEIVE_INFO)) {//收到数据
                byte[] msg = intent.getExtras().getByteArray(SocketService.KEY_RECEIVE_INFO);

                addMsgToList(Util.getNowTime() + " " + "收到数据->" + new String(msg));//添加到list显示出来
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        socketServiceIntent = new Intent(getApplicationContext(), SocketService.class);
        socketServiceConnection = new SocketServiceConnection();

        CmdUtil.initProcess();//申请root权限

//        CmdUtil.getAllRunningProcess(this);

        firstRun();
    }

    private void firstRun() {
        if (Setting.isFirstRun(this)) {
            btServiceIp.setText("配置并启动");
            Toast.makeText(this, "首次运行,请先配置服务器IP", Toast.LENGTH_LONG).show();
        } else {
            startService(socketServiceIntent);//启动服务,服务启动之后,也可以执行绑定操作
        }
    }

    private void initView() {
        ipEditText = (IPEditText) findViewById(R.id.id_ipedit_text);
        etDeviceName = (EditText) findViewById(R.id.id_et_device_name);
        etSendMsg = (EditText) findViewById(R.id.id_et_send_msg);
        btServiceIp = (Button) findViewById(R.id.id_bt_service_ip);
        btDeviceName = (Button) findViewById(R.id.id_bt_device_name);
        btSendMsg = (Button) findViewById(R.id.id_bt_send_msg);
        txServiceIp = (TextView) findViewById(R.id.id_tx_service_ip);
        txLocalIp = (TextView) findViewById(R.id.id_tx_local_ip);
        txLocalMac = (TextView) findViewById(R.id.id_tx_local_mac);
        txPort = (TextView) findViewById(R.id.id_tx_port);
        txSocketState = (TextView) findViewById(R.id.id_tx_socket_state);
        listMsg = (ListView) findViewById(R.id.id_list_msg);
        txDeviceName = (TextView) findViewById(R.id.id_tx_device_name);
        btSubmit = (Button) findViewById(R.id.id_bt_submit);

        btServiceIp.setOnClickListener(this);
        btDeviceName.setOnClickListener(this);
        btSendMsg.setOnClickListener(this);
        btSubmit.setOnClickListener(this);

        initViewData();
    }

    private void initViewData() {
        listMsg.setAdapter(listMsgAdapter);
        ipEditText.setIpText(Setting.getServiceIp(this));
        etDeviceName.setText(Setting.getDeviceName(this));
    }

    @Override
    public void onClick(View v) {
        try {
            String str;
            switch (v.getId()) {
                case R.id.id_bt_device_name:
                    str = etDeviceName.getText().toString();
                    if (Util.isEmpty(str)) {
                        showEtError(etDeviceName, "不能为空");
                    } else {
                        serviceBinder.setDeviceName(str);
                        Setting.setDeviceName(MainActivity.this, str);
                        addMsgToList("请求修改设备名称为->" + str);
                    }
                    break;
                case R.id.id_bt_send_msg:
                    str = etSendMsg.getText().toString();
                    if (Util.isEmpty(str)) {
                        showEtError(etSendMsg, "不能为空");
                    } else {
                        serviceBinder.sendMsg(str);
                        addMsgToList("发送消息->" + str);
                    }

                    break;
                case R.id.id_bt_service_ip:
                    str = ipEditText.getIpText();
                    if (Setting.isFirstRun(MainActivity.this)) {
                        btServiceIp.setText("更新服务器IP");
                        Setting.setFirstRun(MainActivity.this, false);
                        startAndBindService();
                    } else {
                        serviceBinder.setServiceIp(str);
                        addMsgToList("设置服务器IP并重连->" + str);
                    }
                    Setting.setServiceIp(MainActivity.this, str);
                    break;
                case R.id.id_bt_submit:
                    String ip = ipEditText.getIpText();
                    String deviceName = etDeviceName.getText().toString();
                    if (!Setting.getServiceIp(MainActivity.this).equalsIgnoreCase(ip)) {//需要重连
                        if (Setting.getDeviceName(MainActivity.this).equalsIgnoreCase(deviceName)) {
                            serviceBinder.setServiceIp(ip);
                            addMsgToList("设置服务器IP并重连->" + ip);
                            addMsgToList("设备名称未修改");
                            Setting.setServiceIp(MainActivity.this, ip);
                        } else {
                            serviceBinder.setDeviceName(deviceName);
                            serviceBinder.setServiceIp(ip);
                            addMsgToList("设置服务器IP并重连->" + ip);
                            addMsgToList("请求修改设备名称->" + deviceName);
                            Setting.setServiceIp(MainActivity.this, ip);
                            Setting.setDeviceName(MainActivity.this, deviceName);
                        }
                    } else {//未修改服务器,不需要重连
                        if (Setting.getDeviceName(MainActivity.this).equalsIgnoreCase(deviceName)) {
                            addMsgToList("服务器IP未修改");
                            addMsgToList("设备名称未修改");
                        } else {
                            serviceBinder.setDeviceName(deviceName);
                            addMsgToList("服务器IP未修改");
                            addMsgToList("请求修改设备名称->" + deviceName);
                            Setting.setDeviceName(MainActivity.this, deviceName);
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Logger.e(e.toString());
            addMsgToList("绑定服务未成功.");
        }
    }

    private void showEtError(EditText view, String error) {
        view.setError(error);
        view.setFocusable(true);
        view.requestFocus();
    }

    private void addMsgToList(String msg) {
        listMsgData.add(msg);
        listMsgAdapter.notifyDataSetChanged();

        if (listMsg.getLastVisiblePosition() >= (listMsgData.size() - 2)) {
            listMsg.smoothScrollToPosition(listMsgData.size());//滚动到最后
        }
    }

    private void startAndBindService() {
        startService(socketServiceIntent);//启动服务,服务启动之后,也可以执行绑定操作
        bindService(socketServiceIntent, socketServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        //注册更新界面的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.ACTION_UPDATE_SOCKET_INFO);//更新状态信息的广播
        intentFilter.addAction(SocketService.ACTION_RECEIVE_INFO);//收到网络消息的广播
        registerReceiver(mUpdateSocketInfo, intentFilter);

        if (!Setting.isFirstRun(this)) {
            //绑定服务,用于操作服务
            bindService(socketServiceIntent, socketServiceConnection, BIND_AUTO_CREATE);
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(mUpdateSocketInfo);
            unbindService(socketServiceConnection);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("解除服务绑定出错:" + e.toString());
        }
        super.onStop();
    }

    private void goBackRun() {
        moveTaskToBack(true);
    }

    private void updateSocketInfo(SocketInfo info) {
        txDeviceName.setText(info.getDeviceName());
        txLocalMac.setText(info.getLocalMac());
        txLocalIp.setText(info.getLocalIp());
        txPort.setText(info.getPort() + "");
        txServiceIp.setText(info.getServiceIp());

        int state = info.getSocketState();
        switch (state) {
            case SocketUtil.STATE_CONNECTED:
                txSocketState.setText("已连接");
                break;
            case SocketUtil.STATE_CONNECTING:
                txSocketState.setText("正在连接");
                break;
            case SocketUtil.STATE_DISCONNECT:
                txSocketState.setText("已断开");
                break;
            case SocketUtil.STATE_EXCEPTION:
                txSocketState.setText("网络异常");
                break;
            case SocketUtil.STATE_UNKNOWN:
                txSocketState.setText("未知状态");
                break;
            default:
                break;
        }
    }

    private class SocketServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.e("已连接上服务");
            serviceBinder = (SocketService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.e("已断开服务的连接");
        }
    }


    private class ListMsgAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listMsgData.size();
        }

        @Override
        public Object getItem(int position) {
            return listMsgData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.list_item_msg, null);
                holder = new ViewHolder();
                holder.txMsg = (TextView) convertView.findViewById(R.id.id_tx_msg);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txMsg.setText(listMsgData.get(position));

            return convertView;
        }

        class ViewHolder {
            TextView txMsg;
        }
    }
}
