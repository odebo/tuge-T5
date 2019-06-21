package com.tuge.myapp.examples.wifiTranslator.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tuge.myapp.examples.wifiTranslator.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class RecogResultAdapter extends BaseAdapter {

    private LayoutInflater mInflater; //得到一个LayoutInfalter对象用来导入布局
    private List<String> mData;
    public RecogResultAdapter(Context context,List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = mInflater.inflate(R.layout.item_object_rec, null);
            holder = new ViewHolder();
            /*得到各个控件的对象*/
            holder.title = (TextView)view.findViewById(R.id.key);
            holder.text = (TextView) view.findViewById(R.id.value);
            holder.text.setText(mData.get(i));

            view.setTag(holder); //绑定ViewHolder对象
        }
        else {
            holder = (ViewHolder) view.getTag(); //取出ViewHolder对象
        }

        return view;
    }

    /*存放控件 的ViewHolder*/
    public final class ViewHolder {
        public TextView title;
        public TextView text;
    }
}
