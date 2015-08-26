package com.zckj.connect;

/**
 * 下载类
 * @author Administrator
 *
 */
public class dldata {
	private String name; 		// 文件名
	private String port; 		// 端口
	private String address;		//地址
	private Boolean binstall;   //是否安装
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getport() {
		return port;
	}
	public void setport(String port) {
		this.port = port;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public Boolean getInstall(){
		return binstall;
	}
	public void setInstall(Boolean install){
		this.binstall = install;
	}
}
