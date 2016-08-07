package com.example.manager.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.manager.Adapter.FolderAdapter;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.Utils.LoadFile;
import com.example.manager.R;

/**
 * Created by Android on 2016/4/14.
 */
public class FolderActivity extends Activity{

    private GridView gridView;
    private LinearLayout back;
    private LinearLayout search;
    private RelativeLayout no_files_image;
    private RelativeLayout no_files_text;
    private FolderAdapter imageAdapter;
    public  static LoadFile loadFile;
    private int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtil.initActionBar(getActionBar(), getResources().getString(R.string.image), 0x222);
        setContentView(R.layout.folder_layout);
        initView();
        setListener();
        Cursor imageCursor = loadFile.loadImage(getContentResolver());
        if(imageCursor != null && imageCursor.getCount() != 0){
            imageCursor.close();
            for (int i = 0; i < loadFile.getFolderList().size(); i++) {
                String folderPath = loadFile.getFolderList().get(i).getFolderPath();
                String firstImagePath = loadFile.getFirstImagePath(folderPath);
                loadFile.getFolderList().get(i).setFirstImagePath(firstImagePath);
            }
            imageAdapter = new FolderAdapter(getApplicationContext(), loadFile.getFolderList());
            gridView.setAdapter(imageAdapter);
        } else {
            loadFile.addView(no_files_image, no_files_text, R.drawable.picture);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pos != -1){
            String folderPath = loadFile.getFolderList().get(pos).getFolderPath();
            String firstImagePath = loadFile.getFirstImagePath(folderPath);
            loadFile.getFolderList().get(pos).setFirstImagePath(firstImagePath);
            int count = loadFile.getImageCount(loadFile.getFolderList().get(pos).getFolderPath());
            loadFile.getFolderList().get(pos).setCount(count);
            if(count == 0){
                loadFile.getFolderList().remove(pos);
            }
            imageAdapter.notifyDataSetChanged();
        }
    }

    public class ItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            pos = position;
            Intent intent = new Intent(FolderActivity.this, ImageActivity.class);
            String folderName = loadFile.getFolderList().get(position).getFolderName();
            intent.putExtra("folderName", folderName);
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
        loadFile = new LoadFile(FolderActivity.this);
        search.setVisibility(View.GONE);
    }

}
