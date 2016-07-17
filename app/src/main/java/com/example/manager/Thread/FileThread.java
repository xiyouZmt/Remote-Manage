package com.example.manager.Thread;

import android.os.Handler;
import android.util.Log;

import com.example.manager.Activity.MusicList;
import com.example.manager.Class.MediaFiles;
import com.example.manager.Utils.LoadFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

/**
 * Created by Dangelo on 2016/5/20.
 */
public class FileThread implements Runnable {

    private Socket socket;
    private String IP;
    private int port;
    private File file;
    private BufferedWriter writer;
    private OutputStream os;
    private Handler handler;

    public FileThread(Socket socket, String IP, int port, File file, Handler handler){
        this.socket = socket;
        this.IP = IP;
        this.port = port;
        this.file = file;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            if(file.isFile()) {
                socket = new Socket(IP, port);
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String data  = "{'file':'file','fileName':'" + file.getName() + "'}";
                writer.write(data);
                writer.close();
                socket.close();
                Socket fileSocket = new Socket(IP, port);
                os = fileSocket.getOutputStream();
                sendFiles(file);
                os.close();
                fileSocket.close();
            } else {
                handler.sendEmptyMessage(0x444);
            }
            handler.sendEmptyMessage(0x001);
        } catch (IOException e) {
            Log.e("error", e.toString());
            handler.sendEmptyMessage(0x333);
        }
    }

    public boolean sendFiles(File file){
        if(file.isFile() && file.exists()){
            try {
                /**
                 * 字节流发送文件
                 */
                InputStream is = new FileInputStream(file.getPath());
                byte [] c = new byte[1024 * 10];
                int b;
                while ((b = is.read(c)) > 0){
                    os.write(c, 0, b);
                }
                is.close();
            } catch (Exception e) {
                Log.e("error", e.toString());
            }
            return true;
        }
        return false;
    }
}
