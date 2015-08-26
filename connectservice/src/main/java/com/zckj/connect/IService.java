package com.zckj.connect;

public interface IService {
	public void setusername(String name);

	public void sendmsg(String msg);

	public void setclienttype(String clienttype);

	public void Connect(String host, int port);

	public void Disconnect();

	public void installapk(String apkname);

	public void uninstallpackage(String packagename);
}
