package com.test.dan.selfdefview;

/**
 * Created by dan on 2016/2/6.
 * 用于存储用户信息的bean
 */
public class InfoBean {
    private String userName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String current,voltage;
    private String date,time;

    public InfoBean(){

    }

    public InfoBean(String a, String b, String c, String d, String e){
        userName = a;
        current = b;
        voltage = c;
        date = d;
        time = e;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
