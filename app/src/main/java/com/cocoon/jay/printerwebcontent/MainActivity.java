package com.cocoon.jay.printerwebcontent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.tv_commit)
    TextView tvCommit;
    @BindView(R.id.tv_commit_1)
    TextView tvCommit1;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.tv_commit_2)
    TextView tvCommit2;
    @BindView(R.id.tv_commit_3)
    TextView tvCommit3;

    private String url = "https://www.baidu.com";
    private Bitmap bitmap;


    private PrintManager printManager;
    private PrintDocumentAdapter printAdapter;
    private String jobName;

    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        printAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        jobName = getString(R.string.app_name) + " Document";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //我们要先调用enableSlowWholeDocumentDraw() ，才能把整个网页的长图生成。这个enableSlowWholeDocumentDraw() 方法 需要在WebView声明之前调用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        setContentView(R.layout.activity_main);
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
                createWebPrintJob(webview);
                bitmap = captureWebViewLong(webview);
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


    /**
     * 将网页内容转成Bitmap
     *
     * @param webView
     */
    public static Bitmap captureWebViewLong(WebView webView) {
        Bitmap b = null;
        try {
//            webView.setDrawingCacheEnabled(true);
//            b = webView.getDrawingCache();


            Picture picture = webView.capturePicture();
            b = Bitmap.createBitmap(
                    picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            picture.draw(c);
            File file = new File("/sdcard/" + "test.jpg");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
            if (fos != null) {
                b.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }


    @OnClick({R.id.tv_commit, R.id.tv_commit_1, R.id.tv_commit_2, R.id.tv_commit_3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_commit:
//wifi连接 调系统打印 代码如此简单
                printManager.print(jobName, printAdapter,
                        new PrintAttributes.Builder().build());
                break;
            case R.id.tv_commit_1:
//网页转bitmap 进行打印
                PrintHelper photoPrinter = new PrintHelper(this);
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                photoPrinter.printBitmap(jobName, bitmap);

                break;
            case R.id.tv_commit_2:
                startActivity(new Intent(this, CheckWifiPrinterActivity.class));

                break;
            case R.id.tv_commit_3:
//蓝牙打印机贵  不建议用蓝牙连接
                startActivity(new Intent(this, BluetoothPrintActivity.class));

                break;
        }
    }
}
