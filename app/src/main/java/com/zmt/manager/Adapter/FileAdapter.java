package com.zmt.manager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmt.manager.CheckBox.SmoothCheckBox;
import com.zmt.manager.Model.MediaFiles;
import com.zmt.manager.R;
import com.zmt.manager.Utils.StorageSize;

import java.util.List;

/**
 * Created by Dangelo on 2016/4/5.
 */
public class FileAdapter extends BaseAdapter {

    private Context context;
    private List<MediaFiles> fileList;
    private List<MediaFiles> choseFiles;
    private LinearLayout edit;

    public FileAdapter(Context context, List<MediaFiles> fileList,  List<MediaFiles> choseFiles, LinearLayout edit){
        this.context = context;
        this.fileList = fileList;
        this.choseFiles = choseFiles;
        this.edit = edit;
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
        ViewHolder viewHolder;
        if(convertView == null){
            convertView= LayoutInflater.from(context).inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.fileName = (TextView) convertView.findViewById(R.id.file_name);
            viewHolder.fileSize = (TextView) convertView.findViewById(R.id.file_size);
            viewHolder.fileImage = (ImageView) convertView.findViewById(R.id.file_image);
            viewHolder.checkBox = (SmoothCheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        final MediaFiles file = fileList.get(position);
        file.checkBox = viewHolder.checkBox;
        if(file.count == 1) {
            file.checkBox.setChecked(true);
        } else {
            file.checkBox.setChecked(false);
        }
        String filePath = file.getFilePath();
        switch (filePath.substring(filePath.lastIndexOf('.') + 1)){
            case "mp3" :
                viewHolder.fileImage.setBackgroundResource(R.drawable.music_normal);
                break;
            case "mp4":
            case "rmvb" :
            case "mkv" :
                viewHolder.fileImage.setBackgroundResource(R.drawable.video_normal);
                break;
            case "txt" :
                viewHolder.fileImage.setBackgroundResource(R.drawable.txt_normal);
                break;
            case "pdf" :
                viewHolder.fileImage.setBackgroundResource(R.drawable.pdf_normal);
                break;
            case "doc" :
            case "docx" :
                viewHolder.fileImage.setBackgroundResource(R.drawable.doc_normal);
                break;
            case "xlsx" :
                viewHolder.fileImage.setBackgroundResource(R.drawable.xlsx_normal);
                break;
            case "ppt" :
            case "pptx" :
                viewHolder.fileImage.setBackgroundResource(R.drawable.ppt_normal);
                break;
            case "zip" :
            case "rar" :
                viewHolder.fileImage.setBackgroundResource(R.drawable.zip_normal);
                break;
            default:
                viewHolder.fileImage.setBackgroundResource(R.drawable.file);
                break;
        }
        if(file.getArtist() != null){
            viewHolder.fileName.setText(file.getArtist() + " - " + file.getFileName() + ".mp3");
        } else {
            viewHolder.fileName.setText(file.getFilePath().substring(file.getFilePath().lastIndexOf('/') + 1));
        }
        StorageSize storageSize = new StorageSize();
        viewHolder.fileSize.setText(storageSize.typeChange(Double.parseDouble(file.getFileSize())));
        file.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.count == 0) {
                    file.checkBox.setChecked(true, true);
                    file.count = 1;
                    choseFiles.add(file);
                    edit.setVisibility(View.VISIBLE);
                } else {
                    file.checkBox.setChecked(false, true);
                    file.count = 0;
                    choseFiles.remove(file);
                    int aChoose;
                    for (aChoose = 0; aChoose < fileList.size(); aChoose++) {
                        if (fileList.get(aChoose).count == 1) {
                            break;
                        }
                    }
                    if (aChoose == fileList.size()) {
                        edit.setVisibility(View.GONE);
                    }
                }
            }
        });
        return convertView;
    }

    class ViewHolder{
        ImageView fileImage;
        TextView fileName;
        TextView fileSize;
        SmoothCheckBox checkBox;
    }

}