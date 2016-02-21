package com.test.dan.selfdefview;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dan on 2016/2/14.
 */
public class realTimeDataViewBean {
    private TextView userName,current,voltage;

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
}
