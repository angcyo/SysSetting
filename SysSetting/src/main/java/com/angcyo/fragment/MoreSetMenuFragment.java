package com.angcyo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.angcyo.syssetting.R;

public class MoreSetMenuFragment extends MenuBaseFrgment {

	public MoreSetMenuFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_more_menu, container,
				false);
		((RadioGroup) rootView.findViewById(R.id.id_more_set_radio_group)).setOnCheckedChangeListener(this);
		return rootView;
	}

	@Override
	public void onCheckChanged(int id) {
		switch (id) {
			case R.id.id_rb_more_run_bmp:
				itemChangedListener.onItemChanged(MoreSetContentFragment.STYLE_MORE_RUN_BMP);
				break;
			case R.id.id_rb_more_time_volume:
				itemChangedListener.onItemChanged(MoreSetContentFragment.STYLE_MORE_TIME_VOLUME);
				break;
			case R.id.id_rb_more_time_close:
				itemChangedListener.onItemChanged(MoreSetContentFragment.STYLE_MORE_TIME_COLSE);
				break;
			case R.id.id_rb_more_apk_ver:
				itemChangedListener.onItemChanged(MoreSetContentFragment.STYLE_MORE_APK_VER);
				break;
//			case R.id.id_rb_more_add_task:
//				itemChangedListener.onItemChanged(MoreSetContentFragment.STYLE_MORE_ADD_TASK);
//				break;
			default:
				itemChangedListener.onItemChanged(MoreSetContentFragment.STYLE_MORE_RUN_BMP);
				break;
		}

	}
}
