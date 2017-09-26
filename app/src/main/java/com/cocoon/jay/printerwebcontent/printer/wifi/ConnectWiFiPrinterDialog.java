package com.cocoon.jay.printerwebcontent.printer.wifi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cocoon.jay.printerwebcontent.R;


public class ConnectWiFiPrinterDialog extends Dialog
{


	/** 布局文件 **/
	private int layoutRes;
	/** 上下文对象 **/
	private Context context;

	private EditText connect_ip;
	private EditText connect_port;
	private int port;

	public interface OnButtonClickListener {
		void onPrinterClick();
		void onLinkClick(String ip, int port);
	}


	private OnButtonClickListener mOnButtonClickListener;


	public ConnectWiFiPrinterDialog(Context context, int theme, int resLayout) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
	}


	public void setmOnButtonClickListener(OnButtonClickListener mOnButtonClickListener) {
		this.mOnButtonClickListener = mOnButtonClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 指定布局
		this.setContentView(layoutRes);


		// 根据id在布局中找到控件对象
		TextView confirm =  (TextView) findViewById(R.id.confirm);
		TextView cancel =  (TextView) findViewById(R.id.cancel);
		connect_ip =  (EditText) findViewById(R.id.connect_ip);
		connect_port =  (EditText) findViewById(R.id.connect_port);
		connect_ip.setSelection(connect_ip.getText().length());

		//连接打印机
		if(cancel != null) {
			cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					if ("".equals(connect_ip.getText().toString())) {
						Toast.makeText(context, "请先输入ip地址", Toast.LENGTH_SHORT).show();
						return;
					}
					if ("".equals(connect_port.getText().toString())) {
						Toast.makeText(context, "请先输入端口号", Toast.LENGTH_SHORT).show();
						return;
					}
					try {
						port = Integer.parseInt(connect_port.getText().toString());
					}catch (Exception e){

					}
					Toast.makeText(context, "正在连接,请稍等...", Toast.LENGTH_SHORT).show();
					if(mOnButtonClickListener != null){
						mOnButtonClickListener.onLinkClick(connect_ip.getText().toString(), port);
					}
				}

			});
		}

		// 打印
		if(confirm != null) {
			confirm.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(mOnButtonClickListener != null){
						mOnButtonClickListener.onPrinterClick();
					}
				}

			});
		}


	}






}


