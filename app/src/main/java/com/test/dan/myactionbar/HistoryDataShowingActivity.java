package com.test.dan.myactionbar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.test.dan.selfdefview.InfoBean;
import com.test.dan.selfdefview.historyDataListViewAdapter;
import com.test.dan.sqlite.DBManager;

import java.util.ArrayList;

/**
 * Created by dan on 2016/2/14.
 */
public class HistoryDataShowingActivity extends ActionBarActivity {
    private ListView listView;
    private historyDataListViewAdapter historyDataListViewAdapter;
    private ArrayList<InfoBean> listItems;
    private DBManager dbManager;

    private final String TAG = "HistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view1_data_showing);

        listView = (ListView)findViewById(R.id.data_showing_listview);
        dbManager = DBManager.getDBmanager(this);
        //创建bean
        //创建ArrayAdapter
        //����ListView��ArrayAdapter

        //事后修改adapter的arraylist<bean>
        try {
            listItems = dbManager.query("historyData");
            //listItems.add(infoBean);
            historyDataListViewAdapter = new historyDataListViewAdapter(this, listItems);
            listView.setAdapter(historyDataListViewAdapter);
        }catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //save activity content when switch between two activity
    @Override
    protected  void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    //��ֹ������Ļ�л�
    public void onConfigurationChanged(Configuration newConfig) {
        try {
            super.onConfigurationChanged(newConfig);
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.v("info", "onConfigurationChanged_ORIENTATION_LANDSCAPE");
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.v("info", "onConfigurationChanged_ORIENTATION_PORTRAIT");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i(TAG,ex.getMessage());
        }
    }
}
