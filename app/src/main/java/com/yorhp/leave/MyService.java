package com.yorhp.leave;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

public class MyService extends Service {

    public static String SECTION="127";
    private OutputStream out;
    private InputStream in;
    private String data;
    public static CallBack callBack;
    int sleep=1000;
    PowerManager.WakeLock wakeLock;

    public static void setCallBack(CallBack callBack) {
        MyService.callBack = callBack;
    }

    public MyService() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification(getApplicationContext(),0,"","");

        /* 注册屏幕唤醒时的广播 */
        IntentFilter mScreenOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        MyService.this.registerReceiver(mScreenOReceiver, mScreenOnFilter);

        /* 注册机器锁屏时的广播 */
        IntentFilter mScreenOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        MyService.this.registerReceiver(mScreenOReceiver, mScreenOffFilter);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireWakeLock();
        new Thread(thread).start();
        releaseWakeLock();
        acquireWakeLock();
    }

    Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    if(Connect.getSingleton().sendMsg("7A40000104"+ SECTION+ByteStringUtil.StrToAddHexStr(new String[]{"7A","40","00","01","04",SECTION})+"5A")){
                        callBack.callBack(true);
                        sleep=60000;
                    }else {
                        callBack.callBack(false);
                        sleep=1000;
                    }
                    Log.e("What","进行中");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    public interface CallBack{
        void callBack(boolean ok);
    }

    private void showNotification(Context context, int id, String title, String text) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setAutoCancel(false);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setVisibility(Notification.VISIBILITY_SECRET);
        Notification notification = builder.build();
        notification.flags=Notification.FLAG_NO_CLEAR;

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
        startForeground(id, notification);
    }

    private BroadcastReceiver mScreenOReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try {
                //Connect.getSingleton().sendMsg("7A40000104"+ SECTION+ByteStringUtil.StrToAddHexStr(new String[]{"7A","40","00","01","04",SECTION})+"5A");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (action.equals("android.intent.action.SCREEN_ON")) {
                System.out.println("—— SCREEN_ON ——");
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                System.out.println("—— SCREEN_OFF ——");
            }
        }

    };

    private void acquireWakeLock() {
        if (wakeLock ==null) {
            PowerManager pm = (PowerManager) getSystemService(MyService.POWER_SERVICE); //mContext 为上下文如：xxActivity.this
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock !=null&& wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock =null;
        }
    }


    @Override
    public void onDestroy() {
        releaseWakeLock();
        super.onDestroy();
    }
}
