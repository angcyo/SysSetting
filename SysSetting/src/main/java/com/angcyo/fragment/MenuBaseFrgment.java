package com.angcyo.fragment;

import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public abstract class MenuBaseFrgment extends BaseFragment implements
		OnCheckedChangeListener {

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (itemChangedListener != null) {
			onCheckChanged(checkedId);
		}
	}
	public abstract void onCheckChanged(int id);
}
