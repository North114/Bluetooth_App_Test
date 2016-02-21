package com.test.dan.selfdefview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.dan.myactionbar.R;

import java.util.ArrayList;

/**
 * Created by dan on 2016/2/14.
 */
public class realtimeDateListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<InfoBean> listItem;
    private LayoutInflater listContainer;

    public realtimeDateListViewAdapter(Context context,ArrayList<InfoBean> bean){
        this.context = context;
        this.listItem = bean;
        this.listContainer = LayoutInflater.from(context);
    }


    public void setListItem(ArrayList<InfoBean> listItem) {
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final int selectID = position;
        realTimeDataViewBean listItemView = null;

        if (convertView == null) {
            listItemView = new realTimeDataViewBean();

            convertView = listContainer.inflate(R.layout.realtime_card_view, null);

            listItemView.setUserName((TextView) convertView.findViewById(R.id.realtime_user_name_text));
            listItemView.setCurrent((TextView) convertView.findViewById(R.id.realtime_current_text));
            listItemView.setVoltage((TextView) convertView.findViewById(R.id.realtime_voltage_text));

            convertView.setTag(listItemView);
        } else {
            listItemView = (realTimeDataViewBean) convertView.getTag();
        }

        //将内容填充到TextView中
        listItemView.setUserName(listItem.get(position).getUserName());
        listItemView.setCurrent(listItem.get(position).getCurrent() + "mA");
        listItemView.setVoltage(listItem.get(position).getVoltage() + "V");

        return convertView;
    }
}
