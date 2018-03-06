package com.yorhp.leave;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Tyhj on 2017/8/17.
 */

public class Connect {

    private volatile static Connect connect; //声明成 volatile


    private static String ip;

    private static int port = 4196;

    private static Socket client = null;


    private Connect(String ip) {
        Log.e("Connect", "尝试建立连接");
        try {
            client = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(ip, port);
            client.connect(socketAddress,3000);
            client.getInputStream();
        } catch (IOException e) {
            Log.e("connectIOException", "Connect: ",e);
        }
    }

    public boolean sendMsg(String str) {
        try {
            if (client.isClosed()) {
                return false;
            }
            client.getOutputStream().write(ByteStringUtil.hexStrToByteArray(str));
            return true;
        } catch (IOException e) {
            connect = new Connect(ip);
            try {
                client.getOutputStream().write(ByteStringUtil.hexStrToByteArray(str));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    public static Connect getSingleton() throws IOException {
        Log.e("Connect", "什么情况getSingleton"+ip);
        if (connect == null || client.isClosed()||!client.isConnected()) {
            synchronized (Connect.class) {
                if (connect == null || client.isClosed()||!client.isConnected()) {
                    if (ip == null) {
                        return null;
                    }
                    Log.e("Connect", "我重连了一次");
                    connect = new Connect(ip);
                }
            }
        }
        return connect;
    }



    public static void setPort(String ip) {
        Connect.ip = ip;
        try {
            connect=null;
            if (client != null)
                client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
