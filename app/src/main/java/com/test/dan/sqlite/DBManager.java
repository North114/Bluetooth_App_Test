package com.test.dan.sqlite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.test.dan.selfdefview.InfoBean;

import java.util.ArrayList;

/**
 * Created by dan on 2016/2/11.
 */
public class DBManager {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    //for singleton mode
    private static DBManager dbmanager = null;

    private final String TAG = "DBManager";

    /*
    ** Singleton Mode(getter)
     */
    public static DBManager getDBmanager(Context context){

        if(dbmanager == null){
            dbmanager = new DBManager(context,"gprs.db",
                    new String[]{"historyData","realtimeData","userInfo"},
                    new String[]{"id INTEGER,current VARCHAR,voltage VARCHAR,date VARCHAR,time VARCHAR",
                            "id INTEGER PRIMARY KEY,current VARCHAR,voltage VARCHAR",
                            "id INTEGER PRIMARY KEY,userName VARCHAR"});
        }

        return dbmanager;
    }
    /*
    ** Constructor(private)
     */
    private DBManager(Context context,String db_name,String[] tables,String[] params){
        dbHelper = new DBHelper(context,db_name,tables,params);
        //get Writable Database
        db = dbHelper.getWritableDatabase();
    }

    /*
    ** Insertion
     */
    public boolean insert(String table,ContentValues contentValues) {
        long temp = 0;
        try {
            temp = db.insert(table, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,e.getMessage());
        } finally {

        }

        if(temp == -1)return false;
        else return true;
    }

    /*
    ** Deletion
     */
    public int delete(String table){
        //db.execSQL("DELETE FROM " + table);
        //or as follow(delete rows in a table)
        return db.delete(table, null, null);

    }

    /*
    ** Updating
    *  @param{ContentValues}value to update
    *  @param{column}column match "values" condition
    *  @param{values}as above
     */
    public int update(String table,ContentValues contentValues,String column,String[] values){
        return db.update(table, contentValues, column, values);
    }

    /*
    ** Query
    * @param{table} is table name
    * @param{query} is query columns info
    * @param{param} is query condition about columns
    */
    public ArrayList<InfoBean> query(String table){
        ArrayList<InfoBean> data = new ArrayList<>();
        String temp;
        int index;
        String query_statement;

        if(table.equals("historyData")) {
            //query_statement = "SELECT userName,current,voltage,date,time FROM " + table + " LEFT OUTER JOIN userInfo ON userInfo.id = " + table + ".id";
            query_statement = "SELECT userInfo.userName,historyData.current,historyData.voltage,historyData.date,historyData.time FROM " + table + " LEFT OUTER JOIN userInfo ON userInfo.id = " + table + ".id";
        } else if(table.equals("realtimeData")){
            query_statement = "SELECT userInfo.userName,realtimeData.current,realtimeData.voltage FROM " + table + " LEFT OUTER JOIN userInfo ON userInfo.id = " + table + ".id";
        } else if(table.equals("userInfo")){
            query_statement = "SELECT * FROM " + table;
        } else {
            Log.i("SQLite query","Invalid Table Name");
            return data;
        }

        db.beginTransaction();

        Cursor cursor = db.rawQuery(query_statement, null);

        while(cursor.moveToNext()) {
            InfoBean tempBean = new InfoBean();
            index = cursor.getColumnIndex("userName");
            if(index >= 0 && cursor.getString(index) != null){
                tempBean.setUserName(cursor.getString(index));
//                Log.i("userName", cursor.getString(index));
            } else {
                tempBean.setUserName("未知");
            }

            index = cursor.getColumnIndex("current");
            if(index >= 0) {
                tempBean.setCurrent(cursor.getString(index));
            }

            index = cursor.getColumnIndex("voltage");
            if(index >= 0) {
                tempBean.setVoltage(cursor.getString(index));
            }

            index = cursor.getColumnIndex("date");
            if (index >= 0){
                tempBean.setDate(cursor.getString(index));
            }

            index = cursor.getColumnIndex("time");
            if (index >= 0) {
                tempBean.setTime(cursor.getString(index));
            }

            index = cursor.getColumnIndex("id");
            if(index >= 0){
                tempBean.setId(Integer.valueOf(cursor.getString(index)));
                Log.i("userID", cursor.getString(index));
            }

            data.add(tempBean);
        }

        cursor.close();
        db.endTransaction();

        return data;
    }
    /*
    ** raw query database
    */
    public int rawQuery(String statement){
        Cursor cursor;

        try{
            cursor = db.rawQuery(statement,null);
            return cursor.getCount();
        }catch (Exception e){
            e.printStackTrace();
            Log.i("exception",e.getMessage());
        }

        return 0;
    }
    /*
    ** Close
     */
    public void close(){
        db.close();
    }

}
