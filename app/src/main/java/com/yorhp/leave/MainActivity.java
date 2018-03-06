package com.yorhp.leave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements MyService.CallBack {

    Button btn_start;
    EditText edt_ip;
    EditText edt_section;
    String ip,section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        MyService.setCallBack(this);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = edt_ip.getText().toString().trim();
                section = edt_section.getText().toString().trim();
                if (!ip.equals("") && !section.equals("")) {
                    startService(ip, section);
                }
            }
        });
    }

    private void startService(String ip, String section) {
        Connect.setPort(ip);
        String msg = Integer.toHexString(Integer.parseInt(section));
        if (msg.length() == 1) {
            msg = "0" + msg;
        }
        MyService.SECTION = msg;
        btn_start.setText("启动中...");
        Intent i = new Intent(MainActivity.this, MyService.class);
        startService(i);
    }


    private void initView() {
        SharedPreferences date=MainActivity.this.getSharedPreferences("data",MODE_PRIVATE);
        ip=date.getString("ip","192.168.1.111");
        section=date.getString("section","1");
        btn_start = (Button) findViewById(R.id.btn_start);
        edt_ip = (EditText) findViewById(R.id.edt_ip);
        edt_section = (EditText) findViewById(R.id.edt_section);
        edt_ip.setText(ip);
        edt_section.setText(section);
        startService(ip,section);
    }



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                btn_start.setText("启动成功");
                SharedPreferences date=MainActivity.this.getSharedPreferences("data",MODE_PRIVATE);
                SharedPreferences.Editor editor=date.edit();
                editor.putString("ip",ip);
                editor.putString("section",section);
                editor.commit();
            } else {
                btn_start.setText("启动中...");
            }
        }
    };

    @Override
    public void callBack(boolean ok) {
        if (ok)
            handler.sendEmptyMessage(0);
        else
            handler.sendEmptyMessage(1);
    }


}
