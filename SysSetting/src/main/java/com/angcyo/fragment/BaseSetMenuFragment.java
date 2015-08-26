package com.angcyo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.angcyo.syssetting.R;

public class BaseSetMenuFragment extends MenuBaseFrgment {

	public BaseSetMenuFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_base_menu, container,
				false);
		((RadioGroup) rootView.findViewById(R.id.id_base_set_rgroup))
				.setOnCheckedChangeListener(this);
		return rootView;
	}

	@Override
	public void onCheckChanged(int checkedId) {
		switch (checkedId) {
		case R.id.id_rb_server_ip:
			itemChangedListener
					.onItemChanged(BaseSetContentFragment.STYLE_SERVER_IP);
			break;

		case R.id.id_rb_terminal_name:
			itemChangedListener
					.onItemChanged(BaseSetContentFragment.STYLE_TERMINAL_NAME);
			break;

		default:
			break;
		}

	}

}
