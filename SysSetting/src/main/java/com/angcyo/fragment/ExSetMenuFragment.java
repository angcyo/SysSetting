package com.angcyo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.angcyo.syssetting.R;

public class ExSetMenuFragment extends MenuBaseFrgment {

	public ExSetMenuFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater
				.inflate(R.layout.fragment_ex_menu, container, false);
		((RadioGroup) rootView.findViewById(R.id.id_ex_menu_radio_group))
				.setOnCheckedChangeListener(this);
		return rootView;
	}

	@Override
	public void onCheckChanged(int id) {
		switch (id) {
		case R.id.id_rb_ex_display:
			itemChangedListener
					.onItemChanged(ExSetContentFragment.STYLE_EX_DISPLAY);
			break;
		case R.id.id_rb_ex_password:
			itemChangedListener
					.onItemChanged(ExSetContentFragment.STYLE_EX_PASSWORD);
			break;
		case R.id.id_rb_ex_storage:
			itemChangedListener
					.onItemChanged(ExSetContentFragment.STYLE_EX_STORAGE);
			break;

		case R.id.id_rb_ex_update_path:
			itemChangedListener
					.onItemChanged(ExSetContentFragment.STYLE_EX_UPDATE_PATH);
			break;
			case R.id.id_rb_ex_clear_fete:
				itemChangedListener
						.onItemChanged(ExSetContentFragment.STYLE_EX_CLEAR_FETE);
				break;

		default:
			itemChangedListener
					.onItemChanged(ExSetContentFragment.STYLE_EX_DISPLAY);
			break;
		}

	}
}
