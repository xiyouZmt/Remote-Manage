package com.example.manager.Thread;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Dangelo on 2016/8/18.
 */
public class ServerThread implements Runnable {

    private ServerSocket serverSocket;
    private Handler handler;
    private String rootPath;

    public ServerThread(Handler handler, int post){
        try {
            this.handler = handler;
            serverSocket = new ServerSocket(post);
            rootPath = Environment.getExternalStorageDirectory() + "/";
            Log.e("makeServer--->", "服务端创建成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            while (true){
                Socket socket = serverSocket.accept();
                Log.e("connect--->", "客户端连接成功");
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                if(builder.toString().equals("")){
                    continue;
                }
                JSONObject jsonObject = new JSONObject(builder.toString());
                if(jsonObject.has("file")){
                    String fileName = jsonObject.get("fileName").toString();
                    File file = new File(rootPath + "Manager/phoneFiles");
                    if(!file.exists()){
                        file.mkdirs();                               //新建文件夹
                    }
                    /**
                     * 字节流读取文件
                     */
                    Socket fileSocket = serverSocket.accept();
                    InputStream is = fileSocket.getInputStream();
                    FileOutputStream os = new FileOutputStream(file.getPath() + '/' + fileName);
                    byte[] c = new byte[1024 * 100];
                    int b;
                    while ((b = is.read(c)) > 0) {
                        os.write(c, 0, b);
                    }
                    is.close();
                    os.close();
                    fileSocket.close();
                }
                socket.close();
            }
        }catch(Exception e){
            handler.sendEmptyMessage(0x111);
            Log.e("Server error--->", e.toString());
        }
    }
}
