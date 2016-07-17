package com.example.manager.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.Class.MediaFiles;
import com.example.manager.R;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.Utils.LoadFile;
import com.example.manager.Utils.StorageSize;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2016/4/25.
 */
public class OperateFile extends Activity {

    private ListView copyList;
    private Button copy;
    private Button cancel;
    private LoadFile loadFile;
    private String path;
    private String operation;
    private List<MediaFiles> choseFiles;
    private ProgressDialog progressDialog;
    private OperateHandler operateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtil.initActionBar(getActionBar(), "选择粘贴位置", 0x333);
        setContentView(R.layout.copyfile);
        initView();
        setListener();
        loadFile.loadStorage(Environment.getExternalStorageDirectory() + "/");
        CopyAdapter copyAdapter = new CopyAdapter(getApplicationContext(), loadFile.getStorage());
        copyList.setAdapter(copyAdapter);
        Intent intent;
        if((intent = getIntent()) != null) {
            switch (intent.getIntExtra("style", 0)) {
                case R.string.music :
                    choseFiles = MusicList.choseFiles;
                    operation = intent.getStringExtra("operation");
                    break;
                case R.string.video :
                    choseFiles = VideoList.choseFiles;
                    operation = intent.getStringExtra("operation");
                    break;
                case R.string.image :
                    choseFiles = ImageList.choseFiles;
                    operation = intent.getStringExtra("operation");
                    break;
                case R.string.word :
                    choseFiles = WordList.choseFiles;
                    operation = intent.getStringExtra("operation");
                    break;
                case R.string.storage_in :
                    choseFiles = Storage.choseFiles;
                    operation = intent.getStringExtra("operation");
                    break;
            }
        }
    }

    public class CopyAdapter extends BaseAdapter{

        public LayoutInflater inflater;
        public List list;

        public CopyAdapter(Context context, List list){
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
                viewHolder.check = (ImageView) relative.findViewById(R.id.checkBox);
                viewHolder.check.setVisibility(View.INVISIBLE);
                relative.setTag(viewHolder);
            } else {
                relative = (RelativeLayout)convertView;
                viewHolder =(ViewHolder)relative.getTag();
            }
            MediaFiles file = (MediaFiles)list.get(position);
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

            return relative;
        }
        class ViewHolder{
            ImageView fileImage;
            TextView fileName;
            TextView fileSize;
            ImageView check;
        }
    }

    public class ListListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaFiles file = loadFile.getStorage().get(position);
            if(!file.isFile){
                MediaFiles files = loadFile.getStorage().get(0);
                path = files.getFilePath();         //保存当前第一个元素的路径
                loadFile.getStorage().clear();
                loadFile.loadStorage(file.getFilePath() + "/");
                CopyAdapter copyAdapter = new CopyAdapter(getApplicationContext(), loadFile.getStorage());
                copyList.setAdapter(copyAdapter);
                copyList.setOnItemClickListener(new ListListener());
            }
        }
    }

    public class CopyListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.copy :
                    MediaFiles file;
                    progressDialog.show();
                    for (int i = 0; i < choseFiles.size(); i++) {
                        file = choseFiles.get(i);
                        if (loadFile.getStorage().isEmpty()) {
                            if(operation.equals("copy")) {
                                CopyThread ct = new CopyThread(file.getFilePath(), path);
                                Thread t = new Thread(ct, "OperateThread");
                                t.start();
                            } else {
                                MoveThread mt = new MoveThread(file.getFilePath(), path);
                                Thread t = new Thread(mt, "MoveThread");
                                t.start();
                            }
                        } else {
                            File file1 = new File(loadFile.getStorage().get(0).getFilePath());
                            if (file1.getParent().equals("/storage/emulated/0/0")) {
                                Toast.makeText(OperateFile.this, "操作失败, 没有权限", Toast.LENGTH_SHORT).show();
                            }
                            if(operation.equals("copy")) {
                                CopyThread ct = new CopyThread(file.getFilePath(), file1.getPath());
                                Thread t = new Thread(ct, "OperateThread");
                                t.start();
                            } else {
                                MoveThread mt = new MoveThread(file.getFilePath(), file1.getPath());
                                Thread t = new Thread(mt, "MoveThread");
                                t.start();
                            }
                        }
                    }
                    break;
                case R.id.cancel :
                    finish();
                    break;
            }
        }
    }

    public class CopyThread implements Runnable{

        private String sourcesPath;
        private String targetPath;
        private int result ;

        public CopyThread(String sourcesPath, String targetPath){
            this.sourcesPath = sourcesPath;
            this.targetPath = targetPath;
        }

        @Override
        public void run() {
            result = loadFile.copyFiles(sourcesPath, targetPath);
            if(result == 0){
                operateHandler.sendEmptyMessage(0x000);
            } else if(result == 1){
                operateHandler.sendEmptyMessage(0x111);
            } else {
                operateHandler.sendEmptyMessage(0x222);
            }
        }
    }

    public class MoveThread implements Runnable{

        private String sourcesPath;
        private String targetPath;
        private int result ;

        public MoveThread(String sourcesPath, String targetPath){
            this.sourcesPath = sourcesPath;
            this.targetPath = targetPath;
        }

        @Override
        public void run() {
            result = loadFile.moveFile(sourcesPath, targetPath);
            if(result == 0){
                operateHandler.sendEmptyMessage(0x000);
            } else if(result == 1){
                operateHandler.sendEmptyMessage(0x111);
            } else {
                operateHandler.sendEmptyMessage(0x222);
            }
        }
    }

    public class OperateHandler extends Handler{
        public void handleMessage(Message msg){
            progressDialog.dismiss();
            switch (msg.what){
                case 0x000 :
                    Toast.makeText(OperateFile.this, "操作失败，文件已存在！", Toast.LENGTH_SHORT).show();
                    break;
                case 0x111 :
                    Toast.makeText(OperateFile.this, "操作完成!", Toast.LENGTH_SHORT).show();
                    break;
                case 0x222 :
                    Toast.makeText(OperateFile.this, "操作失败，没有权限!", Toast.LENGTH_SHORT).show();
                    break;
            }
            finish();
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
                MediaFiles file2 = new LoadFile(OperateFile.this).loadStorage(Environment.getExternalStorageDirectory() + "/").get(0);
                if (file1.getFilePath().equals(file2.getFilePath())) {
                    finish();
                    return true;
                } else {
                    lastFiles = loadFile.getLastFile(loadFile.getStorage().get(0));
                }
            }
            CopyAdapter adapter = new CopyAdapter(getApplicationContext(), lastFiles);
            copyList.setAdapter(adapter);
            copyList.setOnItemClickListener(new ListListener());
        }
        return true;
    }

    public void setListener(){
        copy.setOnClickListener(new CopyListener());
        cancel.setOnClickListener(new CopyListener());
        copyList.setOnItemClickListener(new ListListener());
    }

    public void initView(){
        copyList = (ListView) findViewById(R.id.copyList);
        copy = (Button) findViewById(R.id.copy);
        cancel = (Button) findViewById(R.id.cancel);
        loadFile = new LoadFile(OperateFile.this);
        choseFiles = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在复制...");
        progressDialog.setCanceledOnTouchOutside(false);
        operateHandler = new OperateHandler();
    }

}
