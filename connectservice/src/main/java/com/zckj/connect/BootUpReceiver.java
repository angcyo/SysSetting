package com.zckj.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent i = new Intent(arg0, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			arg0.startActivity(i);
		} else if (arg1.getAction().equals("myAction")) {
			Intent i = new Intent(arg0, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			arg0.startActivity(i);
		}
	}
}
