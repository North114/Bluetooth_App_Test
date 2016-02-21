package com.test.dan.myactionbar;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.test.dan.bluetooth.BluetoothState;
import com.test.dan.selfdefview.InfoBean;

import java.util.ArrayList;


/**
 * Created by dan on 2015/11/12.
 */
public class MyFragment1 extends Fragment {
    private Button button;
    private TextView textView;
    private View inflatedView;
    private ControlPanelActivity mactivity;

    private final String TAG = "fragment1";
    private byte[] command = {(byte)0xBB,0x02,0x01,0x70};
    private final int PACKAGE_LENGTH = 13;
    private final byte StartByte = (byte)0xBB;
    private final byte EndByte = 0x70;
    private int flag_sendCommand = 0;

    public final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case BluetoothState.MESSAGE_READ:{
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf);
                    //log the received message
                    Log.e("readdata-fragment1", readMessage);
                    if(readBuf != null && readBuf.length > 0 && flag_sendCommand == 1) {
                        flag_sendCommand = 0;
//                        if(mDataReceivedListener != null)
//                            mDataReceivedListener.onDataReceived(readBuf, readMessage);
                        Log.i(TAG,"message length is " + readBuf.length);
                        //insert data into database , then jump to data showing activity
                        for(int i = 0;i <= readBuf.length - PACKAGE_LENGTH;){
                            if(readBuf[i] == StartByte && readBuf[i + PACKAGE_LENGTH - 1] == EndByte) {
                                //valid package found
                                Log.i(TAG,"valid content value!");
                                ContentValues cv = parseContentValue(readBuf,i);
                                mactivity.getDbManager().insert("historyData", cv);
                                i += PACKAGE_LENGTH;
                            }else ++i;

                            int t = readBuf[i] > 0 ? readBuf[i] : readBuf[i] + 256;
                            Log.i(TAG,t / 16 + "" + t % 16 + "");
                        }
                        //start data showing activity
                        showData();
                    }
                }
                default: {

                }
            }
        }
    };

    /**
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle bundle){
        inflatedView = layoutInflater.inflate(R.layout.view1,viewGroup,false);
        Log.v("info", "fragement1 onCreateView");

        mactivity = (ControlPanelActivity) getActivity();
        //Log.v("message",mactivity.bar());

        button = (Button) inflatedView.findViewById(R.id.query_history_data);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"flag_sendCommand is " + flag_sendCommand);
                //set flag
                if(mactivity.getBluetoothService().getState() == BluetoothState.STATE_CONNECTED) {
                    if (flag_sendCommand == 0) {
                        flag_sendCommand = 1;
                        //send data to the connected bluetooth server when button touched
                        try {
                            mactivity.write_To_Bluetooth(command);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, e.getMessage());
                        }
                        //showData();
                    }
                } else {
                    Toast.makeText(getActivity(),"蓝牙尚未连接完成", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v("info", "fragment1 onCreate");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("info","fragment1 onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("info","fragment1 onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("info","fragemnt1 onResume");
    }

    @Override
    public void onDestroy() {
        Log.v("info", "fragement1 onDestroy");
        super.onDestroy();
    }

    public void foo() {
        Log.v("info", "now presenting fragment1");
    }

    /*
    ** Parse ContentValue from byte array
    ** i represent start index of byte array
     */
    public ContentValues parseContentValue(byte[] readBuf,int i) {
        ContentValues cv = new ContentValues();

        Log.i(TAG,"put a content value");

        cv.put("id",(int)(readBuf[i + 1] < 0 ? 256 + readBuf[i + 1] : readBuf[i + 1]));//convert byte to int

        int t = ((readBuf[i + 2] < 0 ? 256 + readBuf[i + 2] : readBuf[i + 2])* 256
                + (readBuf[i + 3] < 0 ? 256 + readBuf[i + 3] : readBuf[i + 3])) / 100;
        cv.put("current",t + "");

        t = ((readBuf[i + 4] < 0 ? 256 + readBuf[i + 4] : readBuf[i + 4])* 256
                + (readBuf[i + 5] < 0 ? 256 + readBuf[i + 5] : readBuf[i + 5])) / 100;
        cv.put("voltage",t + "");

        String s = (readBuf[i + 6] / 16) + "" + (readBuf[i + 6] % 16) + "-" +
                (readBuf[i + 7] / 16) + "" + (readBuf[i + 7] % 16) + "-" +
                (readBuf[i + 8] / 16) + "" + (readBuf[i + 8] % 16);
        cv.put("date",s);

        s = (readBuf[i + 9] / 16) + "" + (readBuf[i + 9] % 16) + ":" +
                (readBuf[i + 10] / 16) + "" + (readBuf[i + 10] % 16) + ":" +
                (readBuf[i + 11] / 16) + "" + (readBuf[i + 11] % 16);
        cv.put("time", s);

        return cv;
    }
    /*
    ** Showing data in a list view
     */
    public void showData() {
        //get data
        //ArrayList<InfoBean> data = mactivity.getDbManager().query("historyData");
        //start activity
        try {
            Intent intent = new Intent(mactivity, HistoryDataShowingActivity.class);
            intent.putExtra("table", "historyData");
            startActivity(intent);
        }catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,e.getMessage());
        }
    }
}
