package com.example.manager.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.manager.Activity.HomeActivity;
import com.example.manager.Activity.ImageFolderList;
import com.example.manager.Activity.MusicList;
import com.example.manager.Activity.Storage;
import com.example.manager.Activity.VideoList;
import com.example.manager.Activity.WordList;
import com.example.manager.Class.MediaFiles;
import com.example.manager.R;
import com.example.manager.ResideMenu.ResideMenu;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.Utils.LoadFile;
import com.example.manager.Utils.StorageSize;

import java.util.List;
import java.util.Map;

public class FileFragment extends Fragment {

    private View view;
    private LinearLayout menu;
    private LinearLayout linear_music;
    private LinearLayout linear_image;
    private LinearLayout linear_video;
    private LinearLayout linear_zip;
    private LinearLayout linear_word;
    private LinearLayout linear_apk;
    private RelativeLayout relative_storage_in;
    private RelativeLayout relative_storage_out;
    private TextView music_count;
    private TextView video_count;
    private TextView image_count;
    private TextView word_count;
    private TextView zip_count;
    private TextView apk_count;
    private TextView useful_storage;
    private TextView useful_storage_out;
    private ProgressBar progressBar;
    private ProgressBar progressBarOut;
    private Intent intent;
    private SetCountHandler setCountHandler;

    public  static final Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public  static final Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    public  static final Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public  static Map<String, List<MediaFiles>> map;
    public  static boolean getSuffixFile = false;

    private Thread [] t = new Thread[2];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, null);
        initView();
        setListener();

        Thread [] t = new Thread[2];
        GetCountThread gt = new GetCountThread();
        t[0] = new Thread(gt,"GetCountThread");
        t[0].start();
        SuffixFileThread st = new SuffixFileThread();
        t[1] = new Thread(st, "SuffixFileThread");
        t[1].start();
        StorageSize storageSize = new StorageSize();
        String storageIn = storageSize.getAvailableInternalMemorySize(getActivity());
        String totalSize = storageSize.getInternalMemorySize(getActivity());
        useful_storage.setText(storageIn + "可用");

        /**
         * 手机总存储空间
         */
        String maxSize = totalSize.substring(0, totalSize.lastIndexOf(' '));
        String doubleMax = String.valueOf(Double.parseDouble(maxSize) * 100);
        int max = Integer.parseInt(doubleMax.substring(0, doubleMax.lastIndexOf('.')));
        /**
         * 手机剩余存储空间
         */
        String hasUsed = storageIn.substring(0, storageIn.lastIndexOf(' '));
        String doubleSize = String.valueOf(Double.parseDouble(hasUsed) * 100);
        int size = Integer.parseInt(doubleSize.substring(0, doubleSize.lastIndexOf('.')));
        progressBar.setMax(max);
        progressBar.setProgress(max - size);

        if(storageSize.externalStorageAvailable() != null){
            relative_storage_out.setVisibility(View.VISIBLE);
            String storageOut = storageSize.getAvailableExternalMemorySize(getActivity());
            totalSize = storageSize.getExternalMemorySize(getActivity());
            useful_storage_out.setText(storageOut + "可用");

            /**
             * SD卡总存储空间
             */
            maxSize = totalSize.substring(0, totalSize.lastIndexOf(' '));
            doubleMax = String.valueOf(Double.parseDouble(maxSize) * 100);
            max = Integer.parseInt(doubleMax.substring(0, doubleMax.lastIndexOf('.')));
            /**
             * SD卡剩余存储空间
             */
            hasUsed = storageOut.substring(0, storageIn.lastIndexOf(' '));
            doubleSize = String.valueOf(Double.parseDouble(hasUsed) * 100);
            size = Integer.parseInt(doubleSize.substring(0, doubleSize.lastIndexOf('.')));
            progressBarOut.setMax(max);
            progressBarOut.setProgress(max - size);
        }
        Log.w("TotalInternal", storageSize.getInternalMemorySize(getActivity()));
        Log.w("AvailableInternal", storageSize.getAvailableInternalMemorySize(getActivity()));

        return view;
    }

    public class HomeListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.menu :
                    HomeActivity.resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                    break;
                case R.id.linear_music :
                    intent.setClass(getActivity(), MusicList.class);
                    startActivity(intent);
                    break;
                case R.id.linear_video :
                    intent.setClass(getActivity(), VideoList.class);
                    startActivity(intent);
                    break;
                case R.id.linear_image :
                    intent.setClass(getActivity(), ImageFolderList.class);
                    startActivity(intent);
                    break;
                case R.id.linear_word :
                    intent.setClass(getActivity(), WordList.class);
                    intent.putExtra("style", getResources().getString(R.string.word));
                    startActivity(intent);
                    break;
                case R.id.linear_zip :
                    intent.setClass(getActivity(), WordList.class);
                    intent.putExtra("style", getResources().getString(R.string.zip));
                    startActivity(intent);
                    break;
                case R.id.linear_apk :
                    intent.setClass(getActivity(), WordList.class);
                    intent.putExtra("style", getResources().getString(R.string.apk));
                    startActivity(intent);
                    break;
                case R.id.relative_storage_in :
                    intent.setClass(getActivity(), Storage.class);
                    intent.putExtra("storage", "in");
                    startActivity(intent);
                    break;
                case R.id.relative_storage_out :
                    intent.setClass(getActivity(), Storage.class);
                    intent.putExtra("storage", "out");
                    startActivity(intent);
                    break;
            }
        }
    }

    public class GetCountThread implements Runnable{

        @Override
        public void run() {
            Cursor musicCursor = getActivity().getContentResolver().query(musicUri, null, null, null, null);
            Cursor videoCursor = getActivity().getContentResolver().query(videoUri, null, null, null, null);
            Cursor imageCursor = getActivity().getContentResolver().query(imageUri, null, null, null, null);

            Bundle bundle = new Bundle();
            bundle.putInt("musicCount",musicCursor.getCount());
            bundle.putInt("videoCount", videoCursor.getCount());
            bundle.putInt("imageCount", imageCursor.getCount());
            musicCursor.close();
            videoCursor.close();
            imageCursor.close();
            Message msg = new Message();
            msg.what = 0x000;
            msg.setData(bundle);
            setCountHandler.sendMessage(msg);
        }
    }
    public class SuffixFileThread implements Runnable{

        @Override
        public void run() {
            LoadFile loadFile = new LoadFile(getActivity());
            map = loadFile.loadSuffixFiles(Environment.getExternalStorageDirectory() + "/", true);
            Bundle bundle = new Bundle();
            bundle.putInt("wordCount", map.get("wordList").size());
            bundle.putInt("zipCount", map.get("zipList").size());
            bundle.putInt("apkCount", map.get("apkList").size());
            Message msg = new Message();
            msg.what = 0x111;
            msg.setData(bundle);
            setCountHandler.sendMessage(msg);
        }
    }

    public class SetCountHandler extends Handler{
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 0x000 :
                    music_count.setText(msg.getData().get("musicCount") + "项");
                    video_count.setText(msg.getData().get("videoCount") + "项");
                    image_count.setText(msg.getData().get("imageCount") + "项");
                    break;
                case 0x111 :
                    word_count.setText(msg.getData().get("wordCount") + "项");
                    zip_count.setText(msg.getData().get("zipCount") + "项");
                    apk_count.setText(msg.getData().get("apkCount") + "项");
                    getSuffixFile = true;
                    break;
            }
        }
    }

    public void onResume(){
        super.onResume();
        GetCountThread gt = new GetCountThread();
        t[0] = new Thread(gt,"GetCountThread");
        t[0].start();
        SuffixFileThread st = new SuffixFileThread();
        t[1] = new Thread(st, "SuffixFileThread");
        t[1].start();
    }

    public void onStart(){
        super.onStart();
        GetCountThread gt = new GetCountThread();
        t[0] = new Thread(gt,"GetCountThread");
        t[0].start();
        SuffixFileThread st = new SuffixFileThread();
        t[1] = new Thread(st, "SuffixFileThread");
        t[1].start();
    }

    public void setListener(){
        menu.setOnClickListener(new HomeListener());
        linear_music.setOnClickListener(new HomeListener());
        linear_video.setOnClickListener(new HomeListener());
        linear_image.setOnClickListener(new HomeListener());
        linear_word.setOnClickListener(new HomeListener());
        linear_zip.setOnClickListener(new HomeListener());
        linear_apk.setOnClickListener(new HomeListener());
        relative_storage_in.setOnClickListener(new HomeListener());
        relative_storage_out.setOnClickListener(new HomeListener());
    }

    private void initView(){
        menu = (LinearLayout) view.findViewById(R.id.menu);
        LinearLayout back = (LinearLayout) view.findViewById(R.id.back);
        TextView title = (TextView) view.findViewById(R.id.fileName);
        LinearLayout search = (LinearLayout) view.findViewById(R.id.search);
        search.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        menu.setVisibility(View.VISIBLE);
        title.setText("文件管理");
        linear_music = (LinearLayout) view.findViewById(R.id.linear_music);
        linear_video = (LinearLayout) view.findViewById(R.id.linear_video);
        linear_image = (LinearLayout) view.findViewById(R.id.linear_image);
        linear_word = (LinearLayout) view.findViewById(R.id.linear_word);
        linear_zip = (LinearLayout) view.findViewById(R.id.linear_zip);
        linear_apk = (LinearLayout) view.findViewById(R.id.linear_apk);

        music_count = (TextView) view.findViewById(R.id.music_count);
        video_count = (TextView) view.findViewById(R.id.video_count);
        image_count = (TextView) view.findViewById(R.id.image_count);
        word_count = (TextView) view.findViewById(R.id.word_count);
        zip_count = (TextView) view.findViewById(R.id.zip_count);
        apk_count = (TextView) view.findViewById(R.id.apk_count);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBarOut = (ProgressBar) view.findViewById(R.id.progressBar_out);

        relative_storage_in = (RelativeLayout) view.findViewById(R.id.relative_storage_in);
        relative_storage_out = (RelativeLayout) view.findViewById(R.id.relative_storage_out);
        useful_storage = (TextView) view.findViewById(R.id.useful_storage);
        useful_storage_out = (TextView) view.findViewById(R.id.useful_storage_out);

        intent = new Intent();
        setCountHandler = new SetCountHandler();
    }

}