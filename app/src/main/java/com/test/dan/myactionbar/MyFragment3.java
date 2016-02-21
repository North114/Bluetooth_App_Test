package com.test.dan.myactionbar;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.dan.selfdefview.InfoBean;
import com.test.dan.sqlite.DBManager;

import java.util.ArrayList;

/**
 * Created by dan on 2015/11/12.
 */
public class MyFragment3 extends Fragment {
    public Handler handler;
    private Button registerButton;
    private Button view_users;
    private EditText userID;
    private EditText userName;
    private View inflatedView;

    private DBManager dbManager;
    /**
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle bundle){
        Log.v("info", "fragement3 onCreateView");
        inflatedView = layoutInflater.inflate(R.layout.view3, viewGroup, false);

        //Obtain DBmaneger of main activity
        dbManager = ((ControlPanelActivity) getActivity()).getDbManager();

        userID = (EditText)inflatedView.findViewById(R.id.frag3_userID);
        userName = (EditText)inflatedView.findViewById(R.id.frag3_userName);
        view_users = (Button)inflatedView.findViewById(R.id.frag3_view_user);
        view_users.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //here we can display information into a listview in the futrue
                try {
                    ArrayList<InfoBean> res = dbManager.query("userInfo");
                    for (InfoBean bean : res) {
                        Log.i("user ID", bean.getId() + "");
                        Log.i("user name", bean.getUserName());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("Exception show user",e.getMessage());
                }
            }
        });

        view_users.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int t = event.getAction();
                if(t == MotionEvent.ACTION_DOWN) {
                    //change color
                    v.setBackgroundColor(0xFFB2FFD9);
                }else if(t == MotionEvent.ACTION_UP){
                    //resume color
                    v.setBackgroundColor(0xff57b6ff);
                }

                return false;
            }
        });

        registerButton = (Button)inflatedView.findViewById(R.id.frag3_regist_user);
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //insert user info to databases
                //�ȿ��Ƿ��������û�������������û���Ϣ������������
                String userId = String.valueOf(userID.getText());
                String username = String.valueOf(userName.getText());

                if(userId == null || username == null || userId.equals("") || username.equals("")){
                    //insert null value
                    Toast.makeText(getActivity(),"Empty Field Value!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ContentValues contentValues = new ContentValues();

                Log.i("user id", userId);
                Log.i("user name", username);

                int res_count = dbManager.rawQuery("SELECT * FROM userInfo WHERE id = " + userId);

                if(res_count <= 0) {
                    //insert operation
                    contentValues.put("id",(int)Integer.valueOf(userId));
                    contentValues.put("userName",username);
                    if(dbManager.insert("userInfo",contentValues)){
                        //insertion success
                        Toast.makeText(getActivity(),"Insert Success!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(),"Insert Failed!", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    //update operation
                    contentValues = new ContentValues();
                    contentValues.put("userName",username);

                    try{
                        dbManager.update("userInfo", contentValues, "id=?", new String[]{userId});
                        Toast.makeText(getActivity(),"Update Success!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        Log.i("update",e.getMessage());
                        Toast.makeText(getActivity(),"Update Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v("info","fragment3 onCreate");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.v("info","fragment3 onPause");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v("info","fragment3 onStart");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v("info","fragemnt3 onResume");
    }

    @Override
    public void onDestroy(){
        Log.v("info", "fragement3 onDestroy");
        super.onDestroy();
    }
}