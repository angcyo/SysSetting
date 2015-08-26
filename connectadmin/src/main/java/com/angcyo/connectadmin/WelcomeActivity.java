package com.angcyo.connectadmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.angcyo.connectadmin.service.SocketService;
import com.angcyo.connectadmin.view.IPEditText;
import com.angcyo.util.Setting;
import com.angcyo.util.Util;

/**
 * Created by angcyo on 2015-04-20 020.
 */
public class WelcomeActivity extends Activity implements OnClickListener {

    IPEditText ipEditText;
    Button btSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        init();

//        System.getProperties().list(System.out);
//        Logger.e("Activity->" + Thread.currentThread().getId());
//        AsyncTask
    }

    public void onMain(View view) {
        if (Setting.isFirstRun(this)) {
            Util.showPostMsg(this, "请先配置服务器IP");
        } else {
            view.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, MainActivity.class);
//            Intent intent = new Intent("com.angcyo.main");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            overridePendingTransition(R.anim.scale_0to1, R.anim.alpha_1to0);
            finish();
        }
    }

    private void init() {
        if (Setting.isFirstRun(this)) {

            //自动读取ip地址
            String ip = Util.getSerIp();

            ipEditText = (IPEditText) findViewById(R.id.id_ipedit_text);
            btSave = (Button) findViewById(R.id.id_bt_save);
            btSave.setOnClickListener(this);

            findViewById(R.id.id_layout_first).setVisibility(View.VISIBLE);

            if (ip != null && !Util.isEmpty(ip) && ip.split("\\.").length == 4) {
                ipEditText.setIpText(ip);
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_bt_save) {
            Setting.setServiceIp(this, ipEditText.getIpText());
            Util.showPostMsg(this, "服务器IP: " + ipEditText.getIpText() + " 已保存");

            Setting.setFirstRun(this, false);
            startService();
            findViewById(R.id.id_layout_first).setVisibility(View.GONE);
        }
    }

    private void startService() {
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);
    }
}
