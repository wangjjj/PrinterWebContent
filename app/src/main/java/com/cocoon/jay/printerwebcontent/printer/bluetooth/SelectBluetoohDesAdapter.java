package com.cocoon.jay.printerwebcontent.printer.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.cocoon.jay.printerwebcontent.R;

import java.util.List;


public class SelectBluetoohDesAdapter extends RecyclerView.Adapter<SelectBluetoohDesAdapter.ViewHolder>{

    private Context mContext;
    private LayoutInflater mInflater;
    private List<BluetoothDevice> mDatas;
    private int selected = -1;

    public void setmDatas(List<BluetoothDevice> mDatas) {
        this.mDatas = mDatas;
    }

    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(int positon);
    }

    public void setmListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    public SelectBluetoohDesAdapter(Context context, List<BluetoothDevice> datas){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this. mDatas = datas;

    }


    @Override
    public int getItemCount() {
        return (mDatas== null) ? 0 : mDatas.size();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.item_bluetooth_device_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.txt = (TextView) view.findViewById(R.id.txt);
        viewHolder.img = (ImageView) view.findViewById(R.id.img);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        if(!TextUtils.isEmpty(mDatas.get(position).getName())) {
            viewHolder.txt.setText(mDatas.get(position).getName());
        }else{
            viewHolder.txt.setText(mDatas.get(position).getAddress());
        }

        if(selected == position){
            viewHolder.img.setImageResource(R.mipmap.radio_on);
        }else{
            viewHolder.img.setImageResource(R.mipmap.radio);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selected = position;
                notifyDataSetChanged();

                if(mListener != null){
                    mListener.onItemClick(position);
                }
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View view){
            super(view);
        }
        TextView txt;
        ImageView img;

    }

}
