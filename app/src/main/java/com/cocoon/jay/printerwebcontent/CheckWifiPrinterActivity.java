package com.cocoon.jay.printerwebcontent;


import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.cocoon.jay.printerwebcontent.printer.wifi.ConnectWiFiPrinterDialog;
import com.cocoon.jay.printerwebcontent.printer.wifi.ConnectWiFiPrinterManager;
import com.cocoon.jay.printerwebcontent.printer.wifi.IConnectWiFiCallBackListener;
import com.cocoon.jay.printerwebcontent.printer.wifi.PrinterUtils;

import java.io.OutputStream;
import java.net.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckWifiPrinterActivity extends AppCompatActivity
        implements IConnectWiFiCallBackListener {


    @BindView(R.id.tv_commit)
    TextView tvCommit;
    @BindView(R.id.webview)
    WebView webview;
    private String url = "https://www.baidu.com";
        private ConnectWiFiPrinterDialog mDialog;
    private ConnectWiFiPrinterManager manager;
    private PrinterUtils printerUtils = new PrinterUtils();
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //我们要先调用enableSlowWholeDocumentDraw() ，才能把整个网页的长图生成。这个enableSlowWholeDocumentDraw() 方法 需要在WebView声明之前调用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        setContentView(R.layout.activity_print);
        ButterKnife.bind(this);


        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // 如果证书一致，忽略错误
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                bitmap = printerUtils.saveImage(webview);
            }
        });


        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true); // 显示放大缩小 controler
        settings.setSupportZoom(true); // 可以缩放
        settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);// 默认缩放模式
        settings.setDisplayZoomControls(false);// 隐藏按钮

        settings.setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true);

        webview.loadUrl(url);
    }



    @Override
    public void callBackListener(String ip, int port, Socket socket, OutputStream outputStream) {
        if (outputStream != null) {
            handler.sendEmptyMessage(0);
            printerUtils.setIsconnected(true);
            printerUtils.setOutputStream(outputStream);
        } else {
            printerUtils.setIsconnected(false);
            handler.sendEmptyMessage(1);
        }
    }


    /**
     * 内部静态handler
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(CheckWifiPrinterActivity.this, "连接成功", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(CheckWifiPrinterActivity.this, "连接失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    @OnClick(R.id.tv_commit)
    public void onViewClicked() {
        if(mDialog == null)
                    mDialog = new ConnectWiFiPrinterDialog(CheckWifiPrinterActivity.this,
                            R.style.selectdialogstyle, R.layout.dialog_connect_printer);
                mDialog.setmOnButtonClickListener(new ConnectWiFiPrinterDialog.OnButtonClickListener() {


                    @Override
                    public void onLinkClick(String ip , int port) {
                        ConnectWiFiPrinterManager.getInstance(CheckWifiPrinterActivity.this).connectWiFiPrinter(ip, port);
                    }

                    @Override
                    public void onPrinterClick() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {


//                                printerUtils.printText("测试打印。。测试打印测试打印测试打印测试打印测试打印测测试打印测试打测试打印测试打测试打印测试打测试打印测试打");
                                printerUtils.print(bitmap);

//                                PrintHelper photoPrinter = new PrintHelper(mContext);
//                                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
//                                photoPrinter.printBitmap(jobName, bitmap);




                            }}).start();


                    }
                });
                mDialog.show();

    }
}
