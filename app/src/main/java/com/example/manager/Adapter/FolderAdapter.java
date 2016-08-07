package com.example.manager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manager.Model.ImageFolder;
import com.example.manager.R;
import com.example.manager.Utils.ImageLoader;

import java.util.List;

/**
 * Created by Dangelo on 2016/8/4.
 */
public class FolderAdapter extends BaseAdapter {

    private Context context;
    private List<ImageFolder> folderList;
    private ImageLoader imageLoader;

    public FolderAdapter(Context context, List<ImageFolder> folderList){
        this.context = context;
        this.folderList = folderList;
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return folderList.size();
    }

    @Override
    public Object getItem(int position) {
        return folderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.image_folder, null);
            viewHolder = new ViewHolder();
            viewHolder.folderView = (ImageView) convertView.findViewById(R.id.folderView);
            viewHolder.folderName = (TextView) convertView.findViewById(R.id.folderName);
            viewHolder.folderCount = (TextView) convertView.findViewById(R.id.folderCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        ImageFolder imageFolder = folderList.get(position);
        viewHolder.folderName.setText(imageFolder.getFolderName());
        viewHolder.folderCount.setText(imageFolder.getCount() + "");

        imageLoader.loadImage(imageFolder.getFirstImagePath(), viewHolder.folderView);

        return convertView;
    }

    class ViewHolder{
        ImageView folderView;
        TextView folderName;
        TextView folderCount;
    }
}
