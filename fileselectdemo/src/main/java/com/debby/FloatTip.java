package com.debby;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class FloatTip {

	static Context activity;
	static int TOOL_BAR_HIGH;
	static WindowManager wm;
	static WindowManager.LayoutParams params;
	static TextView textView;

	public static void show(Context acy, String tipString) {
		if (wm == null) {
			init(acy);
		}
		if (textView == null) {
			textView = getTextView(acy);
			wm.addView(textView, params);
		}
		textView.setText(tipString);

		// params.width = WindowManager.LayoutParams.FILL_PARENT;
		// params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// params.alpha = 80;
		//
		// params.gravity = Gravity.LEFT | Gravity.TOP;
		// // 以屏幕左上角为原点，设置x、y初始值
		// params.x = 0;
		// params.y = 0;

		// tv = new MyTextView(TopFrame.this);
		// wm.addView(tv, params);
	}

	public static void hide() {
		if (wm != null && textView != null) {
			wm.removeView(textView);
		}
	}

	protected static void init(Context acy) {
		if (acy == null) {
			return;
		}
		activity = acy;

		wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT, 60,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.x = 0;
		params.y = 60;
		//
		// params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
		// | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		// params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
		// | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
	}

	protected int getToolHight(Activity acy) {
		Rect frame = new Rect();
		((Activity) activity).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		TOOL_BAR_HIGH = frame.top;
		return frame.top;
	}

	protected static TextView getTextView(Context acy) {
		TextView textView = new TextView(acy);
		textView.setBackgroundColor(Color.parseColor("#8A1083"));
		textView.setTextColor(Color.parseColor("#FCFCFC"));
		return textView;
	}

}
