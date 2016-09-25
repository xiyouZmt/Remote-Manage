package com.zmt.manager.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zmt.manager.Adapter.PCFileAdapter;
import com.zmt.manager.Application.App;
import com.zmt.manager.R;
import com.zmt.manager.Thread.AcceptCommand;
import com.zmt.manager.Thread.AcceptFile;
import com.zmt.manager.Thread.SendCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCFileActivity extends Activity {

    private App app;
    private ListView listView;
    private LinearLayout back;
    private LinearLayout operate;
    private LinearLayout execute;
    private LinearLayout download;
    private List<Map<String, String>> fileList;
    private ProgressDialog progressDialog;
    private TextView content;
    private String path;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_driver_list);
        initViews();
        setListener();
        Intent intent;
        if((intent = getIntent()) != null){
            path = intent.getStringExtra("path");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressDialog.show();
        AcceptCommand acceptCommand = new AcceptCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, handler);
        Thread accept = new Thread(acceptCommand, "AcceptCommand");
        accept.start();
    }

    public final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.obj.toString()){
                /**
                 * 保存当前路径下所有文件
                 */
                case "Command success" :
                    String data = (String) msg.getData().get("data");
                    try {
                        fileList.clear();
                        Log.e("data--->", data);
                        JSONObject jsonObject = new JSONObject(data);
                        PCFileAdapter adapter;
                        if(jsonObject.get("file").toString().equals("null")){
                            progressDialog.dismiss();
                            if(fileList.size() == 0){
                                adapter = new PCFileAdapter(PCFileActivity.this, fileList);
                                listView.setAdapter(adapter);
                                content.setVisibility(View.VISIBLE);
                            }
                        } else {
                            JSONArray jsonArray = jsonObject.getJSONArray("file");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = (JSONObject)jsonArray.get(i);
                                Map<String, String> map = new HashMap<>();
                                map.put("fileName", object.get("fileName").toString());
                                map.put("filePath", object.get("filePath").toString());
                                map.put("fileStyle", object.get("fileStyle").toString());
                                map.put("fileLength", object.get("fileLength").toString());
                                fileList.add(map);
                                progressDialog.dismiss();
                                adapter = new PCFileAdapter(PCFileActivity.this, fileList);
                                listView.setAdapter(adapter);
                            }
                        }
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        content.setText("解析失败, 请重新获取");
                        Log.e("Json Error--->", e.toString());
                    }
                    break;
                case "File success" :
                    progressDialog.dismiss();
                    progressDialog.setMessage("正在加载...");
                    String filePath = (String)msg.getData().get("filePath");
                    Toast.makeText(PCFileActivity.this, "下载成功! 已保存至" + filePath, Toast.LENGTH_SHORT).show();
                    break;
                case "Command error" :
                    progressDialog.dismiss();
                    Toast.makeText(PCFileActivity.this, "接收失败! 请检查服务端是否开启", Toast.LENGTH_SHORT).show();
                    break;
                case "File error" :
                    progressDialog.dismiss();
                    Toast.makeText(PCFileActivity.this, "下载失败! 请检查服务端是否开启", Toast.LENGTH_SHORT).show();
                    break;
            }
            
        }
    };

    public class PCFileItemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /**
             * 发送当前路径，获取当下所有文件
             */
            if(fileList.get(position).get("fileStyle").equals("directory")){
                progressDialog.show();
                String name = fileList.get(position).get("fileName") + '/';
                path += name;
                String data = "{\"command\":\"driver\",\"operation\":\"getFile\",\"path\":\"" + path + "\"}";
                SendCommand driverThread = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
                Thread driver = new Thread(driverThread, "SendCommand");
                driver.start();
                /**
                 * 开启接收数据线程
                 */
                handler.post(new DelayThread());
//                AcceptCommand acceptThread = new AcceptCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, handler);
//                Thread accept = new Thread(acceptThread, "AcceptCommand");
//                accept.start();
            } else {
                /**
                 * 执行操作
                 */
                operate.setVisibility(View.VISIBLE);
                pos = position;
            }
        }
    }

    public class DelayThread implements Runnable{

        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /**
             * 接收服务端发回的文件数据
             */
            AcceptCommand acceptCommand = new AcceptCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, handler);
            Thread accept = new Thread(acceptCommand, "AcceptCommand");
            accept.start();
        }
    }

    public class PCFileListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(!app.getUser().connected){
                Toast.makeText(PCFileActivity.this, "暂无连接", Toast.LENGTH_SHORT).show();
                return;
            }
//            String path = fileList.get(pos).get("filePath");
            StringBuilder filePath = new StringBuilder(path);
            filePath.append(fileList.get(pos).get("fileName"));
            String data = "";
            switch (v.getId()){
                case R.id.execute :
                    data = "{\"command\":\"driver\",\"operation\":\"execute\",\"path\":\"" + filePath + "\"}";
                    break;
                case R.id.download :
                    progressDialog.setMessage("正在下载...");
                    progressDialog.show();
                    data = "{\"command\":\"driver\",\"operation\":\"download\",\"path\":\"" + filePath + "\"}";
                    break;
            }
            /**
             * 执行文件操作
             */
            SendCommand driverThread = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
            Thread driver = new Thread(driverThread, "SendCommand");
            driver.start();
            if(v.getId() == R.id.download){
                /**
                 * 下载文件
                 */
                new Thread(){
                    public void run(){
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            Log.e("sleep error--->", e.toString());
                        }
                    }
                }.start();
                String fileName = fileList.get(pos).get("fileName");    
                AcceptFile acceptFile = new AcceptFile(app.getUser().socket, app.getUser().IP, app.getUser().port, handler, fileName);
                Thread t = new Thread(acceptFile, "AcceptFile");
                t.start();
            }
            operate.setVisibility(View.GONE);
        }
    }

    public void setListener(){
        execute.setOnClickListener(new PCFileListener());
        download.setOnClickListener(new PCFileListener());
    }

    public void initViews(){
        app = (App) getApplication();
        back = (LinearLayout) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.driverList);
        TextView title = (TextView) findViewById(R.id.fileName);
        LinearLayout search = (LinearLayout) findViewById(R.id.search);
        operate = (LinearLayout) findViewById(R.id.operate);
        execute = (LinearLayout) findViewById(R.id.execute);
        download = (LinearLayout) findViewById(R.id.download);
        content = (TextView) findViewById(R.id.fileState);
        search.setVisibility(View.GONE);
        title.setText("电脑磁盘");
        listView.setOnItemClickListener(new PCFileItemListener());
        fileList = new ArrayList<>();
        progressDialog = new ProgressDialog(PCFileActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在加载...");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            /**
             * 发送上一级路径，获取所有文件
             */
            if(operate.getVisibility() == View.VISIBLE){
                operate.setVisibility(View.GONE);
            } else if(path.indexOf('/') == path.lastIndexOf('/')){
                finish();
                return true;
            } else {
                content.setVisibility(View.GONE);
                String str = path.substring(0, path.lastIndexOf('/'));
                String path = str.substring(0, str.lastIndexOf('/') + 1);
                this.path = path;
                String data = "{\"command\":\"driver\",\"operation\":\"getFile\",\"path\":\"" + path + "\"}";
                SendCommand driverThread = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
                Thread driver = new Thread(driverThread, "SendCommand");
                driver.start();
                /**
                 * 开启接收数据线程
                 */
                handler.post(new DelayThread());
//            AcceptCommand acceptThread = new AcceptCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, handler);
//            Thread accept = new Thread(acceptThread, "AcceptCommand");
//            accept.start();
            }
        }
        return true;
    }

}
