package com.example.manager.Thread;

import android.util.Log;

import com.example.manager.Fragment.ConnectFragment;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Dangelo on 2016/5/20.
 */
public class SocketThread implements Runnable {
    private Socket socket;
    private String IP;
    private int port;

    public SocketThread(Socket socket, String IP, int port){
        this.socket = socket;
        this.IP = IP;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(IP, port);
            ConnectFragment.connectHandler.sendEmptyMessage(0x000);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.close();
            socket.close();
        } catch (Exception e) {
            Log.e("Socket Error", e.toString());
            ConnectFragment.connectHandler.sendEmptyMessage(0x111);
        }
    }
}
