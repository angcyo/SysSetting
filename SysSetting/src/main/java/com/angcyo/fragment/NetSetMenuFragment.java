package com.angcyo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.angcyo.syssetting.R;

public class NetSetMenuFragment extends MenuBaseFrgment {

	public NetSetMenuFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_net_menu, container,
				false);
		((RadioGroup)rootView.findViewById(R.id.id_net_set_radio_group)).setOnCheckedChangeListener(this);
		return rootView;
	}

	@Override
	public void onCheckChanged(int id) {
		switch (id) {
			case R.id.id_rb_net_wifi:
				itemChangedListener.onItemChanged(NetSetContentFragment.STYLE_NET_WIFI);
				break;
			case R.id.id_rb_net_wan:
				itemChangedListener.onItemChanged(NetSetContentFragment.STYLE_NET_WAN);
				break;
			default:
				itemChangedListener.onItemChanged(NetSetContentFragment.STYLE_NET_WIFI);
				break;
		}
	}
}
