package com.yorhp.leave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements MyService.CallBack{

    TextView tv_status;
    ImageView iv_card;
    ImageView iv_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MyService.setCallBack(this);
        tv_status= (TextView) findViewById(R.id.tv_status);
        iv_card= (ImageView) findViewById(R.id.iv_card);
        iv_set= (ImageView) findViewById(R.id.iv_set);
        iv_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
            }
        });
        iv_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences date=HomeActivity.this.getSharedPreferences("data",MODE_PRIVATE);
                String ip=date.getString("ip","192.168.1.111");
                String section=date.getString("section","1");
                startService(ip,section);
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
        tv_status.setText("正在录入...");
        Intent i = new Intent(HomeActivity.this, MyService.class);
        startService(i);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){
                tv_status.setText("录入成功");
            }else {
                tv_status.setText("正在录入...");
            }
        }
    };

    @Override
    public void callBack(boolean ok) {
        if(ok){
            handler.sendEmptyMessage(0);
        }else {
            handler.sendEmptyMessage(1);
        }
    }

    @Override
    protected void onRestart() {
        MyService.setCallBack(this);
        super.onRestart();
    }
}
