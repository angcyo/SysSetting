package com.angcyo.util.bean;

/**
 * Created by angcyo on 2015-04-17 017.
 */
public class DownTask {
    private String downFileName = "";        // 下载的文件名
    private int port = 9926;        // 端口
    private String serviceIp = "";        //下载的服务器地址
    private boolean install = false;   //下载完是否安装
    private String url = "";//如果设置了url,就不组装下载地址

    public DownTask() {
    }

    public String getDownFileName() {
        return downFileName;
    }

    public void setDownFileName(String downFileName) {
        this.downFileName = downFileName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public boolean isInstall() {
        return install;
    }

    public void setInstall(boolean install) {
        this.install = install;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
