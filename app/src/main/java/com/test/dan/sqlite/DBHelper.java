package com.test.dan.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dan on 2016/2/11.
 */
public class DBHelper extends SQLiteOpenHelper {
    private String DATABASE_NAME;
    private String[] db_tables;
    private String[] table_param;
    private static final int DATABASE_VERSION = 4;

    public DBHelper(Context context,String db_name,String[] tables,String[] param){
        super(context, db_name, null, DATABASE_VERSION);
        Log.i("dbhelper", "constructor");
        this.DATABASE_NAME = db_name;
        this.db_tables = tables;
        this.table_param = param;
    }

    /*
    ** Called when database created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        for(int i = 0;i < db_tables.length;++i) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + db_tables[i] +"(" + table_param[i] + ")");
        }
    }

    /*
    * Called when DATABASE_VERSION changed . not implement yet
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = 0;i < db_tables.length;++i) {
            db.execSQL("DROP TABLE IF EXISTS " + db_tables[i]);
        }

        for(int i = 0;i < db_tables.length;++i) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + db_tables[i] +"(" + table_param[i] + ")");
        }
    }
}
