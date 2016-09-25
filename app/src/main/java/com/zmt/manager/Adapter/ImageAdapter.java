package com.zmt.manager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zmt.manager.CheckBox.SmoothCheckBox;
import com.zmt.manager.Model.MediaFiles;
import com.zmt.manager.R;
import com.zmt.manager.Utils.ImageLoader;

import java.util.List;

/**
 * Created by Dangelo on 2016/8/4.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List<MediaFiles> imageList;
    private List<MediaFiles> choseFiles;
    private LinearLayout edit;
    private ImageLoader imageLoader;

    public ImageAdapter(Context context, List<MediaFiles> imageList, List<MediaFiles> choseFiles, LinearLayout edit){
        this.context = context;
        this.imageList = imageList;
        this.choseFiles = choseFiles;
        this.edit = edit;
        imageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.itemImage);
            viewHolder.checkBox = (SmoothCheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
            viewHolder.imageView.setImageResource(R.drawable.pictures_no);
        }
        final MediaFiles file = imageList.get(position);
        file.checkBox = viewHolder.checkBox;
        if(file.count == 1) {
            file.checkBox.setChecked(true);
        } else {
            file.checkBox.setChecked(false);
        }

        file.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.count == 0) {
                    file.count = 1;
                    if(file.checkBox != null){
                        file.checkBox.setChecked(true, true);
                    }
                    choseFiles.add(file);
                    edit.setVisibility(View.VISIBLE);
                } else {
                    file.checkBox.setChecked(false, true);
                    file.count = 0;
                    choseFiles.remove(file);
                    int aChoose;
                    for (aChoose = 0; aChoose < imageList.size(); aChoose++) {
                        if (imageList.get(aChoose).count == 1) {
                            break;
                        }
                    }
                    if (aChoose == imageList.size()) {
                        edit.setVisibility(View.GONE);
                    }
                }
            }
        });

        imageLoader.loadImage(imageList.get(position).getFilePath(), viewHolder.imageView);

        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
        SmoothCheckBox checkBox;
    }

}