package com.cocoon.jay.printerwebcontent.printer.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.cocoon.jay.printerwebcontent.R;

import java.util.List;


public class SelectBluetoohDeviceDialog extends Dialog
{


	/** 布局文件 **/
	int layoutRes;
	/** 上下文对象 **/
	Context context;

	private RecyclerView rv_list;
	private List<BluetoothDevice> printerDevices ;
	private BluetoothDevice devic;
	private SelectBluetoohDesAdapter adapter;


	public interface OnButtonClickListener {
		void onGoSetButtonClick();
		void onCertainButtonClick(BluetoothDevice devic);
	}


	private OnButtonClickListener mOnButtonClickListener;


	public SelectBluetoohDeviceDialog(Context context, int theme, int resLayout, List<BluetoothDevice> printerDevices) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
		this.printerDevices = printerDevices;
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

		rv_list = (RecyclerView)findViewById(R.id.rv_list);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		rv_list.setLayoutManager(linearLayoutManager);

		adapter = new SelectBluetoohDesAdapter(context, printerDevices);
		adapter.setmListener(new SelectBluetoohDesAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				if(printerDevices.size() > 0){
					devic = printerDevices.get(position);
				}

			}
		});
		rv_list.setAdapter(adapter);



		// 为按钮绑定点击事件监听器
		if(confirm != null) {
			confirm.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(mOnButtonClickListener != null){
						mOnButtonClickListener.onCertainButtonClick(devic);
					}
				}

			});
		}
		if(cancel != null) {
			cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(mOnButtonClickListener != null){
						mOnButtonClickListener.onGoSetButtonClick();
					}
				}

			});
		}

	}



	public void setPrinterDevices(List<BluetoothDevice> devices) {
		this.printerDevices = devices;
		if(adapter != null) {
			adapter.setmDatas(printerDevices);
			adapter.notifyDataSetChanged();
		}
	}




}


