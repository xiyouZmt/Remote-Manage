package com.zmt.manager.Thread;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Dangelo on 2016/5/20.
 */
public class Connect implements Runnable {
    private Handler handler;
    private Socket socket;
    private String IP;
    private int port;

    public Connect(Handler handler, Socket socket, String IP, int port) {
        this.handler = handler;
        this.socket = socket;
        this.IP = IP;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(IP, port);
            handler.sendEmptyMessage(0x000);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.close();
            socket.close();
        } catch (IOException e) {
            Log.e("Socket Error", e.toString());
            handler.sendEmptyMessage(0x111);
        }
    }
}
