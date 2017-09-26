package com.cocoon.jay.printerwebcontent;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.cocoon.jay.printerwebcontent.printer.bluetooth.BasePrintActivity;
import com.cocoon.jay.printerwebcontent.printer.bluetooth.BluetoothUtil;
import com.cocoon.jay.printerwebcontent.printer.bluetooth.PrintUtil;
import com.cocoon.jay.printerwebcontent.printer.bluetooth.SelectBluetoohDeviceDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BluetoothPrintActivity extends BasePrintActivity {


    private final static int TASK_TYPE_PRINT = 2;
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    @BindView(R.id.webview)
    WebView webview;
    private List<BluetoothDevice> printerDevices;
    private SelectBluetoohDeviceDialog mDialog;
    private String url = "https://www.baidu.com";
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                bitmap = PrintUtil.saveImage(webview);
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
    public void onConnected(BluetoothSocket socket, int taskType) {
        switch (taskType) {
            case TASK_TYPE_PRINT:

                PrintUtil.print(socket, bitmap);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        fillAdapter();
    }


    /**
     * 从所有已配对设备中找出打印设备并显示
     */
    private void fillAdapter() {
        //推荐使用 BluetoothUtil.getPairedPrinterDevices()
        printerDevices = BluetoothUtil.getPairedDevices();

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.setPrinterDevices(printerDevices);
        }
    }



    private void connectDevice(int taskType, BluetoothDevice device){
        if(device!= null) {
            super.connectDevice(device, taskType);
            mDialog.dismiss();
        }else{
            Toast.makeText(this,"请选择打印设备",Toast.LENGTH_SHORT);
        }
    }



    @OnClick(R.id.tv_commit)
    public void onViewClicked() {

        if (printerDevices.size() > 0) {

            if(mDialog == null)
                mDialog = new SelectBluetoohDeviceDialog(BluetoothPrintActivity.this,
                        R.style.selectdialogstyle, R.layout.dialog_select_bluetoohdevice, printerDevices);
            mDialog.setmOnButtonClickListener(new SelectBluetoohDeviceDialog.OnButtonClickListener() {

                @Override
                public void onGoSetButtonClick() {

                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                }

                @Override
                public void onCertainButtonClick(BluetoothDevice devic) {

                    connectDevice(TASK_TYPE_PRINT, devic);
                }
            });
            mDialog.show();

        } else {
            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
        }

    }
}
