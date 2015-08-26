package com.angcyo.util.bean;

import java.io.Serializable;

/**
 * Created by angcyo on 2015-04-17 017.
 */
public class AppInfo implements Serializable {
    private String label = "";//标签,
    private String packageName = "";//包名
    private String processName = "";//进程名

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public AppInfo() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
