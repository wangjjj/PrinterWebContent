package com.cocoon.jay.printerwebcontent.printer.wifi;

import java.io.OutputStream;
import java.net.Socket;

/**
 * 连接WiFi打印机接口回调
 */

public interface IConnectWiFiCallBackListener {

    void callBackListener(String ip, int port, Socket socket, OutputStream outputStream);
}
