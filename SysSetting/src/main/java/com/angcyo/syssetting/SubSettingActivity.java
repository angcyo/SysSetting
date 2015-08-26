package com.angcyo.syssetting;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.angcyo.fragment.BaseFragment;
import com.angcyo.fragment.BaseSetContentFragment;
import com.angcyo.fragment.BaseSetMenuFragment;
import com.angcyo.fragment.ExSetContentFragment;
import com.angcyo.fragment.ExSetMenuFragment;
import com.angcyo.fragment.MoreSetContentFragment;
import com.angcyo.fragment.MoreSetMenuFragment;
import com.angcyo.fragment.NetSetContentFragment;
import com.angcyo.fragment.NetSetMenuFragment;
import com.angcyo.fragment.port.OnMenuItemChanged;

public class SubSettingActivity extends FragmentActivity implements
        OnMenuItemChanged {

    public static final String SUB_SET_NAME = "sub_set_name";// key
    // public int sub_set_style = 0x0004;

    public static final int SUB_NET_SET = 0x0001;
    public static final int SUB_MORE_SET = 0x0002;
    public static final int SUB_BASE_SET = 0x0004;
    public static final int SUB_EX_SET = 0x0008;

    public SubSettingActivity() {
    }

    protected int sub_set = 0x0004;// 保存当前是什么类型的设置界面
    protected FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//6.
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        // ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;8
//		setRequestedOrientation(6);
        fm = getSupportFragmentManager();

        setContentView(R.layout.sub_setting_main);
        sub_set = getIntent().getIntExtra(SUB_SET_NAME, SUB_MORE_SET);
        setContentFragment();
    }

    /**
     * 根据不同设置类型,填充不同布局Fragment
     */
    private void setContentFragment() {
        switch (sub_set) {
            case SUB_BASE_SET:
                mainBaseSetFragment();
                break;
            case SUB_EX_SET:
                mainExSetFragment();
                break;
            case SUB_MORE_SET:
                mainMoreSetFragment();
                break;
            case SUB_NET_SET:
                mainNetSetFragment();
                break;
            default:
                mainBaseSetFragment();
                break;
        }
    }

    public void replaceFragment(int resId, BaseFragment fragment) {
        replaceFragment(resId, fragment, true);
    }

    public void replaceFragment(int resId, BaseFragment fragment, boolean anim) {
        FragmentTransaction ft = fm.beginTransaction();
        if (anim)
            ft.setCustomAnimations(R.anim.tran_ttb_enter_frag,
                    R.anim.tran_ttb_exit_frag);
        ft.replace(resId, fragment).commit();
    }

    public void replaceFragment(BaseFragment menuFragment,
                                BaseFragment contentFragment) {
        replaceFragment(R.id.layout_menu, menuFragment, false);
        replaceFragment(R.id.layout_content, contentFragment);
    }

    private void mainBaseSetFragment() {
        replaceFragment(new BaseSetMenuFragment(), new BaseSetContentFragment());
    }

    private void mainNetSetFragment() {
        replaceFragment(new NetSetMenuFragment(), new NetSetContentFragment());
    }

    private void mainExSetFragment() {
        replaceFragment(new ExSetMenuFragment(), new ExSetContentFragment());
    }

    private void mainMoreSetFragment() {
        replaceFragment(new MoreSetMenuFragment(), new MoreSetContentFragment());
    }

    @Override
    public void onItemChanged(int itemId) {
        BaseFragment fragment;
        Bundle args = new Bundle();
        args.putInt(BaseFragment.LAYOUT_STYLE, itemId);

        switch (sub_set) {
            case SUB_BASE_SET:
                fragment = new BaseSetContentFragment();
                break;
            case SUB_EX_SET:
                fragment = new ExSetContentFragment();
                break;
            case SUB_MORE_SET:
                fragment = new MoreSetContentFragment();
                break;
            case SUB_NET_SET:
                fragment = new NetSetContentFragment();
                break;
            default:
                fragment = new BaseSetContentFragment();
                break;
        }
        fragment.setArguments(args);
        replaceContentFragment(fragment);
    }

    private void replaceContentFragment(BaseFragment fragment) {
        replaceFragment(R.id.layout_content, fragment);
    }

}
