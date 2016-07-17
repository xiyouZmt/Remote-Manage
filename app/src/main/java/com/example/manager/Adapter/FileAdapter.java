package com.example.manager.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.manager.Class.MediaFiles;
import com.example.manager.R;
import com.example.manager.Utils.StorageSize;

import java.util.List;

/**
 * Created by Dangelo on 2016/4/5.
 */
public class FileAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List fileList;
    private int resources;

    public FileAdapter(Context context, List fileList, int resources){
        layoutInflater = LayoutInflater.from(context);
        this.fileList = fileList;
        this.resources = resources;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout relative;
        ViewHolder viewHolder;
        if(convertView == null){
            relative = (RelativeLayout)layoutInflater.inflate(R.layout.list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.fileName = (TextView)relative.findViewById(R.id.file_name);
            viewHolder.fileSize = (TextView)relative.findViewById(R.id.file_size);
            viewHolder.fileImage = (ImageView)relative.findViewById(R.id.file_image);
            viewHolder.check = (ImageView) relative.findViewById(R.id.checkBox);
            relative.setTag(viewHolder);
        } else {
            relative = (RelativeLayout)convertView;
            viewHolder =(ViewHolder)relative.getTag();
        }
        Log.i("position", String.valueOf(position));
        MediaFiles file = (MediaFiles)fileList.get(position);
        file.check = viewHolder.check;
        if(file.count == 1) {
            file.check.setBackgroundResource(R.drawable.side_checked);
        } else {
            file.check.setBackgroundResource(R.drawable.side);
        }
        viewHolder.fileImage.setBackgroundResource(resources);
        if(file.getArtist() != null){
            viewHolder.fileName.setText(file.getArtist() + " - " + file.getFileName() + ".mp3");
        } else {
            viewHolder.fileName.setText(file.getFileName());
        }
        StorageSize storageSize = new StorageSize();
        viewHolder.fileSize.setText(storageSize.typeChange(Double.parseDouble(file.getFileSize())));
        return relative;
    }

    class ViewHolder{
        ImageView fileImage;
        TextView fileName;
        TextView fileSize;
        ImageView check;
    }

}