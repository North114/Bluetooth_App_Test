package com.test.dan.selfdefview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.dan.myactionbar.R;

import java.util.ArrayList;

/**
 * Created by dan on 2016/2/6.
 */
public class historyDataListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<InfoBean> listItem;
    private LayoutInflater listContainer;

    private final String TAG = "historyDataList Adapter";

    //���캯��
    public historyDataListViewAdapter(Context context, ArrayList<InfoBean> infoBeans) {
        this.context = context;
        this.listContainer = LayoutInflater.from(context);

        this.listItem = infoBeans;
    }

    //������ʾ������
    public void setListItem(ArrayList<InfoBean> li){
        this.listItem = li;
    }

    //ʵ�ֽӿڶ���ķ���
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
        historyDataViewBean listItemView = null;

        Log.w(TAG, "Start getView!");

        if (convertView == null) {
            listItemView = new historyDataViewBean();

            convertView = listContainer.inflate(R.layout.card_view, null);

            listItemView.setUserName((TextView) convertView.findViewById(R.id.user_name_text));
            listItemView.setCurrent((TextView) convertView.findViewById(R.id.current_text));
            listItemView.setVoltage((TextView) convertView.findViewById(R.id.voltage_text));
            listItemView.setDate((TextView) convertView.findViewById(R.id.date_text));
            listItemView.setDate_image((ImageView) convertView.findViewById(R.id.date_image));
            listItemView.setTime((TextView) convertView.findViewById(R.id.time_text));
            listItemView.setTime_image((ImageView) convertView.findViewById(R.id.time_image));

            convertView.setTag(listItemView);
        } else {
            listItemView = (historyDataViewBean) convertView.getTag();
        }
        //��������䵽TextView��
        listItemView.setUserName(listItem.get(position).getUserName());
        listItemView.setCurrent(listItem.get(position).getCurrent() + "mA");
        listItemView.setVoltage(listItem.get(position).getVoltage() + "V");
        listItemView.setDate(listItem.get(position).getDate());
        listItemView.setTime(listItem.get(position).getTime());

        return convertView;
    }

}
