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
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.manager.Adapter.FileAdapter;
import com.example.manager.Application.App;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;
import com.example.manager.Thread.SendFile;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.Utils.LoadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dangelo on 2016/4/5.
 */
public class WordActivity extends Activity {

    private App app;
    private ListView listView;
    private LinearLayout back;
    private LinearLayout search;
    private LoadFile loadFile;
    private List<MediaFiles> wordList;
    private LinearLayout edit;
    private LinearLayout copy;
    private LinearLayout move;
    private LinearLayout share;
    private LinearLayout delete;
    private LinearLayout chooseAll;
    private RelativeLayout no_files_image;
    private RelativeLayout no_files_text;
    private ProgressDialog progressDialog;
    private WordHandler wordHandler;
    private FileAdapter fileAdapter;
    private String style;
    public  static List<MediaFiles> choseFiles;
    private boolean hasChoseAll = false;
    private int count = 0;
    public  static List<String> newPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getIntent().getStringExtra("style")){
            case "文档" :
                ActionBarUtil.initActionBar(getActionBar(), "文档", 0x222);
                style = getResources().getString(R.string.word);
                break;
            case "压缩包" :
                ActionBarUtil.initActionBar(getActionBar(), "压缩包", 0x222);
                style = getResources().getString(R.string.zip);
                break;
            case "安装包" :
                ActionBarUtil.initActionBar(getActionBar(), "安装包", 0x222);
                style = getResources().getString(R.string.apk);
                break;
        }
        setContentView(R.layout.filelist);
        initView();
        setListener();
        progressDialog.setMessage("加载中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        WordThread wordThread = new WordThread(style);
        Thread thread = new Thread(wordThread, "wordThread");
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(edit.getVisibility() == View.VISIBLE){
            if(hasChoseAll){
                for (int i = 0; i < wordList.size(); i++) {
                    wordList.get(i).count = 0;
                    if(newPath.size() != 0){
                        wordList.get(i).setFilePath(newPath.get(i));
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
            fileAdapter.notifyDataSetChanged();
            edit.setVisibility(View.GONE);
        }
    }

    public class WordThread implements Runnable{

        private String style;

        public WordThread(String style) {
            this.style = style;
        }

        @Override
        public void run() {
            switch (style){
                case "文档" :
                    wordList = loadFile.loadWord(getContentResolver());
                    wordHandler.sendEmptyMessage(0x000);
                    break;
                case "压缩包" :
                    wordList = loadFile.loadZip(getContentResolver());
                    wordHandler.sendEmptyMessage(0x111);
                    break;
                case "安装包" :
                    wordList = loadFile.loadApk(getContentResolver());
                    wordHandler.sendEmptyMessage(0x222);
                    break;
            }
        }
    }

    public class WordHandler extends Handler{
        public void handleMessage(Message msg){
            progressDialog.cancel();
            switch (msg.what) {
                case 0x000 :
                    if (!wordList.isEmpty()) {
                        fileAdapter = new FileAdapter(getApplicationContext(), loadFile, wordList, choseFiles, edit);
                        listView.setAdapter(fileAdapter);
                    } else {
                        loadFile.addView(no_files_image, no_files_text, R.drawable.word);
                    }
                    break;
                case 0x111 :
                    if (!wordList.isEmpty()) {
                        fileAdapter = new FileAdapter(getApplicationContext(), loadFile, wordList, choseFiles, edit);
                        listView.setAdapter(fileAdapter);
                    } else {
                        loadFile.addView(no_files_image, no_files_text, R.drawable.zip);
                    }
                    break;
                case 0x222 :
                    if (!wordList.isEmpty()) {
                        fileAdapter = new FileAdapter(getApplicationContext(), loadFile, wordList, choseFiles, edit);
                        listView.setAdapter(fileAdapter);
                    } else {
                        loadFile.addView(no_files_image, no_files_text, R.drawable.apk);
                    }
                    break;
                case 0x333 :
                    progressDialog.dismiss();
                    app.getUser().connected = false;
                    Toast.makeText(WordActivity.this, "连接失败，请重新连接", Toast.LENGTH_SHORT).show();
                    break;
                case 0x001 :
                    count ++;
                    if(count < choseFiles.size()) {
                        File file = new File(choseFiles.get(count).getFilePath());
                        SendFile ft = new SendFile(app.getUser().socket, app.getUser().IP, app.getUser().port, file, wordHandler);
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
                            fileAdapter.notifyDataSetChanged();
                            edit.setVisibility(View.GONE);
                        }
                        Toast.makeText(WordActivity.this, "传输完成!", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    public class WordListener implements View.OnClickListener{

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
                    intent.setClass(WordActivity.this, OperateActivity.class);
                    intent.putExtra("style", R.string.word);
                    intent.putExtra("operation", "copy");
                    startActivity(intent);
                    break;
                case R.id.move :
                    intent.setClass(WordActivity.this, OperateActivity.class);
                    intent.putExtra("style", R.string.word);
                    intent.putExtra("operation", "move");
                    startActivity(intent);
                    break;
                case R.id.share :
                    if(app.getUser().connected) {
                        progressDialog.setMessage("传输中...");
                        progressDialog.show();
                        File file = new File(choseFiles.get(0).getFilePath());
                        SendFile ft = new SendFile(app.getUser().socket, app.getUser().IP, app.getUser().port, file, wordHandler);
                        Thread t = new Thread(ft, "SendFile");
                        t.start();
                    } else {
                        Toast.makeText(WordActivity.this, "设备未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.delete :
                    AlertDialog.Builder dialog = new AlertDialog.Builder(WordActivity.this);
                    dialog.setTitle("提示").setMessage("确认删除").setPositiveButton("确认", new DialogListener())
                            .setNegativeButton("取消", new DialogListener()).create().show();
                    break;
                case R.id.chooseAll :
                    MediaFiles file;
                    if(!hasChoseAll) {
                        for (int i = 0; i < wordList.size(); i++) {
                            file = wordList.get(i);
                            if (file.count == 0) {
                                file.count = 1;
                                if (file.checkBox != null) {
                                    file.checkBox.setChecked(true, true);
                                }
                            }
                            if (i == wordList.size() - 1) {
                                hasChoseAll = true;
                            }
                        }
                        choseFiles = wordList;
                    } else {
                        for (int i = 0; i < wordList.size(); i++) {
                            file = wordList.get(i);
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

    public class WordListListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            MediaFiles mediaFiles = wordList.get(position);
//            if(mediaFiles.count == 1){
//                mediaFiles.count = 0;
//                mediaFiles.checkBox.setChecked(false);
//                choseFiles.remove(mediaFiles);
//                int aChoose;
//                for (aChoose = 0; aChoose < wordList.size(); aChoose ++) {
//                    if (wordList.get(aChoose).count == 1) {
//                        break;
//                    }
//                }
//                if(aChoose == wordList.size()){
//                    edit.setVisibility(View.GONE);
//                }
//            } else {
//                mediaFiles.count = 1;
//                mediaFiles.checkBox.setChecked(true);
//                choseFiles.add(mediaFiles);
//                edit.setVisibility(View.VISIBLE);
//            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = uri = Uri.parse("file://" + wordList.get(position).getFilePath());;
            switch (style){
                case "文档" :
                    intent.setDataAndType(uri, "text/*");
                    break;
                case "压缩包" :
                    String path = wordList.get(position).getFilePath();
                    String suffix = path.substring(path.lastIndexOf('.') + 1);
                    if(suffix.equals("zip")){
                        intent.setDataAndType(uri, "application/zip");
                    } else if(suffix.equals("rar")) {
                        intent.setDataAndType(uri, "application/x-rar-compressed");
                    }
                    break;
                case "安装包" :
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    break;
            }
            startActivity(intent);
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
                        wordList.clear();
                    } else {
                        wordList.removeAll(choseFiles);
                        choseFiles.clear();
                    }
                    fileAdapter.notifyDataSetChanged();
                    Toast.makeText(WordActivity.this, "删除完成", Toast.LENGTH_SHORT).show();
                    edit.setVisibility(View.GONE);
                    break;
                case -2 :
                    dialog.dismiss();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(edit.getVisibility() == View.VISIBLE){
            if(hasChoseAll){
                for (int i = 0; i < wordList.size(); i++) {
                    wordList.get(i).count = 0;
                }
            } else {
                for (int i = 0; i < choseFiles.size(); i++) {
                    choseFiles.get(i).count = 0;
                }
            }
            fileAdapter.notifyDataSetChanged();
            edit.setVisibility(View.GONE);
        } else {
            finish();
        }
        return true;
    }

    public void setListener(){
        back.setOnClickListener(new WordListener());
        search.setOnClickListener(new WordListener());
        copy.setOnClickListener(new WordListener());
        move.setOnClickListener(new WordListener());
        share.setOnClickListener(new WordListener());
        delete.setOnClickListener(new WordListener());
        chooseAll.setOnClickListener(new WordListener());
        listView.setOnItemClickListener(new WordListListener());
    }

    public void initView(){
        listView = (ListView) findViewById(R.id.fileList);
        back = (LinearLayout) findViewById(R.id.back);
        search = (LinearLayout) findViewById(R.id.search);
        edit = (LinearLayout) findViewById(R.id.edit);
        copy = (LinearLayout) findViewById(R.id.copy);
        move = (LinearLayout) findViewById(R.id.move);
        share = (LinearLayout) findViewById(R.id.share);
        delete = (LinearLayout) findViewById(R.id.delete);
        chooseAll = (LinearLayout) findViewById(R.id.chooseAll);
        no_files_image = (RelativeLayout) findViewById(R.id.no_files_image);
        no_files_text = (RelativeLayout) findViewById(R.id.no_files_text);
        loadFile = new LoadFile(WordActivity.this);
        progressDialog = new ProgressDialog(WordActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        wordHandler = new WordHandler();
        choseFiles = new ArrayList<>();
        app = (App)getApplication();
    }

}