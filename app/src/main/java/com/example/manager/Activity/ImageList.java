package com.example.manager.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.manager.Application.App;
import com.example.manager.Class.ImageFolder;
import com.example.manager.Class.MediaFiles;
import com.example.manager.R;
import com.example.manager.Thread.FileThread;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.Utils.ImageLoader;
import com.example.manager.Utils.LoadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2016/4/16.
 */
public class ImageList extends Activity {

    private App app;
    private GridView gridView;
    private LinearLayout back;
    private LinearLayout search;
    private LinearLayout copy;
    private LinearLayout move;
    private LinearLayout share;
    private LinearLayout delete;
    private LinearLayout chooseAll;
    private LoadFile loadFile;
    private LinearLayout edit;
    private boolean hasChoseAll = false;
    private ImageHandler imageHandler;
    private ProgressDialog progressDialog;
    public  static List<MediaFiles> choseFiles;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtil.initActionBar(getActionBar(), getResources().getString(R.string.image), 0x222);
        setContentView(R.layout.imagelayout);
        initView();
        setListener();
        Intent intent;
        if((intent = getIntent()) != null){
            int position = intent.getIntExtra("position", 0);
            ImageFolder imageFolder = ImageFolderList.loadFile.getFolderList().get(position);
            loadFile = new LoadFile(ImageList.this);
            loadFile.getImage(imageFolder.getFolderPath());
            Log.i("storageList size : ", String.valueOf(loadFile.getStorage().size()));
            ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext());
            gridView.setAdapter(imageAdapter);
        }
    }

    public class ImageAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ImageLoader imageLoader;

        public ImageAdapter(Context context){
            inflater = LayoutInflater.from(context);
            imageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
        }

        @Override
        public int getCount() {
            return loadFile.getImageList().size();
        }

        @Override
        public Object getItem(int position) {
            return loadFile.getImageList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder ;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.gridview_item, parent,false);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.itemImage);
                viewHolder.check = (ImageView) convertView.findViewById(R.id.checkBox);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
                viewHolder.imageView.setImageResource(R.drawable.pictures_no);
            }

            MediaFiles file = loadFile.getImageList().get(position);
            file.check = viewHolder.check;
            if(file.count == 1) {
                file.check.setBackgroundResource(R.drawable.side_checked);
            } else {
                file.check.setBackgroundResource(R.drawable.side);
            }

            imageLoader.loadImage(loadFile.getImageList().get(position).getFilePath(), viewHolder.imageView);

            return convertView;
        }

        class ViewHolder{
            ImageView imageView;
            ImageView check;
        }

    }

    public class ImageItemListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaFiles mediaFiles = loadFile.getImageList().get(position);
            if(mediaFiles.count == 1){
                mediaFiles.count = 0;
                mediaFiles.check.setBackgroundResource(R.drawable.side);
                choseFiles.remove(mediaFiles);
                int aChoose;
                for (aChoose = 0; aChoose < loadFile.getImageList().size(); aChoose ++) {
                    if (loadFile.getImageList().get(aChoose).count == 1) {
                        break;
                    }
                }
                if(aChoose == loadFile.getImageList().size()){
                    edit.setVisibility(View.GONE);
                }
            } else {
                mediaFiles.count = 1;
                mediaFiles.check.setBackgroundResource(R.drawable.side_checked);
                choseFiles.add(mediaFiles);
                edit.setVisibility(View.VISIBLE);
            }
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
                    intent.setClass(ImageList.this, OperateFile.class);
                    intent.putExtra("style", R.string.image);
                    intent.putExtra("operation", "copy");
                    startActivity(intent);
                    break;
                case R.id.move :
                    intent.setClass(ImageList.this, OperateFile.class);
                    intent.putExtra("style", R.string.image);
                    intent.putExtra("operation", "move");
                    startActivity(intent);
                    break;
                case R.id.share :
                    if(app.getUser().connected) {
                        progressDialog.show();
                        File file = new File(choseFiles.get(0).getFilePath());
                        FileThread ft = new FileThread(app.getUser().socket, app.getUser().IP, app.getUser().port, file, imageHandler);
                        Thread t = new Thread(ft, "FileThread");
                        t.start();
                    } else {
                        Toast.makeText(ImageList.this, "设备未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.delete :
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ImageList.this);
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
                                if (file.check != null) {
                                    file.check.setBackgroundResource(R.drawable.side_checked);
                                }
                            }
                            if (i == loadFile.getImageList().size() - 1) {
                                hasChoseAll = true;
                            }
                        }
                    } else {
                        for (int i = 0; i < loadFile.getImageList().size(); i++) {
                            file = loadFile.getImageList().get(i);
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
                    if(hasChoseAll){
                        choseFiles = loadFile.getImageList();
                    }
                    for (int i = 0; i < choseFiles.size(); i++) {
                        File file = new File(choseFiles.get(i).getFilePath());
                        loadFile.deleteFile(file);
                        loadFile.getImageList().remove(choseFiles.get(i));
                    }
                    choseFiles.clear();
                    ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext());
                    gridView.setAdapter(imageAdapter);
                    Toast.makeText(ImageList.this, "删除完成", Toast.LENGTH_SHORT).show();
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
                        FileThread ft = new FileThread(app.getUser().socket, app.getUser().IP, app.getUser().port, file, imageHandler);
                        Thread t = new Thread(ft, "FileThread");
                        t.start();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ImageList.this, "传输完成!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x333 :
                    progressDialog.dismiss();
                    app.getUser().connected = false;
                    Toast.makeText(ImageList.this, "连接失败，请重新连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public void setListener(){
        back.setOnClickListener(new ImageListener());
        search.setOnClickListener(new ImageListener());
        copy.setOnClickListener(new ImageListener());
        move.setOnClickListener(new ImageListener());
        share.setOnClickListener(new ImageListener());
        delete.setOnClickListener(new ImageListener());
        chooseAll.setOnClickListener(new ImageListener());
        gridView.setOnItemClickListener(new ImageItemListener());
    }

    public void initView(){
        back = (LinearLayout) findViewById(R.id.back);
        search = (LinearLayout) findViewById(R.id.search);
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
