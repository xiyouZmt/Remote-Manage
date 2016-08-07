package com.example.manager.Thread;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Dangelo on 2016/7/27.
 */
public class AcceptFile implements Runnable {

    private Socket socket;
    private String IP;
    private int port;
    private String fileName;
    private String rootPath = Environment.getExternalStorageDirectory() + "/";
    private Handler handler;
    private Message msg;

    public AcceptFile(Socket socket, String IP, int port, Handler handler, String fileName) {
        this.socket = socket;
        this.IP = IP;
        this.port = port;
        this.handler = handler;
        this.fileName = fileName;
        msg = new Message();
    }

    @Override
    public void run() {
        File file = new File(rootPath + "/Manager/PCFiles");
        if(!file.exists()){
            file.mkdirs();
        }
        try {
            socket = new Socket(IP, port);
            InputStream is = socket.getInputStream();
            OutputStream os = new FileOutputStream(file.getPath() + '/' + fileName);
            byte [] b = new byte[1024 * 10];
            int c;
            while ((c = is.read(b)) > 0){
                os.write(b, 0, c);
            }
            os.close();
            is.close();
            socket.close();
            msg.obj = "File success";
            Bundle bundle = new Bundle();
            bundle.putString("filePath", file.getPath() + '/' + fileName);
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (IOException e) {
            Log.e("AcceptFile error--->", e.toString());
            msg.obj = "File error";
            handler.sendMessage(msg);
        }
    }
}
