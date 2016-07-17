package com.example.manager.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.manager.Class.ImageFolder;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.Utils.ImageLoader;
import com.example.manager.Utils.LoadFile;
import com.example.manager.R;

import java.util.List;

/**
 * Created by Android on 2016/4/14.
 */
public class ImageFolderList extends Activity{

    private GridView gridView;
    private LinearLayout back;
    private LinearLayout search;
    private RelativeLayout no_files_image;
    private RelativeLayout no_files_text;
    public  static LoadFile loadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtil.initActionBar(getActionBar(), getResources().getString(R.string.image), 0x222);
        setContentView(R.layout.imagelayout);
        initView();
        setListener();
        Cursor imageCursor = loadFile.loadImage(getContentResolver());
        if(imageCursor != null && imageCursor.getCount() != 0){
            imageCursor.close();
            FolderAdapter imageAdapter = new FolderAdapter(getApplicationContext());
            gridView.setAdapter(imageAdapter);
        } else {
            loadFile.addView(no_files_image, no_files_text, R.drawable.picture);
        }
    }

    public class FolderAdapter extends BaseAdapter{

        private LayoutInflater inflater;
        private ImageLoader imageLoader;

        public FolderAdapter(Context context){
            inflater = LayoutInflater.from(context);
            imageLoader = ImageLoader.getInstance();
        }

        @Override
        public int getCount() {
            return loadFile.getFolderList().size();
        }

        @Override
        public Object getItem(int position) {
            return loadFile.getFolderList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linear;
            ViewHolder viewHolder;
            if(convertView == null){
                linear = (LinearLayout)inflater.inflate(R.layout.image_folder, null);
                viewHolder = new ViewHolder();
                viewHolder.folderView = (ImageView) linear.findViewById(R.id.folderView);
                viewHolder.folderName = (TextView) linear.findViewById(R.id.folderName);
                viewHolder.folderCount = (TextView) linear.findViewById(R.id.folderCount);
                linear.setTag(viewHolder);
            } else {
                linear = (LinearLayout)convertView;
                viewHolder = (ViewHolder)linear.getTag();
            }
            Log.i("position", String.valueOf(position));
            ImageFolder imageFolder = loadFile.getFolderList().get(position);
            viewHolder.folderCount.setText(imageFolder.getCount() + "");

            imageLoader.loadImage(imageFolder.getFirstPath(), viewHolder.folderView);
            int pos = imageFolder.getFolderPath().lastIndexOf('/');
            viewHolder.folderName.setText(imageFolder.getFolderPath().substring(pos + 1));

            return linear;
        }

        class ViewHolder{
            ImageView folderView;
            TextView folderName;
            TextView folderCount;
        }
    }

    public class ItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(ImageFolderList.this, ImageList.class);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    public class ImageListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back :
                    finish();
                    break;
                case R.id.search :

                    break;
            }
        }
    }

    public void setListener(){
        back.setOnClickListener(new ImageListener());
        search.setOnClickListener(new ImageListener());
        gridView.setOnItemClickListener(new ItemClickListener());
    }

    public void initView(){
        gridView = (GridView) findViewById(R.id.gridView);
        back = (LinearLayout) findViewById(R.id.back);
        search = (LinearLayout) findViewById(R.id.search);
        no_files_image = (RelativeLayout) findViewById(R.id.no_files_image);
        no_files_text = (RelativeLayout) findViewById(R.id.no_files_text);
        loadFile = new LoadFile(ImageFolderList.this);
    }

}
