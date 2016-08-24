package com.example.manager.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.Adapter.FileAdapter;
import com.example.manager.Application.App;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;
import com.example.manager.Thread.SendFile;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.Utils.CircleProgress;
import com.example.manager.Utils.LoadFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    private LinearLayout cancel;
    private RelativeLayout relative_search;
    private EditText editText;
    private LoadFile loadFile;
    private List<MediaFiles> wordList;
    private List<MediaFiles> searchList;
    private LinearLayout edit;
    private LinearLayout copy;
    private LinearLayout move;
    private LinearLayout share;
    private LinearLayout delete;
    private LinearLayout chooseAll;
    private RelativeLayout no_files_image;
    private RelativeLayout no_files_text;
    private PopupWindow popupWindow;
    private RelativeLayout progress_background;
    private CircleProgress circleProgress;
    private TextView fileCount;
    private ProgressDialog progressDialog;
    private FileAdapter fileAdapter;
    private String style;
    public  static List<MediaFiles> choseFiles;
    private boolean isSearching = false;
    private boolean hasChoseAll = false;
    private int count = 0;
    private int max = 0;
    public  static List<String> newPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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

    private Handler wordHandler = new Handler(){
        public void handleMessage(Message msg){
            progressDialog.cancel();
            switch (msg.what) {
                case 0x011 :
                    if (!wordList.isEmpty()) {
                        fileAdapter = new FileAdapter(getApplicationContext(), wordList, choseFiles, edit);
                        listView.setAdapter(fileAdapter);
                    } else {
                        loadFile.addView(no_files_image, no_files_text, R.drawable.word);
                    }
                    break;
                case 0x111 :
                    if (!wordList.isEmpty()) {
                        fileAdapter = new FileAdapter(getApplicationContext(), wordList, choseFiles, edit);
                        listView.setAdapter(fileAdapter);
                    } else {
                        loadFile.addView(no_files_image, no_files_text, R.drawable.zip);
                    }
                    break;
                case 0x222 :
                    if (!wordList.isEmpty()) {
                        fileAdapter = new FileAdapter(getApplicationContext(), wordList, choseFiles, edit);
                        listView.setAdapter(fileAdapter);
                    } else {
                        loadFile.addView(no_files_image, no_files_text, R.drawable.apk);
                    }
                    break;
                case 0x000 :
                    String percentage = String.valueOf(msg.arg1 * 100 / max);
                    circleProgress.setProgress(circleProgress.getProgress() + Integer.parseInt(percentage));
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
                        SendFile ft = new SendFile(app.getUser().socket, app.getUser().IP, app.getUser().port, file, wordHandler);
                        Thread t = new Thread(ft, "SendFile");
                        t.start();
                    } else {
                        progress_background.setVisibility(View.GONE);
                        count = 0;
                        if(hasChoseAll){
                            for (int i = 0; i < wordList.size(); i++) {
                                wordList.get(i).count = 0;
                            }
                        } else {
                            for (int i = 0; i < choseFiles.size(); i++) {
                                choseFiles.get(i).count = 0;
                            }
                        }
                        choseFiles.clear();
                        fileAdapter.notifyDataSetChanged();
                        circleProgress.setProgress(0);
                        Toast.makeText(WordActivity.this, "传输完成!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x333 :
                    progressDialog.dismiss();
                    app.getUser().connected = false;
                    circleProgress.setProgress(0);
                    Toast.makeText(WordActivity.this, "连接失败，请重新连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public class WordListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()){
                case R.id.back :
                    finish();
                    break;
                case R.id.search :
                    search.setVisibility(View.GONE);
                    relative_search.setVisibility(View.VISIBLE);
                    isSearching = true;
                    break;
                case R.id.cancel :
                    relative_search.setVisibility(View.GONE);
                    search.setVisibility(View.VISIBLE);
                    listView.setAdapter(fileAdapter);
                    isSearching = false;
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
                    View view = getLayoutInflater().inflate(R.layout.choose_type, null);
                    Button commit = (Button) view.findViewById(R.id.commit);
                    Button cancel = (Button) view.findViewById(R.id.cancel);
                    commit.setOnClickListener(new ShareListener());
                    cancel.setOnClickListener(new ShareListener());
                    popupWindow.setContentView(view);
                    popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
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
                        try {
                            max = new FileInputStream(file).available();
                        } catch (IOException e) {
                            Log.e("io error--->", e.toString());
                        }
                        SendFile ft = new SendFile(app.getUser().socket, app.getUser().IP, app.getUser().port, file, wordHandler);
                        Thread t = new Thread(ft, "SendFile");
                        t.start();
                    } else {
                        Toast.makeText(WordActivity.this, "设备未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.cancel :
                    popupWindow.dismiss();
                    break;
            }
        }
    }


    public class WordListListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("file://" + wordList.get(position).getFilePath());
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

    public class EditTextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            searchList.clear();
            for (int i = 0; i < wordList.size(); i++) {
                MediaFiles file = wordList.get(i);
                if(file.getFileName().contains(s.toString())){
                    searchList.add(file);
                }
            }
            FileAdapter fileAdapter = new FileAdapter(getApplicationContext(), searchList, choseFiles, edit);
            listView.setAdapter(fileAdapter);
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
                    wordHandler.sendEmptyMessage(0x011);
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
            choseFiles.clear();
            fileAdapter.notifyDataSetChanged();
            edit.setVisibility(View.GONE);
        } else if(isSearching && editText.getText().toString().equals("")){
            relative_search.setVisibility(View.GONE);
            search.setVisibility(View.VISIBLE);
            listView.setAdapter(fileAdapter);
            isSearching = false;
        } else if(search.getVisibility() == View.VISIBLE){
            finish();
        }
        return true;
    }

    public void setListener(){
        back.setOnClickListener(new WordListener());
        search.setOnClickListener(new WordListener());
        cancel.setOnClickListener(new WordListener());
        copy.setOnClickListener(new WordListener());
        move.setOnClickListener(new WordListener());
        share.setOnClickListener(new WordListener());
        delete.setOnClickListener(new WordListener());
        chooseAll.setOnClickListener(new WordListener());
        editText.addTextChangedListener(new EditTextListener());
        listView.setOnItemClickListener(new WordListListener());
    }

    public void initView(){
        back = (LinearLayout) findViewById(R.id.back);
        TextView fileName = (TextView) findViewById(R.id.fileName);
        switch (getIntent().getStringExtra("style")){
            case "文档" :
                fileName.setText(R.string.word);
                style = getResources().getString(R.string.word);
                break;
            case "压缩包" :
                fileName.setText(R.string.zip);
                style = getResources().getString(R.string.zip);
                break;
            case "安装包" :
                fileName.setText(R.string.apk);
                style = getResources().getString(R.string.apk);
                break;
        }
        search = (LinearLayout) findViewById(R.id.search);
        search.setVisibility(View.VISIBLE);
        cancel = (LinearLayout) findViewById(R.id.cancel);
        editText = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.fileList);
        edit = (LinearLayout) findViewById(R.id.edit);
        copy = (LinearLayout) findViewById(R.id.copy);
        move = (LinearLayout) findViewById(R.id.move);
        share = (LinearLayout) findViewById(R.id.share);
        delete = (LinearLayout) findViewById(R.id.delete);
        chooseAll = (LinearLayout) findViewById(R.id.chooseAll);
        relative_search = (RelativeLayout) findViewById(R.id.relative_search);
        no_files_image = (RelativeLayout) findViewById(R.id.no_files_image);
        no_files_text = (RelativeLayout) findViewById(R.id.no_files_text);
        progress_background = (RelativeLayout) findViewById(R.id.progress_background);
        circleProgress = (CircleProgress) findViewById(R.id.progress);
        fileCount = (TextView) findViewById(R.id.fileCount);
        progressDialog = new ProgressDialog(WordActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        loadFile = new LoadFile(WordActivity.this);
        choseFiles = new ArrayList<>();
        searchList = new ArrayList<>();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        popupWindow = new PopupWindow(WordActivity.this);
        popupWindow.setWidth((int) (displayMetrics.widthPixels * 0.9));
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.share_style);
        app = (App)getApplication();
    }

}