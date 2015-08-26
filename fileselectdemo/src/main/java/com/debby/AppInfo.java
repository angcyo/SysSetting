package com.debby;

import android.graphics.drawable.Drawable;

public class AppInfo {
	protected String strAppLabel;
	protected String strActivityName;
	protected Drawable dwAppIco;
	protected String strPackName;
	protected String strVerName;
	protected int nVerCode;
	protected boolean isSysApp = false;

	public boolean isSysApp() {
		return isSysApp;
	}

	public void setSysApp(boolean isSysApp) {
		this.isSysApp = isSysApp;
	}

	public String getStrVerName() {
		return strVerName;
	}

	public void setStrVerName(String strVerName) {
		this.strVerName = strVerName;
	}

	public int getnVerCode() {
		return nVerCode;
	}

	public void setnVerCode(int nVerCode) {
		this.nVerCode = nVerCode;
	}

	public String getStrAppLabel() {
		return strAppLabel;
	}

	public void setStrAppLabel(String strAppLabel) {
		this.strAppLabel = strAppLabel;
	}

	public String getStrActivityName() {
		return strActivityName;
	}

	public void setStrActivityName(String strActivityName) {
		this.strActivityName = strActivityName;
	}

	public Drawable getDwAppIco() {
		return dwAppIco;
	}

	public void setDwAppIco(Drawable dwAppIco) {
		this.dwAppIco = dwAppIco;
	}

	public String getStrPackName() {
		return strPackName;
	}

	public void setStrPackName(String strPackName) {
		this.strPackName = strPackName;
	}

}
