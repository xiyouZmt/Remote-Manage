package com.zmt.manager.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zmt.manager.Adapter.OperateAdapter;
import com.zmt.manager.Model.MediaFiles;
import com.zmt.manager.R;
import com.zmt.manager.Utils.LoadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Android on 2016/4/25.
 */
public class OperateActivity extends Activity {

    private LinearLayout back;
    private ListView operateList;
    private Button paste;
    private Button cancel;
    private LoadFile loadFile;
    private String path;
    private String operation;
    private List<MediaFiles> choseFiles;
    private ProgressDialog progressDialog;
    private OperateHandler operateHandler;
    private OperateAdapter operateAdapter;
    private int style = 0;
    private String newPath = "";
    private String targetPath = "";
    private MediaFiles file;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.operate_file);
        initView();
        setListener();
        loadFile.loadStorage(Environment.getExternalStorageDirectory() + "/");
        Collections.sort(loadFile.getStorage(), new Comparator<MediaFiles>() {
            @Override
            public int compare(MediaFiles lhs, MediaFiles rhs) {
                return lhs.getFileName().compareTo(rhs.getFileName());
            }
        });
        operateAdapter = new OperateAdapter(getApplicationContext(), loadFile.getStorage());
        operateList.setAdapter(operateAdapter);
        Intent intent;
        if((intent = getIntent()) != null) {
            operation = intent.getStringExtra("operation");
            if(operation.equals("move")){
                progressDialog.setMessage("正在移动...");
            }
            switch ((style = intent.getIntExtra("style", 0))) {
                case R.string.music :
                    choseFiles = MusicActivity.choseFiles;
                    break;
                case R.string.video :
                    choseFiles = VideoActivity.choseFiles;
                    break;
                case R.string.image :
                    choseFiles = ImageActivity.choseFiles;
                    break;
                case R.string.word :
                    choseFiles = WordActivity.choseFiles;
                    break;
                case R.string.storage_in :
                    choseFiles = StorageActivity.choseFiles;
                    break;
            }
        }
    }

    public class OperateItemListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaFiles file = loadFile.getStorage().get(position);
            if(!file.isFile){
                MediaFiles files = loadFile.getStorage().get(0);
                path = files.getFilePath();         //保存当前第一个元素的路径
                loadFile.getStorage().clear();
                loadFile.loadStorage(file.getFilePath() + "/");
                operateAdapter = new OperateAdapter(getApplicationContext(), loadFile.getStorage());
                operateList.setAdapter(operateAdapter);
                operateList.setOnItemClickListener(new OperateItemListener());
            }
        }
    }

    public class OperateListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.paste :
                    progressDialog.show();
                    file = choseFiles.get(count);
                    Log.e("filePath--->", file.getFilePath());
                    int index = file.getFilePath().lastIndexOf('/');
                    if (loadFile.getStorage().isEmpty()) {
                        targetPath = new File(path).getParent() + '/';
                        if(operation.equals("copy")) {
                            CopyThread ct = new CopyThread(file.getFilePath(), targetPath);
                            Thread t = new Thread(ct, "OperateThread");
                            t.start();
                        } else {

                            newPath = new File(path).getParent() + '/' + file.getFilePath().substring(index + 1);
                            MoveThread mt = new MoveThread(file.getFilePath(), targetPath);
                            Thread t = new Thread(mt, "MoveThread");
                            t.start();
                        }
                    } else {
                        File file1 = new File(loadFile.getStorage().get(0).getFilePath());
                        targetPath = file1.getParent() + '/';
                        if (file1.getParent().equals("/storage/emulated/0/0")) {
                            Toast.makeText(OperateActivity.this, "操作失败, 没有权限", Toast.LENGTH_SHORT).show();
                        }
                        if(operation.equals("copy")) {
                            CopyThread ct = new CopyThread(file.getFilePath(), targetPath);
                            Thread t = new Thread(ct, "OperateThread");
                            t.start();
                        } else {
                            newPath = file1.getParent() + '/' + file.getFilePath().substring(index + 1);
                            MoveThread mt = new MoveThread(file.getFilePath(), targetPath);
                            Thread t = new Thread(mt, "MoveThread");
                            t.start();
                        }
                    }
                    break;
                case R.id.paste_cancel :
                case R.id.back :
                    finish();
                    break;
            }
        }
    }

    public class CopyThread implements Runnable{

        private String sourcesPath;
        private String targetPath;
        private int result;
        private Message msg;

        public CopyThread(String sourcesPath, String targetPath){
            this.sourcesPath = sourcesPath;
            this.targetPath = targetPath;
            msg = new Message();
        }

        @Override
        public void run() {
            result = loadFile.copyFiles(sourcesPath, targetPath);
            if(result == 0){
                msg.obj = "copy failed";
            } else if(result == 1){
                msg.obj = "copy succeed";
            } else {
                msg.obj = "no permission";
            }
            operateHandler.sendMessage(msg);
        }
    }

    public class MoveThread implements Runnable{

        private String sourcesPath;
        private String targetPath;
        private Message msg;
        private boolean move;

        public MoveThread(String sourcesPath, String targetPath){
            this.sourcesPath = sourcesPath;
            this.targetPath = targetPath;
            msg = new Message();
        }

        @Override
        public void run() {
            move = loadFile.moveFile(sourcesPath, targetPath);
            if(move){
                msg.obj = "move succeed";
                if(style != R.string.storage_in){
                    MusicActivity.newPath.add(newPath);
                    VideoActivity.newPath.add(newPath);
                    ImageActivity.newPath.add(newPath);
                    WordActivity.newPath.add(newPath);
                }
            } else {
                msg.obj = "move failed";
            }
            operateHandler.sendMessage(msg);
        }
    }

    public class OperateHandler extends Handler{
        public void handleMessage(Message msg){
            count ++;
            if(count < choseFiles.size()){
                file = choseFiles.get(count);
                if(operation.equals("copy")){
                    CopyThread ct = new CopyThread(file.getFilePath(), targetPath);
                    Thread t = new Thread(ct, "OperateThread");
                    t.start();
                } else if(operation.equals("move")){
                    MoveThread mt = new MoveThread(file.getFilePath(), targetPath);
                    Thread t = new Thread(mt, "MoveThread");
                    t.start();
                }
            } else {
                progressDialog.dismiss();
                switch (msg.obj.toString()){
                    case "move failed" :
                        Toast.makeText(OperateActivity.this, file.getFileName() + "已存在!", Toast.LENGTH_SHORT).show();
                        break;
                    case "move succeed" :
                        StorageActivity.TAG = msg.obj.toString();
                        Toast.makeText(OperateActivity.this, "移动完成!", Toast.LENGTH_SHORT).show();
                        break;
                    case "copy failed" :
                        Toast.makeText(OperateActivity.this, file.getFileName() + "已存在!", Toast.LENGTH_SHORT).show();
                        break;
                    case "copy succeed" :
                        Toast.makeText(OperateActivity.this, "复制完成!", Toast.LENGTH_SHORT).show();
                        break;
                    case "no permission" :
                        Toast.makeText(OperateActivity.this, "操作失败，没有权限!", Toast.LENGTH_SHORT).show();
                        break;
                }
                count = 0;
                finish();
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
                if (new File(file1.getFilePath()).getParent()
                        .equals(Environment.getExternalStorageDirectory() + "")) {
                    finish();
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
            operateAdapter = new OperateAdapter(getApplicationContext(), lastFiles);
            operateList.setAdapter(operateAdapter);
            operateList.setOnItemClickListener(new OperateItemListener());
        }
        return true;
    }

    public void setListener(){
        back.setOnClickListener(new OperateListener());
        paste.setOnClickListener(new OperateListener());
        cancel.setOnClickListener(new OperateListener());
        operateList.setOnItemClickListener(new OperateItemListener());
    }

    public void initView(){
        back = (LinearLayout) findViewById(R.id.back);
        TextView fileName = (TextView) findViewById(R.id.fileName);
        fileName.setText("选择粘贴位置");
        operateList = (ListView) findViewById(R.id.operateList);
        paste = (Button) findViewById(R.id.paste);
        cancel = (Button) findViewById(R.id.paste_cancel);
        loadFile = new LoadFile(OperateActivity.this);
        choseFiles = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在复制...");
        progressDialog.setCancelable(false);
        operateHandler = new OperateHandler();
    }
}
