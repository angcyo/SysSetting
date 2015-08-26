package com.angcyo.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.angcyo.syssetting.R;
import com.angcyo.syssetting.SubSettingActivity;

public class SettingDialogFragment extends DialogFragment implements
		OnClickListener {

	View lastFocusView;//保存最后具有焦点的view,用户恢复

	public SettingDialogFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, getTheme());
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		// dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_fragment_setting,
				container, false);
		rootView.findViewById(R.id.id_bt_base_set).setOnClickListener(this);
		rootView.findViewById(R.id.id_bt_net_set).setOnClickListener(this);
		rootView.findViewById(R.id.id_bt_more_set).setOnClickListener(this);
		rootView.findViewById(R.id.id_bt_ex_set).setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (lastFocusView!=null){
			lastFocusView.requestFocus();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getActivity(), SubSettingActivity.class);

		int sub_set = SubSettingActivity.SUB_BASE_SET;

		switch (v.getId()) {
		case R.id.id_bt_base_set:
			sub_set = SubSettingActivity.SUB_BASE_SET;
			break;
		case R.id.id_bt_more_set:
			sub_set = SubSettingActivity.SUB_MORE_SET;
			break;
		case R.id.id_bt_ex_set:
			sub_set = SubSettingActivity.SUB_EX_SET;
			break;
		case R.id.id_bt_net_set:
			sub_set = SubSettingActivity.SUB_NET_SET;
			break;
		default:
			break;
		}
		lastFocusView = v;
		intent.putExtra(SubSettingActivity.SUB_SET_NAME, sub_set);
		startActivity(intent);
	}

}
