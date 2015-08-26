package com.angcyo.connectadmin.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.angcyo.connectadmin.MainActivity;
import com.angcyo.connectadmin.service.SocketService;

/**
 * Created by angcyo on 2015-04-15 015.
 */
public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {//开机广播
            Intent actionIntent = new Intent(context, SocketService.class);
            context.startService(actionIntent);
        } else if (action.equalsIgnoreCase(SocketService.ACTION_REBOOT_SOCKET_SERVICE)) {//重启服务的广播
            Intent actionIntent = new Intent(context, SocketService.class);
            context.startService(actionIntent);
        } else if (action.equalsIgnoreCase(SocketService.ACTION_START_MAIN_ACTIVITY)) {//启动主界面的广播
            Intent actionIntent = new Intent(context, MainActivity.class);
            context.startActivity(actionIntent);
        }
    }
}
