package com.tuge.myapp.examples.wifiTranslator.adapter;

import android.util.Log;

import com.tuge.myapp.examples.wifiTranslator.view.WheelView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * The simple Array wheel adapter
 */
public class ArrayWheelAdapter implements WheelView.WheelAdapter {
	private static final String TAG = "ArrayWheelAdapter";

	// items
	private List items;

	public ArrayWheelAdapter(String [] items) {
		if (items == null || items.length <= 0) {
			Log.e(TAG, "ArrayWheelAdapter: 数据无效");
		}
        List<String> list = new ArrayList<>();
		for (String str : items) {
			list.add(str);
		}
		this.items = list;
	}

	/**
	 * Constructor
	 * @param items the items
	 */
	public ArrayWheelAdapter(List items) {
		this.items = items;
	}
	
	@Override
	public Object getItem(int index) {
		if (index >= 0 && index < items.size()) {
			return items.get(index);
		}
		return "";
	}

	@Override
	public int getItemsCount() {
		return items.size();
	}

	@Override
	public int indexOf(Object o){
		return items.indexOf(o);
	}

}
