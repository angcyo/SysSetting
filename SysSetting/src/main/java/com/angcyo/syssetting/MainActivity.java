package com.angcyo.syssetting;

import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;

import com.angcyo.fragment.SettingDialogFragment;
import com.angcyo.util.Logger;
import com.angcyo.util.Util;
import com.angcyo.util.XmlSetting;

public class MainActivity extends FragmentActivity {

    public static final int KEY_MENU = 82;//菜单按键的键码值

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String bgPath = XmlSetting.getXmlRunImage();
        if (!Util.isEmpty(bgPath)) {
            View bgView = findViewById(R.id.id_layout_main);
            bgView.setBackground(new BitmapDrawable(getResources(), bgPath));
        } else {
            onBtClick(null);
        }
        //        showDisplayMetrics();

        //        Logger.e("rootPath-" + FileUtil.getAppRootPath(this));
    }

    public void onBtClick(View view) {
        DialogFragment dialogFragment = new SettingDialogFragment();
        // dialogFragment.getDialog().setCanceledOnTouchOutside(false);

        dialogFragment.show(getSupportFragmentManager(), "tag");
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        Logger.e("KeyMultiple:" + "KeyCode::" + keyCode + "::KeyAction::" + event.getAction());
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        Logger.e("KeyShortcut:" + "KeyCode::" + keyCode + "::KeyAction::" + event.getAction());
        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        Logger.e("KeyLongPress:" + "KeyCode::" + keyCode + "::KeyAction::" + event.getAction());
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KEY_MENU) {//如果按下菜单键
            onBtClick(null);
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    void showDisplayMetrics() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        Logger.e("xdpi:" + displayMetrics.xdpi + " ydpi:" + displayMetrics.ydpi
                + " dpi:" + displayMetrics.densityDpi + " ds:" + displayMetrics.density);
        Logger.e("widthPx:" + displayMetrics.widthPixels + " heightPx:" + displayMetrics.heightPixels);
    }
}
