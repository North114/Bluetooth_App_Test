package com.test.dan.myactionbar;

import android.content.ContentValues;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.dan.bluetooth.BluetoothState;
import com.test.dan.selfdefview.InfoBean;
import com.test.dan.selfdefview.realtimeDateListViewAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dan on 2015/11/12.
 */
public class MyFragment2 extends Fragment {
    private View inflatedView;
    private ControlPanelActivity mactivity;
    private Button button;
    private ListView listView;
    private realtimeDateListViewAdapter listViewAdapter;
    private ArrayList<InfoBean> listItems;

    private final static int PACKAGE_LENGTH = 7;
    private final String TAG = "MyFragment2";
    private final byte StartByte = (byte)0xBB;
    private final byte EndByte = 0x70;
    private byte[] command = {StartByte,0x02,0x02,EndByte};

    private int flag_sendCommand = 0;

    public final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case BluetoothState.MESSAGE_READ:{
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf);
                    //log the received message
                    Log.e("readdata-fragment2",readMessage);

                    if(readBuf != null && readBuf.length > 0 && flag_sendCommand == 1) {
                        flag_sendCommand = 0;
//                        if(mDataReceivedListener != null)
//                            mDataReceivedListener.onDataReceived(readBuf, readMessage);

                        //Drop old data
                        mactivity.getDbManager().delete("realtimeData");

                        Log.i(TAG,"readBuf size is : " + readBuf.length);

                        for(int i = 0;i <= readBuf.length - PACKAGE_LENGTH;){
                            Log.i(TAG,readBuf[i]/16 + "" + readBuf[i]%16 + "");
                            if(readBuf[i] == StartByte && readBuf[i + PACKAGE_LENGTH - 1] == EndByte){
                                ContentValues cv = parseContentValue(readBuf,i);
                                mactivity.getDbManager().insert("realtimeData",cv);
                                i += PACKAGE_LENGTH;
                                Log.i(TAG,"valid content value!");
                            }else ++i;
                        }
                        //show data in listview
                        showData();
/*                        if (readBuf.length == PACKAGE_LENGTH && 1 == flag_sendCommand){
                            *//*
                            ** defualt data arrange:
                            ** Id byte + current MSB + current LSB + voltage MSB + voltage LSB + 0x0A + 0x0D
                             *//*
                            int temp = 0;
                            int t;

                            t = readBuf[1] > 0 ? readBuf[1] : 256 + readBuf[1];
                            temp = t * 256;
                            t = readBuf[2] > 0 ? readBuf[2] : 256 + readBuf[2];
                            temp = (temp + t) / 100;

                            current.setText(temp + " mA");

                            t = readBuf[3] > 0 ? readBuf[3] : 256 + readBuf[3];
                            temp = t * 256;
                            t = readBuf[4] > 0 ? readBuf[4] : 256 + readBuf[4];
                            temp = (temp + t) / 100;
                            voltage.setText(temp + " V.AC");
                            flag_sendCommand = 0;
                        } else {
                            Log.e("read data error","package length invalid: " + readBuf.length);
                        }*/
                    }
                }
                default:{

                }
            }
        }
    };
    /**
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle bundle){
        inflatedView = layoutInflater.inflate(R.layout.view2,viewGroup,false);
        Log.v("info", "fragement2 onCreateView");

        mactivity = (ControlPanelActivity) getActivity();

        button = (Button) inflatedView.findViewById(R.id.frag2_get_realtime_data);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "flag_sendCommand is " + flag_sendCommand);
                if (mactivity.getBluetoothService().getState() == BluetoothState.STATE_CONNECTED) {
                    if (0 == flag_sendCommand) {
                        flag_sendCommand = 1;
                        //send data to the connected bluetooth server when button touched
                        mactivity.write_To_Bluetooth(command);
                    }
                    //showData();
                } else {
                    Toast.makeText(getActivity(), "蓝牙尚未连接完成", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView = (ListView) inflatedView.findViewById(R.id.frag2_data_showing);
        //创建arraylist<bean>
        listItems = new ArrayList<>();
        listViewAdapter = new realtimeDateListViewAdapter(getActivity(), listItems);
        listView.setAdapter(listViewAdapter);

        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v("info", "fragment2 onCreate");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.v("info","fragment2 onPause");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v("info","fragment2 onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("info","fragemnt2 onResume");
    }

    @Override
    public void onDestroy(){
        Log.v("info", "fragement2 onDestroy");
        super.onDestroy();
    }

    /*
    ** Parse ContentValue from byte array
    ** i represent start index of byte array
     */
    public ContentValues parseContentValue(byte[] readBuf,int i) {
        ContentValues cv = new ContentValues();

        cv.put("id",(int)(readBuf[i + 1] < 0 ? 256 + readBuf[i + 1] : readBuf[i + 1]));//convert byte to int

        int t = ((readBuf[i + 2] < 0 ? 256 + readBuf[i + 2] : readBuf[i + 2])* 256
                + (readBuf[i + 3] < 0 ? 256 + readBuf[i + 3] : readBuf[i + 3])) / 100;
        cv.put("current",t + "");

        t = ((readBuf[i + 4] < 0 ? 256 + readBuf[i + 4] : readBuf[i + 4])* 256
                + (readBuf[i + 5] < 0 ? 256 + readBuf[i + 5] : readBuf[i + 5])) / 100;
        cv.put("voltage",t + "");

        return cv;
    }

    public void showData() {
        ArrayList<InfoBean> bean = mactivity.getDbManager().query("realtimeData");

/*        InfoBean infoBean = new InfoBean();
        infoBean.setUserName("伍丹");
        infoBean.setCurrent("34mA");
        infoBean.setVoltage("234V");
        infoBean.setDate("2013-12-22");
        infoBean.setTime("23:12:11");
        bean.add(infoBean);*/
        Log.i(TAG, "show data function called");

        listViewAdapter = new realtimeDateListViewAdapter(getActivity(),bean);
        listView.setAdapter(listViewAdapter);
    }
}