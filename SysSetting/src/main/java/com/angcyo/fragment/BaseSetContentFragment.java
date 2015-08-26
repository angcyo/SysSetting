package com.angcyo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.angcyo.syssetting.R;
import com.angcyo.util.Util;
import com.angcyo.util.XmlSetting;
import com.angcyo.view.IPEditText;

public class BaseSetContentFragment extends BaseFragment {
    public static final int STYLE_SERVER_IP = 0x001001;
    public static final int STYLE_TERMINAL_NAME = 0x001002;
    IPEditText ipEditText;
    Button btSave;
    EditText etTerminalName;

    public BaseSetContentFragment() {
        default_layout_style = STYLE_SERVER_IP;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        switch (style) {
            case STYLE_SERVER_IP:
                rootView = inflater.inflate(R.layout.layout_base_server_ip, container,
                        false);
                initServerIpLayout();
                break;
            case STYLE_TERMINAL_NAME:
                rootView = inflater.inflate(R.layout.layout_base_terminal_name,
                        container, false);
                initTerminalNameLayout();
                break;
            default:
                rootView = inflater.inflate(R.layout.layout_base_server_ip, container,
                        false);
                initServerIpLayout();
                break;
        }

        return rootView;
    }

    private void initTerminalNameLayout() {
        etTerminalName = (EditText) rootView.findViewById(R.id.id_et_terminal_name);
        btSave = (Button) rootView.findViewById(R.id.id_server_ip_ok);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String terminalName = etTerminalName.getText().toString();
                if (terminalName != null) {
                    XmlSetting.setXmlTerminalName(terminalName);
                    Util.showPostMsg("终端名 " + terminalName + " 已保存");
                }
            }
        });

        String terminalName = XmlSetting.getXmlTerminalName();
        if (terminalName != null) {
            etTerminalName.setText(terminalName);
        }
    }

    private void initServerIpLayout() {
        ipEditText = (IPEditText) rootView.findViewById(R.id.id_server_edit_ip);
        btSave = (Button) rootView.findViewById(R.id.id_server_ip_ok);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipEditText.getIpText();
                if (ip != null) {
                    XmlSetting.setXmlServerIp(ip);
                    Util.showPostMsg("IP " + ip + " 已保存");
                }
            }
        });

        String ip = XmlSetting.getXmlServerIp();
        if (ip != null) {
            ipEditText.setIpText(ip);
        } else {
            ipEditText.setIpText("192.168.1.102");
        }
        ipEditText.clearAllFocus();
    }

}
