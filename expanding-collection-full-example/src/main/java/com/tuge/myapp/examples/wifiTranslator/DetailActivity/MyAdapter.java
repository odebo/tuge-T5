package com.tuge.myapp.examples.wifiTranslator.DetailActivity;

import android.content.Context;
import android.graphics.Point;
import android.net.sip.SipSession;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.library.banner.BannerLayout;
import com.tuge.myapp.examples.wifiTranslator.R;
import com.tuge.myapp.examples.wifiTranslator.view.CameraSurfaceView;

/**
 * Author: jpeng
 * Date: 17-9-12 下午6:50
 * E-mail:peng8350@gmail.com
 * Description:
 */
public class MyAdapter extends BaseAdapter
{
    private Context context;
    private ListBean[] mDatas;
    private onListener listener;

    public MyAdapter(Context context, ListBean[] mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_menu,null);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_text);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_image);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(mDatas[position].getTitle());
        holder.iv.setImageResource(mDatas[position].getResource());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null) {
                    listener.OnListener(position);
                }

//                Toast.makeText(context,mDatas[position].getTitle()+"被点击!!!",Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
    public void setOnItemClickListener(MyAdapter.onListener listener) {

        this.listener = listener;

    }
    public interface  onListener{
        void OnListener(int position);
    }
    static class ViewHolder{
        TextView tv;
        ImageView iv;
    }
}
