package com.test.dan.myactionbar;

//import android.app.ActionBar;
//import android.app.Fragment;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.test.dan.bluetooth.BluetoothService;
import com.test.dan.sqlite.DBManager;

import java.util.ArrayList;
import java.util.List;


public class ControlPanelActivity extends ActionBarActivity implements ActionBar.TabListener{
    private List<Fragment> fragmentList;
    private List<String> titleList;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private Toolbar toolBar;

    public String test = "hello dan";
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothService bluetoothService;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);

        Log.v("info", "activity control panel onCreate");

        //get the message from the intent
        Intent intent = getIntent();
        String remoteAddr = intent.getStringExtra("deviceAddress");

        Toast.makeText(ControlPanelActivity.this,remoteAddr,Toast.LENGTH_SHORT).show();

        //connect to the device
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(remoteAddr);
        MyFragment1 myFragment1 = new MyFragment1();
        //when activity loaded , we use handler of fragment 1
        bluetoothService = new BluetoothService(bluetoothDevice,myFragment1.handler);
        bluetoothService.connect();

        // Set up the action bar.
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(myFragment1);
        fragmentList.add(new MyFragment2());
        fragmentList.add(new MyFragment3());

        titleList = new ArrayList<String>();

        titleList.add(this.getString(R.string.section1_name));
        titleList.add(this.getString(R.string.section2_name));
        titleList.add(this.getString(R.string.section3_name));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(
                getSupportFragmentManager(),fragmentList,titleList
        );

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(myFragmentPagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            //����ÿ���л�fragmentʱͬʱ�л�tab�ı���
            @Override
            public void onPageSelected(int position) {

                actionBar.setSelectedNavigationItem(position);
                Log.v("info", "entering " + (position + 1));

                if(position == 0) {
                    MyFragment1 mf1 = (MyFragment1) fragmentList.get(position);
                    //MainActivity.this.test = mf1.test;
                    //make current activity's handler to be the fragment's handler
                    //�л�feagment��ͬʱ�л����������̵߳�handler,ȷ��handler����ǰfragment�õ�
                    bluetoothService.setHandler(mf1.handler);

                    Toast.makeText(ControlPanelActivity.this,"fragment1 handler",Toast.LENGTH_SHORT).show();
                }else if(position == 1){
                    MyFragment2 fm2 = (MyFragment2) fragmentList.get(position);
                    bluetoothService.setHandler(fm2.handler);

                    Toast.makeText(ControlPanelActivity.this,"fragment2 handler",Toast.LENGTH_SHORT).show();
                }else if(position == 2){
                    //MyFragment3 fm3 = (MyFragment3) fragmentList.get(position);
                    //MainActivity.this.handler = fm3.handler;
                    Toast.makeText(ControlPanelActivity.this,"fragment3 handler",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ControlPanelActivity.this,"error in switch fragments",Toast.LENGTH_SHORT).show();
                }

            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < myFragmentPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(myFragmentPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
        //actionBar.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_launcher));

        //SQLite manager
        dbManager = DBManager.getDBmanager(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.menu_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart(){
        super.onStart();
        Log.v("info","activity control panel onStart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.v("info","activity control panel onResume");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.v("info","activity control panel onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.v("info","activity control panel onStop");
    }

    @Override
    protected void onDestroy(){
        this.bluetoothService.stop();
        this.dbManager.close();
        super.onDestroy();
        Log.v("info", "activity control panel onDestroy");
    }

    // 3 function for tab listener
    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    //meathod to write data to bluetooth
    public void write_To_Bluetooth(byte[] buffer){

        bluetoothService.write(buffer);
    }

    public DBManager getDbManager(){
        return this.dbManager;
    }

    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }
}
