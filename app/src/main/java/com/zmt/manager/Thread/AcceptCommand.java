package com.zmt.manager.Thread;

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
public class AcceptCommand implements Runnable {

    private Socket socket;
    private String IP;
    private int port;
    private Handler handler;
    private Message msg;

    public AcceptCommand(Socket socket, String IP, int port, Handler handler){
        this.socket = socket;
        this.IP = IP;
        this.port = port;
        this.handler = handler;
        msg = new Message();
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
            Bundle bundle = new Bundle();
            bundle.putString("data", buffer.toString());
            msg.setData(bundle);
            msg.obj = "Command success";
            handler.sendMessage(msg);
        } catch (IOException e) {
            Log.e("error", e.toString());
            msg.obj = "Command error";
            handler.sendMessage(msg);
        }
    }
}
