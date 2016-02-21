package com.test.dan.myactionbar;

/**
 * Created by dan on 2015/11/15.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    public final static String EXTRA_MESSAGE = "com.test.dan.MESSAGE";
    private BluetoothAdapter bt_adapter;
    private ArrayAdapter<String> bt_arrayAdapter;
    private ListView mylistview;
    private ArrayList<BluetoothDevice> pairedDevices;
    private Button bt_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("info", "onCreate issue!");

        setContentView(R.layout.activity_main);

        bt_scan = (Button) findViewById(R.id.bt_scan);

        bt_adapter = BluetoothAdapter.getDefaultAdapter();
        bt_arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mylistview = (ListView) findViewById(R.id.listView1);

        pairedDevices = new ArrayList<BluetoothDevice>();

        //step 3:Scan Bluetooth Device
        bt_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bt_adapter.isDiscovering()){
                    bt_adapter.cancelDiscovery();
                    Toast.makeText(getApplicationContext(),"stop scan",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "start scan", Toast.LENGTH_SHORT).show();
                    bt_arrayAdapter.clear();
                    //asynchronous(return immediately),duration is about 12 seconds
                    bt_adapter.startDiscovery();

                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    //filter.addAction(BluetoothDevice.ACTION_FOUND);

                    registerReceiver(bReceiver, filter);

                    //scanDevice(v);

                    mylistview.setAdapter(bt_arrayAdapter);
                    //setContentView(mylistview);
                }
            }
        });

        //step 1: Check Availability of bluetooth
        if (bt_adapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("没有可用蓝牙设备")
                    .setPositiveButton("退出",new DialogInterface.OnClickListener(){

                        public void onClick(DialogInterface dialog,int which){
                            System.exit(0);
                        }

                    })
                    .setIcon(android.R.mipmap.sym_def_app_icon)
                    .show();
        }

        //step 2: �������豸
        if(!bt_adapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }

        //mylistview.setAdapter(bt_arrayAdapter);

        //step 4: ����ListView�������豸�����ʱҪ��������---���Ӹ��豸
        //setContentView(mylistview);
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                //Toast.makeText(MainActivity.this, bt_arrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                //step 5: �����豸����ת����һ������

                //connectThread = new ConnectThread(pairedDevices.get(position));
                //connectThread.start();
                Intent intent = new Intent(MainActivity.this,ControlPanelActivity.class);
                intent.putExtra("deviceAddress",pairedDevices.get(position).getAddress());
                startActivity(intent);

            }
        });

    }


    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            //Log.i("info","Yeah , I am now in onReceive function!");

            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter

                //avoid duplicated bluetooth device(low efficient , need to be improved)
                String temp = device.getName() + "\n" + device.getAddress();
                for (int i = 0;i < bt_arrayAdapter.getCount();++i) {
                    if(bt_arrayAdapter.getItem(i).equals(temp)) return;
                }
                //add new bluetooth device to ArrayAdapter
                bt_arrayAdapter.add(temp);
                pairedDevices.add(device);
                //notify UI thread to refresh UI with new arrayAdapter
                bt_arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        Log.i("info","onStart issue!");
    }

    @Override
    protected  void onRestart(){
        super.onRestart();
        Log.i("info", "onReStart issue!");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("info", "onResume issue!");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i("info", "onPause issue!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("info", "onStop issue!");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (bt_adapter != null){
            if (bt_adapter.isEnabled()){
                bt_adapter.disable();
            }
            //stop discovering devices
            bt_adapter.cancelDiscovery();
        }
        //un-register Receiver
        this.unregisterReceiver(bReceiver);

        Log.i("info", "onDestroy issue!");
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
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

        return super.onOptionsItemSelected(item);
    }


    public void sendMessage(View view){
        //to do
//
//        Intent intent = new Intent(this,DisplayMessageActivity.class);
//
//        EditText editText = (EditText) findViewById(R.id.edit_message);
//
//        String message = editText.getText().toString();
//
//        intent.putExtra(EXTRA_MESSAGE,message);
//
//        startActivity(intent);

        //return;
    }

    public void scanDevice(View view){
        //pairedDevices = bt_adapter.getBondedDevices();
        //Log.i("info",pairedDevices.size()+"\n");

        //for (BluetoothDevice blue : pairedDevices){
        //    bt_arrayAdapter.add(blue.getName() + '\n' + blue.getAddress());
        //}

        //Toast.makeText(getApplicationContext(),"Show Paired Device",Toast.LENGTH_SHORT).show();
    }
}