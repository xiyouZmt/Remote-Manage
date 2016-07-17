package com.example.manager.Thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Dangelo on 2016/6/2.
 */
public class AcceptThread implements Runnable {

    private Socket socket;
    private String IP;
    private int port;
    private Handler handler;

    public AcceptThread(Socket socket, String IP, int port, Handler handler){
        this.socket = socket;
        this.IP = IP;
        this.port = port;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(IP, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }
            reader.close();
            socket.close();
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("data", buffer.toString());
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (IOException e) {
            Log.e("error", e.toString());
        }
    }
}
