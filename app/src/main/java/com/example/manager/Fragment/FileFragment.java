package com.example.manager.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.manager.Activity.HomeActivity;
import com.example.manager.Activity.FolderActivity;
import com.example.manager.Activity.MusicActivity;
import com.example.manager.Activity.StorageActivity;
import com.example.manager.Activity.VideoActivity;
import com.example.manager.Activity.WordActivity;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;
import com.example.manager.ResideMenu.ResideMenu;
import com.example.manager.Utils.StorageSize;

import java.io.File;
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
    private ImageView line;

    public  static Map<String, List<MediaFiles>> map;
    public  static final Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public  static final Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    public  static final Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public  static boolean getSuffixFile = false;
    public  static Thread thread = new Thread();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, null);
        initView();
        setListener();
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
            line.setVisibility(View.VISIBLE);
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
                    intent.setClass(getActivity(), MusicActivity.class);
                    startActivity(intent);
                    break;
                case R.id.linear_video :
                    intent.setClass(getActivity(), VideoActivity.class);
                    startActivity(intent);
                    break;
                case R.id.linear_image :
                    intent.setClass(getActivity(), FolderActivity.class);
                    startActivity(intent);
                    break;
                case R.id.linear_word :
                    intent.setClass(getActivity(), WordActivity.class);
                    intent.putExtra("style", getResources().getString(R.string.word));
                    startActivity(intent);
                    break;
                case R.id.linear_zip :
                    intent.setClass(getActivity(), WordActivity.class);
                    intent.putExtra("style", getResources().getString(R.string.zip));
                    startActivity(intent);
                    break;
                case R.id.linear_apk :
                    intent.setClass(getActivity(), WordActivity.class);
                    intent.putExtra("style", getResources().getString(R.string.apk));
                    startActivity(intent);
                    break;
                case R.id.relative_storage_in :
                    intent.setClass(getActivity(), StorageActivity.class);
                    intent.putExtra("storage", "in");
                    startActivity(intent);
                    break;
                case R.id.relative_storage_out :
                    intent.setClass(getActivity(), StorageActivity.class);
                    intent.putExtra("storage", "out");
                    startActivity(intent);
                    break;
            }
        }
    }

    public class GetCountThread implements Runnable{

        @Override
        public void run() {

            FragmentActivity activity;
            if((activity = getActivity()) != null){
                Cursor musicCursor = activity.getContentResolver().query(
                        musicUri, null, null, null, null);
                Cursor videoCursor = activity.getContentResolver().query(
                        videoUri, null, null, null, null);
                Cursor imageCursor = activity.getContentResolver().query(imageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/png", "image/jpg", "image/jpeg", "image/gif"}, null);
                Cursor wordCursor = activity.getContentResolver().query(
                        Uri.parse("content://media/external/file"), null,
                        MediaStore.Files.FileColumns.DATA + "=? or "
                                + MediaStore.Files.FileColumns.DATA  + " like ? or "
                                + MediaStore.Files.FileColumns.DATA  + " like ? or "
                                + MediaStore.Files.FileColumns.DATA  + " like ? or "
                                + MediaStore.Files.FileColumns.DATA  + " like ? or "
                                + MediaStore.Files.FileColumns.DATA  + " like ? or "
                                + MediaStore.Files.FileColumns.DATA  + " like ? or "
                                + MediaStore.Files.FileColumns.DATA  + " like ?",
                        new String[]{"%.doc", "%.docx", "%.ppt", "%.pptx", "%.pdf", "%.xlsx", "%.xls", "%.txt"}, null);
                int badWord = 0;
                while(wordCursor.moveToNext()){
                    String path = wordCursor.getString(wordCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    File file = new File(path);
                    if(!file.exists() || !file.isFile()){
                        badWord ++;
                    }
                }
                Cursor zipCursor = activity.getContentResolver().query(
                        Uri.parse("content://media/external/file"), null,
                        MediaStore.Files.FileColumns.DATA + " like ? or "
                                + MediaStore.Files.FileColumns.DATA  + " like ?",
                        new String[]{"%.zip", "%.rar"}, null);
                int badZip = 0;
                while(zipCursor.moveToNext()){
                    String path = zipCursor.getString(zipCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    File file = new File(path);
                    if(!file.exists() || !file.isFile()){
                        badZip ++;
                    }
                }
                Cursor apkCursor = activity.getContentResolver().query(
                        Uri.parse("content://media/external/file"), null,
                        MediaStore.Files.FileColumns.DATA + " like ?",
                        new String[]{"%.apk"}, null);
                int badApk = 0;
                while(apkCursor.moveToNext()){
                    String path = apkCursor.getString(apkCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    File file = new File(path);
                    if(!file.exists() || !file.isFile()){
                        badApk ++;
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putInt("musicCount",musicCursor.getCount());
                bundle.putInt("videoCount", videoCursor.getCount());
                bundle.putInt("imageCount", imageCursor.getCount());
                bundle.putInt("wordCount", wordCursor.getCount() - badWord);
                bundle.putInt("zipCount", zipCursor.getCount() - badZip);
                bundle.putInt("apkCount", apkCursor.getCount() - badApk);
                musicCursor.close();
                videoCursor.close();
                imageCursor.close();
                wordCursor.close();
                zipCursor.close();
                apkCursor.close();
                Message msg = new Message();
                msg.what = 0x000;
                msg.setData(bundle);
                setCountHandler.sendMessage(msg);
            }
        }
    }

    public class SetCountHandler extends Handler{
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 0x000 :
                    music_count.setText(msg.getData().get("musicCount") + "项");
                    video_count.setText(msg.getData().get("videoCount") + "项");
                    image_count.setText(msg.getData().get("imageCount") + "项");
                    word_count.setText(msg.getData().get("wordCount") + "项");
                    zip_count.setText(msg.getData().get("zipCount") + "项");
                    apk_count.setText(msg.getData().get("apkCount") + "项");
                    getSuffixFile = true;
                    break;
            }
        }
    }

    public void onResume() {
        super.onResume();
        GetCountThread gt = new GetCountThread();
        thread = new Thread(gt, "GetCountThread");
        thread.start();
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
        line = (ImageView) view.findViewById(R.id.line);
        useful_storage_out = (TextView) view.findViewById(R.id.useful_storage_out);

        intent = new Intent();
        setCountHandler = new SetCountHandler();
    }

}