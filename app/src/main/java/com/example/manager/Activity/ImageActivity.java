package com.example.manager.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.Adapter.ImageAdapter;
import com.example.manager.Application.App;
import com.example.manager.Model.ImageFolder;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;
import com.example.manager.Thread.SendFile;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.Utils.LoadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2016/4/16.
 */
public class ImageActivity extends Activity {

    private App app;
    private GridView gridView;
    private LinearLayout back;
    private LinearLayout copy;
    private LinearLayout move;
    private LinearLayout share;
    private LinearLayout delete;
    private LinearLayout chooseAll;
    private LoadFile loadFile;
    private LinearLayout edit;
    private ImageHandler imageHandler;
    private ProgressDialog progressDialog;
    private ImageAdapter imageAdapter;
    private int count = 0;
    private boolean hasChoseAll = false;
    public  static List<MediaFiles> choseFiles;
    public  static List<String> newPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        ActionBarUtil.initActionBar(getActionBar(), getIntent().getStringExtra("folderName"), 0x222);
        setContentView(R.layout.imagelayout);
        initView();
        setListener();
        Intent intent;
        if((intent = getIntent()) != null){
            int position = intent.getIntExtra("position", 0);
            ImageFolder imageFolder = FolderActivity.loadFile.getFolderList().get(position);
            loadFile = new LoadFile(ImageActivity.this);
            loadFile.getImage(imageFolder.getFolderPath());
            Log.e("storageList size : ", String.valueOf(loadFile.getStorage().size()));
            imageAdapter = new ImageAdapter(getApplicationContext(), loadFile.getImageList(), choseFiles, edit);
            gridView.setAdapter(imageAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(edit.getVisibility() == View.VISIBLE){
            if(hasChoseAll){
                for (int i = 0; i < loadFile.getImageList().size(); i++) {
                    loadFile.getImageList().get(i).count = 0;
                    if(newPath.size() != 0){
                        loadFile.getImageList().get(i).setFilePath(newPath.get(i));
                    }
                }
            } else {
                for (int i = 0; i < choseFiles.size(); i++) {
                    choseFiles.get(i).count = 0;
                    if(newPath.size() != 0){
                        choseFiles.get(i).setFilePath(newPath.get(i));
                    }
                }
            }
            newPath.clear();
            choseFiles.clear();
            imageAdapter.notifyDataSetChanged();
            edit.setVisibility(View.GONE);
        }
    }

    public class ImageItemListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("file://" + loadFile.getImageList().get(position).getFilePath());
            intent.setDataAndType(uri, "image/*");
            startActivity(intent);
        }
    }

    public class ImageListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent= new Intent();
            switch (v.getId()){
                case R.id.back :
                    finish();
                    break;
                case R.id.search :

                    break;
                case R.id.copy :
                    intent.setClass(ImageActivity.this, OperateActivity.class);
                    intent.putExtra("style", R.string.image);
                    intent.putExtra("operation", "copy");
                    startActivity(intent);
                    break;
                case R.id.move :
                    intent.setClass(ImageActivity.this, OperateActivity.class);
                    intent.putExtra("style", R.string.image);
                    intent.putExtra("operation", "move");
                    startActivity(intent);
                    break;
                case R.id.share :
                    if(app.getUser().connected) {
                        progressDialog.show();
                        File file = new File(choseFiles.get(0).getFilePath());
                        SendFile ft = new SendFile(app.getUser().socket, app.getUser().IP, app.getUser().port, file, imageHandler);
                        Thread t = new Thread(ft, "SendFile");
                        t.start();
                    } else {
                        Toast.makeText(ImageActivity.this, "设备未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.delete :
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ImageActivity.this);
                    dialog.setTitle("提示").setMessage("确认删除").setPositiveButton("确认", new DialogListener())
                            .setNegativeButton("取消", new DialogListener()).create().show();
                    break;
                case R.id.chooseAll :
                    MediaFiles file;
                    if(!hasChoseAll) {
                        for (int i = 0; i < loadFile.getImageList().size(); i++) {
                            file = loadFile.getImageList().get(i);
                            if (file.count == 0) {
                                file.count = 1;
                                if (file.checkBox != null) {
                                    file.checkBox.setChecked(true, true);
                                }
                            }
                            if (i == loadFile.getImageList().size() - 1) {
                                hasChoseAll = true;
                            }
                        }
                        choseFiles = loadFile.getImageList();
                    } else {
                        for (int i = 0; i < loadFile.getImageList().size(); i++) {
                            file = loadFile.getImageList().get(i);
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
                    for (int i = 0; i < choseFiles.size(); i++) {
                        File file = new File(choseFiles.get(i).getFilePath());
                        loadFile.deleteFile(file);
                    }
                    if(hasChoseAll){
                        loadFile.getImageList().clear();
                    } else {
                        loadFile.getImageList().removeAll(choseFiles);
                        choseFiles.clear();
                    }
                    if(loadFile.getImageList().size() == 0){
                        finish();
                    } else {
                        imageAdapter.notifyDataSetChanged();
                        Toast.makeText(ImageActivity.this, "删除完成", Toast.LENGTH_SHORT).show();
                        edit.setVisibility(View.GONE);
                    }
                    break;
                case -2 :
                    dialog.dismiss();
                    break;
            }
        }
    }

    public class ImageHandler extends Handler{
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x001 :
                    count ++;
                    if(count < choseFiles.size()) {
                        File file = new File(choseFiles.get(count).getFilePath());
                        SendFile ft = new SendFile(app.getUser().socket, app.getUser().IP, app.getUser().port, file, imageHandler);
                        Thread t = new Thread(ft, "SendFile");
                        t.start();
                    } else {
                        progressDialog.dismiss();
                        count = 0;
                        if(edit.getVisibility() == View.VISIBLE){
                            if(hasChoseAll){
                                for (int i = 0; i < loadFile.getMusicList().size(); i++) {
                                    loadFile.getMusicList().get(i).count = 0;
                                }
                            } else {
                                for (int i = 0; i < choseFiles.size(); i++) {
                                    choseFiles.get(i).count = 0;
                                }
                            }
                            choseFiles.clear();
                            imageAdapter.notifyDataSetChanged();
                            edit.setVisibility(View.GONE);
                        }
                        Toast.makeText(ImageActivity.this, "传输完成!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x333 :
                    progressDialog.dismiss();
                    app.getUser().connected = false;
                    Toast.makeText(ImageActivity.this, "连接失败，请重新连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(edit.getVisibility() == View.VISIBLE){
            if(hasChoseAll){
                for (int i = 0; i < loadFile.getImageList().size(); i++) {
                    loadFile.getImageList().get(i).count = 0;
                }
            } else {
                for (int i = 0; i < choseFiles.size(); i++) {
                    choseFiles.get(i).count = 0;
                }
            }
            imageAdapter.notifyDataSetChanged();
            edit.setVisibility(View.GONE);
        } else {
            finish();
        }
        return true;
    }

    public void setListener(){
        back.setOnClickListener(new ImageListener());
        copy.setOnClickListener(new ImageListener());
        move.setOnClickListener(new ImageListener());
        share.setOnClickListener(new ImageListener());
        delete.setOnClickListener(new ImageListener());
        chooseAll.setOnClickListener(new ImageListener());
        gridView.setOnItemClickListener(new ImageItemListener());
    }

    public void initView(){
        back = (LinearLayout) findViewById(R.id.back);
        TextView fileName = (TextView) findViewById(R.id.fileName);
        fileName.setText(getIntent().getStringExtra("folderName"));
        gridView = (GridView) findViewById(R.id.gridView);
        edit = (LinearLayout) findViewById(R.id.edit);
        copy = (LinearLayout) findViewById(R.id.copy);
        move = (LinearLayout) findViewById(R.id.move);
        share = (LinearLayout) findViewById(R.id.share);
        delete = (LinearLayout) findViewById(R.id.delete);
        chooseAll = (LinearLayout) findViewById(R.id.chooseAll);
        choseFiles = new ArrayList<>();
        imageHandler = new ImageHandler();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("传输中...");
        progressDialog.setCanceledOnTouchOutside(false);
        app = (App) getApplication();
    }
}
