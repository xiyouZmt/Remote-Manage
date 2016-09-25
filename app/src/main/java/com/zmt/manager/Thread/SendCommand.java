package com.zmt.manager.Thread;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Dangelo on 2016/5/20.
 */
public class SendCommand implements Runnable {

    private Socket socket;
    private String IP;
    private int port;
    private String data;

    public SendCommand(Socket socket, String IP, int port, String data){
        this.socket = socket;
        this.IP = IP;
        this.port = port;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(IP, port);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(data);
            writer.close();
            socket.close();
        } catch (IOException e) {
            Log.e("Socket Error", e.toString());
        }
    }
}
