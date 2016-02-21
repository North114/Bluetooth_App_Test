package com.test.dan.selfdefview;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dan on 2016/2/8.
 */
public class historyDataViewBean {
    private TextView userName,current,voltage;
    private TextView date,time;
    private ImageView date_image,time_image;

    public TextView getUserName() {
        return userName;
    }

    public void setUserName(TextView userName) {
        this.userName = userName;
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    public TextView getCurrent() {
        return current;
    }

    public void setCurrent(TextView current) {
        this.current = current;
    }

    public void setCurrent(String current) {
        this.current.setText(current);
    }

    public TextView getVoltage() {
        return voltage;
    }

    public void setVoltage(TextView voltage) {
        this.voltage = voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage.setText(voltage);
    }

    public TextView getDate() {
        return date;
    }

    public void setDate(TextView date) {
        this.date = date;
    }

    public void setDate(String date) {
        this.date.setText(date);
    }

    public TextView getTime() {
        return time;
    }

    public void setTime(TextView time) {
        this.time = time;
    }

    public void setTime(String time) {
        this.time.setText(time);
    }

    public ImageView getDate_image() {
        return date_image;
    }

    public void setDate_image(ImageView date_image) {
        this.date_image = date_image;
    }

    public ImageView getTime_image() {
        return time_image;
    }

    public void setTime_image(ImageView time_image) {
        this.time_image = time_image;
    }
}
