package com.example.manager.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.manager.Application.App;
import com.example.manager.R;
import com.example.manager.Thread.AcceptThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverList extends Activity {

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
        AcceptThread acceptThread = new AcceptThread(app.getUser().socket, app.getUser().IP, app.getUser().port, handler);
        Thread accept = new Thread(acceptThread, "AcceptThread");
        accept.start();
    }

//    private Handler handler = new Handler(){
//        public void handleMessage(Message msg){
//            String data = (String) msg.getData().get("data");
//            try {
//                JSONObject jsonObject = new JSONObject(data);
//                JSONArray jsonArray = jsonObject.getJSONArray("driver");
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject object = (JSONObject) jsonArray.get(i);
//                    Map<String, String> map = new HashMap<>();
//                    map.put("name", object.get("name").toString());
//                    map.put("totalSize", object.get("totalSize").toString());
//                    map.put("availableSize", object.get("availableSize").toString());
//                    driverList.add(map);
//                }
//                DriverAdapter adapter = new DriverAdapter(DriverList.this, driverList);
//                listView.setAdapter(adapter);
//            } catch (JSONException e) {
//                Log.e("error", e.toString());
//            }
//        }
//    };

    public class DriverHandler extends Handler {
        public void handleMessage(Message msg) {
            String data = (String) msg.getData().get("data");
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("driver");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    Map<String, String> map = new HashMap<>();
                    map.put("name", object.get("name").toString());
                    map.put("totalSize", object.get("totalSize").toString());
                    map.put("availableSize", object.get("availableSize").toString());
                    driverList.add(map);
                }
                progressDialog.dismiss();
                DriverAdapter adapter = new DriverAdapter(DriverList.this, driverList);
                listView.setAdapter(adapter);
            } catch (JSONException e) {
                Log.e("error", e.toString());
            }
        }
    }

    public class DriverAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<Map<String, String>> list;

        public DriverAdapter(Context context, List<Map<String, String>> list) {
            layoutInflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linearLayout;
            ViewHolder viewHolder;
            if (convertView == null) {
                linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.driver_item, null);
                viewHolder = new ViewHolder();
                viewHolder.driverName = (TextView) linearLayout.findViewById(R.id.driverName);
                viewHolder.totalSize = (TextView) linearLayout.findViewById(R.id.totalSize);
                viewHolder.availableSize = (TextView) linearLayout.findViewById(R.id.availableSize);
                viewHolder.progressBar = (ProgressBar) linearLayout.findViewById(R.id.diskProgressBar);
                linearLayout.setTag(viewHolder);
            } else {
                linearLayout = (LinearLayout) convertView;
                viewHolder = (ViewHolder) linearLayout.getTag();
            }
            String total = list.get(position).get("totalSize");
            String available = list.get(position).get("availableSize");
            viewHolder.driverName.setText(list.get(position).get("name"));
            viewHolder.totalSize.setText("共" + total);
            viewHolder.availableSize.setText(available + "可用");
            /**
             * 总空间大小
             */
            String allSize = total.substring(0, total.lastIndexOf('G'));
            String allSizeToInteger = String.valueOf(Double.parseDouble(allSize) * 100);
            int allSizeForInteger = Integer.parseInt(allSizeToInteger.substring(0, allSizeToInteger.lastIndexOf('.')));
            /**
             * 可用空间大小
             */
            String hasUsedSize = available.substring(0, available.lastIndexOf('G'));
            String hasUsedSizeToInteger = String.valueOf(Double.parseDouble(hasUsedSize) * 100);
            int hasUsedSizeForInteger = Integer.parseInt(hasUsedSizeToInteger.substring(0, hasUsedSizeToInteger.lastIndexOf('.')));

            viewHolder.progressBar.setMax(allSizeForInteger);
            viewHolder.progressBar.setProgress(allSizeForInteger - hasUsedSizeForInteger);

            return linearLayout;
        }

        class ViewHolder {
            public TextView driverName;
            public TextView totalSize;
            public TextView availableSize;
            public ProgressBar progressBar;
        }
    }

    public class ItemListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }

    public void initViews() {
        app = (App) getApplication();
        back = (LinearLayout) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.driverList);
        TextView title = (TextView) findViewById(R.id.fileName);
        LinearLayout search = (LinearLayout) findViewById(R.id.search);
        search.setVisibility(View.GONE);
        title.setText("电脑磁盘");
        listView.setOnItemClickListener(new ItemListener());
        driverList = new ArrayList<>();
        progressDialog = new ProgressDialog(DriverList.this);
        progressDialog.setMessage("加载中...");
        handler = new DriverHandler();
    }

}
