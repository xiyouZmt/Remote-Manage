package com.example.manager.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.Adapter.StorageAdapter;
import com.example.manager.Application.App;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;
import com.example.manager.Thread.SendFile;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.Utils.CircleProgress;
import com.example.manager.Utils.LoadFile;
import com.example.manager.Utils.StorageSize;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Android on 2016/4/17.
 */
public class StorageActivity extends Activity {

    private App app;
    private ListView listView;
    private LinearLayout back;
    private LinearLayout edit;
    private LinearLayout copy;
    private LinearLayout move;
    private LinearLayout share;
    private LinearLayout delete;
    private LinearLayout chooseAll;
    private LoadFile loadFile;
    private String style;
    private String path;
    private PopupWindow popupWindow;
    private RelativeLayout progress_background;
    private CircleProgress circleProgress;
    private TextView fileCount;
    private ProgressDialog progressDialog;
    private StorageAdapter storageAdapter;
    public  int pos = 0;
    private int max = 0;
    private int count = 0;
    private double temp = 0;
    private boolean hasChoseAll;
    public  static String TAG = "";
    public  static List<MediaFiles> choseFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filelist);
        initView();
        setListener();
        switch (style) {
            case "in" :
                loadFile.loadStorage(Environment.getExternalStorageDirectory() + "/");
                Collections.sort(loadFile.getStorage(), new Comparator<MediaFiles>() {
                    @Override
                    public int compare(MediaFiles lhs, MediaFiles rhs) {
                        return lhs.getFileName().compareTo(rhs.getFileName());
                    }
                });
                storageAdapter = new StorageAdapter(getApplicationContext(),
                        loadFile.getStorage(), loadFile, choseFiles, edit);
                break;
            case "out" :
                StorageSize storageSize = new StorageSize();
                Collections.sort(loadFile.getStorage(), new Comparator<MediaFiles>() {
                    @Override
                    public int compare(MediaFiles lhs, MediaFiles rhs) {
                        return lhs.getFileName().compareTo(rhs.getFileName());
                    }
                });
                if(storageSize.externalStorageAvailable() != null) {
                    loadFile.loadStorage(storageSize.externalPath);
                    storageAdapter = new StorageAdapter(getApplicationContext(),
                            loadFile.getStorage(), loadFile, choseFiles, edit);
                }
                break;
        }
        listView.setAdapter(storageAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(edit.getVisibility() == View.VISIBLE){
            if(hasChoseAll){
                for (int i = 0; i < loadFile.getStorage().size(); i++) {
                    loadFile.getStorage().get(i).count = 0;
                }
                if(TAG.equals("move succeed")){
                    loadFile.getStorage().clear();
                }
            } else {
                for (int i = 0; i < choseFiles.size(); i++) {
                    choseFiles.get(i).count = 0;
                }
                if(TAG.equals("move succeed")){
                    loadFile.getStorage().removeAll(choseFiles);
                }
            }
            TAG = "";
            choseFiles.clear();
            storageAdapter.notifyDataSetChanged();
            edit.setVisibility(View.GONE);
        }
    }

    private Handler storageHandler = new Handler(){

        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x000 :
                    int i = msg.arg1 * 100 / max;
                    if(i >= 1){
                        String percentage = String.valueOf(i);
                        circleProgress.setProgress(circleProgress.getProgress() + Integer.parseInt(percentage));
                    } else {
                        double d = (double)msg.arg1 * 100 / max;
                        temp += d;
                        if(temp >= 1){
                            String percentage = String.valueOf((int)temp);
                            circleProgress.setProgress(circleProgress.getProgress() + Integer.parseInt(percentage));
                            temp = 0;
                        }
                    }
                    break;
                case 0x001 :
                    count ++;
                    if(count < choseFiles.size()) {
                        int currentCount = count + 1;
                        fileCount.setText("共" + choseFiles.size() + "项, 第" + currentCount + "项");
                        File file = new File(choseFiles.get(count).getFilePath());
                        try {
                            max = new FileInputStream(file).available();
                        } catch (IOException e) {
                            Log.e("io error--->", e.toString());
                        }
                        SendFile ft = new SendFile(app.getUser().socket, app.getUser().IP, app.getUser().port, file, storageHandler);
                        Thread t = new Thread(ft, "SendFile");
                        t.start();
                    } else {
                        progress_background.setVisibility(View.GONE);
                        count = 0;
                        if(hasChoseAll){
                            for (i = 0; i < loadFile.getStorage().size(); i++) {
                                loadFile.getStorage().get(i).count = 0;
                            }
                        } else {
                            for (i = 0; i < choseFiles.size(); i++) {
                                choseFiles.get(i).count = 0;
                            }
                        }
                        choseFiles.clear();
                        storageAdapter.notifyDataSetChanged();
                        circleProgress.setProgress(0);
                        Toast.makeText(StorageActivity.this, "传输完成!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x333 :
                    progressDialog.dismiss();
                    app.getUser().connected = false;
                    circleProgress.setProgress(0);
                    Toast.makeText(StorageActivity.this, "连接失败，请重新连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public class StorageListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()){
                case R.id.back :
                    finish();
                    break;
                case R.id.copy :
                    intent.setClass(StorageActivity.this, OperateActivity.class);
                    intent.putExtra("style", R.string.storage_in);
                    intent.putExtra("operation", "copy");
                    startActivity(intent);
                    break;
                case R.id.move :
                    intent.setClass(StorageActivity.this, OperateActivity.class);
                    intent.putExtra("style", R.string.storage_in);
                    intent.putExtra("operation", "move");
                    startActivity(intent);
                    break;
                case R.id.share :
                    View view = getLayoutInflater().inflate(R.layout.choose_type, null);
                    Button commit = (Button) view.findViewById(R.id.commit);
                    Button cancel = (Button) view.findViewById(R.id.cancel);
                    commit.setOnClickListener(new ShareListener());
                    cancel.setOnClickListener(new ShareListener());
                    popupWindow.setContentView(view);
                    popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                    break;
                case R.id.delete :
                    AlertDialog.Builder dialog = new AlertDialog.Builder(StorageActivity.this);
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
                                if (file.checkBox != null) {
                                    file.checkBox.setChecked(true, true);
                                }
                            }
                            if (i == loadFile.getStorage().size() - 1) {
                                hasChoseAll = true;
                            }
                        }
                        choseFiles = loadFile.getStorage();
                    } else {
                        for (int i = 0; i < loadFile.getStorage().size(); i++) {
                            file = loadFile.getStorage().get(i);
                            file.count = 0;
                            if (file.checkBox != null) {
                                file.checkBox.setChecked(false, true);
                            }
                        }
                        edit.setVisibility(View.GONE);
                        hasChoseAll = false;
                    }
                    break;
            }
        }
    }

    public class ShareListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            popupWindow.dismiss();
            switch (v.getId()){
                case R.id.commit :
                    if(app.getUser().connected) {
                        edit.setVisibility(View.GONE);
                        progress_background.setVisibility(View.VISIBLE);
                        fileCount.setText("共" + choseFiles.size() + "项, 第1项" );
                        File file = new File(choseFiles.get(0).getFilePath());
                        if(file.isFile()){
                            try {
                                max = new FileInputStream(file).available();
                            } catch (IOException e) {
                                Log.e("io error--->", e.toString());
                            }
                            SendFile ft = new SendFile(app.getUser().socket, app.getUser().IP, app.getUser().port, file, storageHandler);
                            Thread t = new Thread(ft, "SendFile");
                            t.start();
                        } else {
                            Toast.makeText(StorageActivity.this, "暂不支持上传文件夹!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(StorageActivity.this, "设备未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.cancel :
                    popupWindow.dismiss();
                    break;
            }
        }
    }

    public class ListListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            pos = position;
            edit.setVisibility(View.GONE);
            MediaFiles file = loadFile.getStorage().get(position);
            if(!file.isFile){
                MediaFiles files = loadFile.getStorage().get(0);
                path = files.getFilePath();         //保存当前第一个元素的路径
                loadFile.getStorage().clear();
                loadFile.loadStorage(file.getFilePath() + "/");
                Collections.sort(loadFile.getStorage(), new Comparator<MediaFiles>() {
                    @Override
                    public int compare(MediaFiles lhs, MediaFiles rhs) {
                        return lhs.getFileName().compareTo(rhs.getFileName());
                    }
                });
                storageAdapter = new StorageAdapter(getApplicationContext(), loadFile.getStorage(), loadFile, choseFiles, edit);
                listView.setAdapter(storageAdapter);
                listView.setOnItemClickListener(new ListListener());
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("file://" + loadFile.getStorage().get(position).getFilePath());
                switch (file.getFilePath().substring(file.getFilePath().lastIndexOf('.') + 1)){
                    case "mp3" :
                        intent.setDataAndType(uri, "audio/*");
                        break;
                    case "mp4":
                    case "rmvb" :
                    case "mkv" :
                        intent.setDataAndType(uri, "video/*");
                        break;
                    case "jpg" :
                    case "jpeg" :
                    case "png" :
                    case "gif" :
                        intent.setDataAndType(uri, "image/*");
                    case "txt" :
                    case "pdf" :
                    case "doc" :
                    case "docx" :
                    case "xlsx" :
                    case "ppt" :
                    case "pptx" :
                        intent.setDataAndType(uri, "text/*");
                        break;
                    case "zip" :
                        intent.setDataAndType(uri, "application/zip");
                    case "rar" :
                        intent.setDataAndType(uri, "application/x-rar-compressed");
                        break;
                    case "apk" :
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        break;
                    default:
                        intent.setDataAndType(uri, "text/*");
                        break;
                }
                startActivity(intent);
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
                    for (int i = 0; i < choseFiles.size(); i++) {
                        File file = new File(choseFiles.get(i).getFilePath());
                        result = loadFile.deleteFile(file);
                    }
                    if(result){
                        if(hasChoseAll){
                            loadFile.getStorage().clear();
                        } else {
                            loadFile.getStorage().removeAll(choseFiles);
                            choseFiles.clear();
                        }
                    }
                    storageAdapter.notifyDataSetChanged();
                    edit.setVisibility(View.GONE);
                    if(result) {
                        Toast.makeText(StorageActivity.this, "删除完成", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StorageActivity.this, "删除失败, 没有权限", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case -2 :
                    dialog.dismiss();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            List<MediaFiles> lastFiles;
            if(loadFile.getStorage().isEmpty()){
                lastFiles = loadFile.loadStorage(path.substring(0, path.lastIndexOf('/')));
            } else {
                MediaFiles file1 = loadFile.getStorage().get(0);
                Log.e("file1Parent--->", new File(file1.getFilePath()).getParent());
                Log.e("root--->", Environment.getExternalStorageDirectory() + "/");
                if (new File(file1.getFilePath()).getParent()
                        .equals(Environment.getExternalStorageDirectory() + "")) {
                    if(edit.getVisibility() == View.VISIBLE){
                        if(hasChoseAll){
                            for (int i = 0; i < loadFile.getStorage().size(); i++) {
                                loadFile.getStorage().get(i).count = 0;
                            }
                        } else {
                            for (int i = 0; i < choseFiles.size(); i++) {
                                choseFiles.get(i).count = 0;
                            }
                        }
                        storageAdapter.notifyDataSetChanged();
                        edit.setVisibility(View.GONE);
                    } else {
                        finish();
                    }
                    return true;
                } else {
                    lastFiles = loadFile.getLastFile(loadFile.getStorage().get(0));
                }
            }
            Collections.sort(lastFiles, new Comparator<MediaFiles>() {
                @Override
                public int compare(MediaFiles lhs, MediaFiles rhs) {
                    return lhs.getFileName().compareTo(rhs.getFileName());
                }
            });
            storageAdapter = new StorageAdapter(getApplicationContext(),
                    lastFiles, loadFile, choseFiles, edit);
            listView.setAdapter(storageAdapter);
            listView.setSelection(pos);
            listView.setOnItemClickListener(new ListListener());
        }
        return false;
    }

    public void setListener(){
        back.setOnClickListener(new StorageListener());
        copy.setOnClickListener(new StorageListener());
        move.setOnClickListener(new StorageListener());
        share.setOnClickListener(new StorageListener());
        delete.setOnClickListener(new StorageListener());
        chooseAll.setOnClickListener(new StorageListener());
        listView.setOnItemClickListener(new ListListener());
    }

    public void initView(){
        back = (LinearLayout) findViewById(R.id.back);
        TextView fileName = (TextView) findViewById(R.id.fileName);
        switch (getIntent().getStringExtra("storage")) {
            case "in":
                fileName.setText(R.string.storage_in);
                style = "in";
                break;
            case "out":
                fileName.setText(R.string.storage_out);
                style = "out";
                break;
        }
        edit = (LinearLayout) findViewById(R.id.edit);
        copy = (LinearLayout) findViewById(R.id.copy);
        move = (LinearLayout) findViewById(R.id.move);
        share = (LinearLayout) findViewById(R.id.share);
        delete = (LinearLayout) findViewById(R.id.delete);
        chooseAll = (LinearLayout) findViewById(R.id.chooseAll);
        listView = (ListView) findViewById(R.id.fileList);
        loadFile = new LoadFile(StorageActivity.this);
        choseFiles = new ArrayList<>();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        popupWindow = new PopupWindow(StorageActivity.this);
        popupWindow.setWidth((int) (displayMetrics.widthPixels * 0.9));
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.share_style);
        progress_background = (RelativeLayout) findViewById(R.id.progress_background);
        circleProgress = (CircleProgress) findViewById(R.id.progress);
        fileCount = (TextView) findViewById(R.id.fileCount);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("传输中...");
        progressDialog.setCanceledOnTouchOutside(false);
        app = (App) getApplication();
    }
}
