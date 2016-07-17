package com.example.manager.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.Application.App;
import com.example.manager.Class.MediaFiles;
import com.example.manager.R;
import com.example.manager.Thread.FileThread;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.Utils.LoadFile;
import com.example.manager.Utils.StorageSize;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2016/4/17.
 */
public class Storage extends Activity {

    private App app;
    private ListView listView;
    private LinearLayout back;
    private LinearLayout search;
    private LinearLayout edit;
    private LinearLayout copy;
    private LinearLayout move;
    private LinearLayout share;
    private LinearLayout delete;
    private LinearLayout chooseAll;
    private LoadFile loadFile;
    private String style;
    private String path;
    private boolean hasChoseAll;
    private StorageHandler storageHandler;
    private ProgressDialog progressDialog;
    public  static List<MediaFiles> choseFiles;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        if((intent = getIntent()) != null) {
            switch (intent.getStringExtra("storage")) {
                case "in":
                    ActionBarUtil.initActionBar(getActionBar(), getResources().getString(R.string.storage_in), 0x000);
                    style = "in";
                    break;
                case "out":
                    ActionBarUtil.initActionBar(getActionBar(), getResources().getString(R.string.storage_out), 0x000);
                    style = "out";
                    break;
            }
        }
        setContentView(R.layout.filelist);
        initView();
        setListener();
        switch (style) {
            case "in" :
                loadFile.loadStorage(Environment.getExternalStorageDirectory() + "/");
                StorageAdapter adapter = new StorageAdapter(getApplicationContext(), loadFile.getStorage());
                listView.setAdapter(adapter);
                break;
            case "out" :
                StorageSize storageSize = new StorageSize();
                if(storageSize.externalStorageAvailable() != null) {
                    loadFile.loadStorage(storageSize.externalPath);
                    adapter = new StorageAdapter(getApplicationContext(), loadFile.getStorage());
                    listView.setAdapter(adapter);
                }
                break;
        }
    }

    public class StorageAdapter extends BaseAdapter{

        public LayoutInflater inflater;
        public List list;

        public StorageAdapter(Context context, List list){
            inflater = LayoutInflater.from(context);
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
            RelativeLayout relative;
            ViewHolder viewHolder;
            if(convertView == null) {
                relative = (RelativeLayout) inflater.inflate(R.layout.list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.fileName = (TextView)relative.findViewById(R.id.file_name);
                viewHolder.fileSize = (TextView)relative.findViewById(R.id.file_size);
                viewHolder.fileImage = (ImageView)relative.findViewById(R.id.file_image);
                viewHolder.check = (ImageView)relative.findViewById(R.id.checkBox);
                relative.setTag(viewHolder);
            } else {
                relative = (RelativeLayout)convertView;
                viewHolder =(ViewHolder)relative.getTag();
            }
            final MediaFiles file = (MediaFiles)list.get(position);
            file.check = viewHolder.check;
            if(file.count == 1) {
                file.check.setBackgroundResource(R.drawable.side_checked);
            } else {
                file.check.setBackgroundResource(R.drawable.side);
            }
            viewHolder.fileName.setText(file.getFileName());
            if(file.isFile){
                /**
                 * 设置文件大小
                 */
                StorageSize storageSize = new StorageSize();
                viewHolder.fileSize.setText(storageSize.typeChange(Double.parseDouble(file.getFileSize())));
                /**
                 * 设置文件图片
                 */
                viewHolder.fileImage.setBackgroundResource(R.drawable.file);
            } else {
                /**
                 * 文件夹中项目的个数
                 */
                viewHolder.fileSize.setText(file.getItemCount(file.getFilePath()) + "项");
                viewHolder.fileImage.setBackgroundResource(R.drawable.directory);
            }
            file.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (file.count == 0) {
                        file.check.setBackgroundResource(R.drawable.side_checked);
                        file.count = 1;
                        choseFiles.add(file);
                        edit.setVisibility(View.VISIBLE);
                    } else {
                        file.check.setBackgroundResource(R.drawable.side);
                        file.count = 0;
                        choseFiles.remove(file);
                        int aChoose;
                        for (aChoose = 0; aChoose < loadFile.getStorage().size(); aChoose++) {
                            if (loadFile.getStorage().get(aChoose).count == 1) {
                                break;
                            }
                        }
                        if (aChoose == loadFile.getStorage().size()) {
                            edit.setVisibility(View.GONE);
                        }
                    }
                }
            });

            return relative;
        }
        class ViewHolder{
            ImageView fileImage;
            TextView fileName;
            TextView fileSize;
            ImageView check;
        }
    }

    public class StorageListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()){
                case R.id.back :
                    finish();
                    break;
                case R.id.search :

                    break;
                case R.id.copy :
                    intent.setClass(Storage.this, OperateFile.class);
                    intent.putExtra("style", R.string.storage_in);
                    intent.putExtra("operation", "copy");
                    startActivity(intent);
                    break;
                case R.id.move :
                    intent.setClass(Storage.this, OperateFile.class);
                    intent.putExtra("style", R.string.storage_in);
                    intent.putExtra("operation", "move");
                    startActivity(intent);
                    break;
                case R.id.share :
                    if(app.getUser().connected) {
                        File file = new File(choseFiles.get(0).getFilePath());
                        FileThread ft = new FileThread(app.getUser().socket, app.getUser().IP, app.getUser().port, file, storageHandler);
                        Thread t = new Thread(ft, "FileThread");
                        t.start();
                    } else {
                        Toast.makeText(Storage.this, "设备未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.delete :
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Storage.this);
                    dialog.setTitle("提示").setMessage("确认删除 ?").setPositiveButton("确认", new DialogListener())
                            .setNegativeButton("取消", new DialogListener()).create().show();
                    break;
                case R.id.chooseAll :
                    MediaFiles file;
                    if(!hasChoseAll) {
                        for (int i = 0; i < loadFile.getStorage().size(); i++) {
                            file = loadFile.getStorage().get(i);
                            if (file.count == 0) {
                                file.count = 1;
                                if (file.check != null) {
                                    file.check.setBackgroundResource(R.drawable.side_checked);
                                }
                            }
                            if (i == loadFile.getStorage().size() - 1) {
                                hasChoseAll = true;
                            }
                        }
                    } else {
                        for (int i = 0; i < loadFile.getStorage().size(); i++) {
                            file = loadFile.getStorage().get(i);
                            file.count = 0;
                            if (file.check != null) {
                                file.check.setBackgroundResource(R.drawable.side);
                            }
                        }
                        edit.setVisibility(View.GONE);
                        hasChoseAll = false;
                    }
                    break;
            }
        }
    }

    public class ListListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            edit.setVisibility(View.GONE);
            MediaFiles file = loadFile.getStorage().get(position);
            if(!file.isFile){
                MediaFiles files = loadFile.getStorage().get(0);
                path = files.getFilePath();         //保存当前第一个元素的路径
                loadFile.getStorage().clear();
                loadFile.loadStorage(file.getFilePath() + "/");
                StorageAdapter adapter = new StorageAdapter(getApplicationContext(), loadFile.getStorage());
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new ListListener());
            }
        }
    }

    public class DialogListener implements DialogInterface.OnClickListener{

        /**
         * setPositiveButton:一个积极的按钮，一般用于“OK”或者“继续”等操作。
         * setNegativeButton:一个负面的按钮，一般用于“取消”操作。
         * setNeutralButton:一个比较中性的按钮，一般用于“忽略”、“以后提醒我”等操作。
         * which为点击按钮的标识符，是一个 整形的数据，对于这三个按钮而言，每个按钮使用不同的int类型数据进行标识：
         * Positive（-1）、Negative(-2)、 Neutral(-3)。
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case -1 :
                    dialog.dismiss();
                    boolean result = false;
                    if(hasChoseAll){
                        choseFiles = loadFile.getStorage();
                    }
                    for (int i = 0; i < choseFiles.size(); i++) {
                        File file = new File(choseFiles.get(i).getFilePath());
                        result = loadFile.deleteFile(file);
                        if(result) {
                            loadFile.getStorage().remove(choseFiles.get(i));
                        }
                    }
                    choseFiles.clear();
                    StorageAdapter storageAdapter = new StorageAdapter(getApplicationContext(),loadFile.getStorage());
                    listView.setAdapter(storageAdapter);
                    if(result) {
                        Toast.makeText(Storage.this, "删除完成", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Storage.this, "删除失败, 没有权限", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case -2 :
                    dialog.dismiss();
                    break;
            }
        }
    }

    public class StorageHandler extends Handler{
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x001 :
                    count ++;
                    if(count < choseFiles.size()) {
                        File file = new File(choseFiles.get(count).getFilePath());
                        FileThread ft = new FileThread(app.getUser().socket, app.getUser().IP, app.getUser().port, file, storageHandler);
                        Thread t = new Thread(ft, "FileThread");
                        t.start();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Storage.this, "传输完成!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x333 :
                    progressDialog.dismiss();
                    app.getUser().connected = false;
                    Toast.makeText(Storage.this, "连接失败，请重新连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            List lastFiles;
            if(loadFile.getStorage().isEmpty()){
                lastFiles = loadFile.loadStorage(path.substring(0, path.lastIndexOf('/')));
            } else {
                MediaFiles file1 = loadFile.getStorage().get(0);
                MediaFiles file2 = new LoadFile(Storage.this).loadStorage(Environment.getExternalStorageDirectory() + "/").get(0);
                if (file1.getFilePath().equals(file2.getFilePath())) {
                    finish();
                    return true;
                } else {
                    lastFiles = loadFile.getLastFile(loadFile.getStorage().get(0));
                }
            }
            StorageAdapter adapter = new StorageAdapter(getApplicationContext(), lastFiles);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new ListListener());
        }
        return false;
    }

    public void setListener(){
        back.setOnClickListener(new StorageListener());
        search.setOnClickListener(new StorageListener());
        copy.setOnClickListener(new StorageListener());
        move.setOnClickListener(new StorageListener());
        share.setOnClickListener(new StorageListener());
        delete.setOnClickListener(new StorageListener());
        chooseAll.setOnClickListener(new StorageListener());
        listView.setOnItemClickListener(new ListListener());
    }

    public void initView(){
        back = (LinearLayout) findViewById(R.id.back);
        search = (LinearLayout) findViewById(R.id.search);
        edit = (LinearLayout) findViewById(R.id.edit);
        copy = (LinearLayout) findViewById(R.id.copy);
        move = (LinearLayout) findViewById(R.id.move);
        share = (LinearLayout) findViewById(R.id.share);
        delete = (LinearLayout) findViewById(R.id.delete);
        chooseAll = (LinearLayout) findViewById(R.id.chooseAll);
        listView = (ListView) findViewById(R.id.fileList);
        loadFile = new LoadFile(Storage.this);
        choseFiles = new ArrayList<>();
        storageHandler = new StorageHandler();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("传输中...");
        progressDialog.setCanceledOnTouchOutside(false);
        app = (App) getApplication();
    }

}
