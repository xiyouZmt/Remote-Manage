package com.example.manager.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.Adapter.FileAdapter;
import com.example.manager.Adapter.VideoAdapter;
import com.example.manager.Application.App;
import com.example.manager.Model.MediaFiles;
import com.example.manager.Thread.SendFile;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.Utils.CircleProgress;
import com.example.manager.Utils.LoadFile;
import com.example.manager.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dangelo on 2016/4/4.
 */
public class VideoActivity extends Activity {

    private App app;
    private GridView gridView;
    private LinearLayout back;
    private LinearLayout search;
    private LoadFile loadFile;
    private LinearLayout edit;
    private LinearLayout copy;
    private LinearLayout move;
    private LinearLayout share;
    private LinearLayout delete;
    private LinearLayout chooseAll;
    private RelativeLayout no_files_image;
    private RelativeLayout no_files_text;
    private VideoHandler videoHandler;
    private ProgressDialog progressdialog;
    private PopupWindow popupWindow;
    private RelativeLayout progress_background;
    private CircleProgress circleProgress;
    private TextView fileCount;
    private VideoAdapter videoAdapter;
    public  static List<MediaFiles> choseFiles;
    private boolean hasChoseAll = false;
    private int max = 0;
    private int count = 0;
    private double temp = 0;
    private final int ok = 2;
    private final int error = 4;
    public  static List<String> newPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtil.initActionBar(getActionBar(), getResources().getString(R.string.video), 0x222);
        setContentView(R.layout.video_layout);
        initView();
        setListener();
        progressdialog.show();
        VideoThread vt = new VideoThread();
        Thread t = new Thread(vt, "VideoThread");
        t.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(edit.getVisibility() == View.VISIBLE){
            if(hasChoseAll){
                for (int i = 0; i < loadFile.getVideoList().size(); i++) {
                    loadFile.getVideoList().get(i).count = 0;
                    if(newPath.size() != 0){
                        loadFile.getVideoList().get(i).setFilePath(newPath.get(i));
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
            videoAdapter.notifyDataSetChanged();
            edit.setVisibility(View.GONE);
        }
    }

    public class VideoThread implements Runnable{

        @Override
        public void run() {
            Cursor cursor = loadFile.loadVideo(getContentResolver());
            if(cursor != null && cursor.getCount() != 0) {
                cursor.close();
                videoHandler.sendEmptyMessage(ok);
            } else {
                videoHandler.sendEmptyMessage(error);
            }
        }
    }

    public class VideoListener implements View.OnClickListener{

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
                    intent.setClass(VideoActivity.this, OperateActivity.class);
                    intent.putExtra("style", R.string.video);
                    intent.putExtra("operation", "copy");
                    startActivity(intent);
                    break;
                case R.id.move :
                    intent.setClass(VideoActivity.this, OperateActivity.class);
                    intent.putExtra("style", R.string.video);
                    intent.putExtra("operation", "move");
                    startActivity(intent);
                    break;
                case R.id.share :
                    View view = getLayoutInflater().inflate(R.layout.choose_type, null);
                    Button toPc = (Button) view.findViewById(R.id.pc);
                    Button toPhone = (Button) view.findViewById(R.id.phone);
                    Button cancel = (Button) view.findViewById(R.id.cancel);
                    toPc.setOnClickListener(new ShareListener());
                    toPhone.setOnClickListener(new ShareListener());
                    cancel.setOnClickListener(new ShareListener());
                    popupWindow.setContentView(view);
                    popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                    break;
                case R.id.delete :
                    AlertDialog.Builder dialog = new AlertDialog.Builder(VideoActivity.this);
                    dialog.setTitle("提示").setMessage("确认删除").setPositiveButton("确认", new DialogListener())
                            .setNegativeButton("取消", new DialogListener()).create().show();
                    break;
                case R.id.chooseAll :
                    MediaFiles file;
                    if(!hasChoseAll) {
                        for (int i = 0; i < loadFile.getVideoList().size(); i++) {
                            file = loadFile.getVideoList().get(i);
                            if (file.count == 0) {
                                file.count = 1;
                                if (file.checkBox != null) {
                                    file.checkBox.setChecked(true, true);
                                }
                            }
                            if (i == loadFile.getVideoList().size() - 1) {
                                hasChoseAll = true;
                            }
                        }
                        choseFiles = loadFile.getVideoList();
                    } else {
                        for (int i = 0; i < loadFile.getVideoList().size(); i++) {
                            file = loadFile.getVideoList().get(i);
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
                case R.id.pc :
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
                        SendFile ft = new SendFile(app.getUser().socket, app.getUser().IP, app.getUser().port, file, videoHandler);
                        Thread t = new Thread(ft, "SendFile");
                        t.start();
                    } else {
                        Toast.makeText(VideoActivity.this, "设备未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.phone :
                    break;
                case R.id.cancel :
                    popupWindow.dismiss();
                    break;
            }
        }
    }

    public class VideoListListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("file://" + loadFile.getVideoList().get(position).getFilePath());
            intent.setDataAndType(uri, "video/*");
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
                        loadFile.getVideoList().clear();
                    } else {
                        loadFile.getVideoList().removeAll(choseFiles);
                        choseFiles.clear();
                    }
                    videoAdapter.notifyDataSetChanged();
                    Toast.makeText(VideoActivity.this, "删除完成", Toast.LENGTH_SHORT).show();
                    edit.setVisibility(View.GONE);
                    break;
                case -2 :
                    dialog.dismiss();
                    break;
            }
        }
    }

    public class VideoHandler extends Handler{
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
                        fileCount.setText("共" + choseFiles.size() + "项, 第" + count + 1 + "项");
                        File file = new File(choseFiles.get(count).getFilePath());
                        try {
                            max = new FileInputStream(file).available();
                        } catch (IOException e) {
                            Log.e("io error--->", e.toString());
                        }
                        SendFile ft = new SendFile(app.getUser().socket, app.getUser().IP, app.getUser().port, file, videoHandler);
                        Thread t = new Thread(ft, "SendFile");
                        t.start();
                    } else {
                        progress_background.setVisibility(View.GONE);
                        count = 0;
                        if(hasChoseAll){
                            for (i = 0; i < loadFile.getVideoList().size(); i++) {
                                loadFile.getVideoList().get(i).count = 0;
                            }
                        } else {
                            for (i = 0; i < choseFiles.size(); i++) {
                                choseFiles.get(i).count = 0;
                            }
                        }
                        choseFiles.clear();
                        videoAdapter.notifyDataSetChanged();
                        circleProgress.setProgress(0);
                        Toast.makeText(VideoActivity.this, "传输完成!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x333 :
                    progressdialog.dismiss();
                    app.getUser().connected = false;
                    circleProgress.setProgress(0);
                    Toast.makeText(VideoActivity.this, "连接失败，请重新连接", Toast.LENGTH_SHORT).show();
                    break;
                case ok :
                    progressdialog.dismiss();
                    videoAdapter = new VideoAdapter(getApplicationContext(), loadFile.getVideoList(), choseFiles, edit);
                    gridView.setAdapter(videoAdapter);
                    break;
                case error :
                    progressdialog.dismiss();
                    loadFile.addView(no_files_image, no_files_text, R.drawable.no_video);
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(edit.getVisibility() == View.VISIBLE){
            if(hasChoseAll){
                for (int i = 0; i < loadFile.getVideoList().size(); i++) {
                    loadFile.getVideoList().get(i).count = 0;
                }
            } else {
                for (int i = 0; i < choseFiles.size(); i++) {
                    choseFiles.get(i).count = 0;
                }
            }
            videoAdapter.notifyDataSetChanged();
            edit.setVisibility(View.GONE);
        } else {
            finish();
        }
        return true;
    }

    public void setListener(){
        back.setOnClickListener(new VideoListener());
        search.setOnClickListener(new VideoListener());
        copy.setOnClickListener(new VideoListener());
        move.setOnClickListener(new VideoListener());
        share.setOnClickListener(new VideoListener());
        delete.setOnClickListener(new VideoListener());
        chooseAll.setOnClickListener(new VideoListener());
        gridView.setOnItemClickListener(new VideoListListener());
    }

    public void initView(){
        gridView = (GridView) findViewById(R.id.gridView);
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
        loadFile = new LoadFile(VideoActivity.this);
        choseFiles = new ArrayList<>();
        videoHandler = new VideoHandler();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        popupWindow = new PopupWindow(VideoActivity.this);
        popupWindow.setWidth((int) (displayMetrics.widthPixels * 0.9));
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.share_style);
        progress_background = (RelativeLayout) findViewById(R.id.progress_background);
        circleProgress = (CircleProgress) findViewById(R.id.progress);
        fileCount = (TextView) findViewById(R.id.fileCount);
        progressdialog = new ProgressDialog(this);
        progressdialog.setMessage("加载中...");
        progressdialog.setCancelable(false);
        app = (App) getApplication();
    }

}
