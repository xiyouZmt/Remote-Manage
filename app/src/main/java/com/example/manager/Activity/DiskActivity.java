package com.example.manager.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.manager.Adapter.DiskAdapter;
import com.example.manager.Application.App;
import com.example.manager.R;
import com.example.manager.Thread.AcceptCommand;
import com.example.manager.Thread.SendCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiskActivity extends Activity {

    private App app;
    private ListView listView;
    private LinearLayout back;
    private List<Map<String, String>> driverList;
    private ProgressDialog progressDialog;
    private DriverHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_driver_list);
        initViews();
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

    public class DriverHandler extends Handler {
        public void handleMessage(Message msg) {
            String data = (String) msg.getData().get("data");
            try {
                JSONObject jsonObject = new JSONObject(data);
                /**
                 * 所有磁盘信息
                 */
                driverList.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("driver");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    Map<String, String> map = new HashMap<>();
                    map.put("diskName", object.get("diskName").toString());
                    map.put("totalSize", object.get("totalSize").toString());
                    map.put("availableSize", object.get("availableSize").toString());
                    driverList.add(map);
                }
                progressDialog.dismiss();
                DiskAdapter adapter = new DiskAdapter(DiskActivity.this, driverList);
                listView.setAdapter(adapter);
            } catch (JSONException e) {
                Log.e("error", e.toString());
            }
        }
    }

    public class ItemListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /**
             * 发送当前路径，获取当下所有文件
             */
            String name = driverList.get(position).get("diskName");
            String path = name.substring(name.indexOf('(') + 1, name.indexOf(')')) + '/';
            String data = "{\"command\":\"driver\",\"operation\":\"getFile\",\"path\":\"" + path + "\"}";
            SendCommand driverThread = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
            Thread driver = new Thread(driverThread, "SendCommand");
            driver.start();
            Intent intent = new Intent(DiskActivity.this, PCFileActivity.class);
            intent.putExtra("path", path);
            startActivity(intent);
        }
    }

    public void initViews() {
        app = (App) getApplication();
        back = (LinearLayout) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.driverList);
        TextView title = (TextView) findViewById(R.id.fileName);
        title.setText("电脑磁盘");
        listView.setOnItemClickListener(new ItemListener());
        driverList = new ArrayList<>();
        progressDialog = new ProgressDialog(DiskActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("加载中...");
        handler = new DriverHandler();
    }
}
