package com.example.manager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manager.CheckBox.SmoothCheckBox;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;
import com.example.manager.Utils.LoadFile;
import com.example.manager.Utils.StorageSize;

import java.util.List;

/**
 * Created by Dangelo on 2016/8/4.
 */
public class StorageAdapter extends BaseAdapter {

    private Context context;
    private List<MediaFiles> list;
    private LoadFile loadFile;
    private List<MediaFiles> choseFiles;
    private LinearLayout edit;

    public StorageAdapter(Context context, List<MediaFiles> list, LoadFile loadFile, List<MediaFiles> choseFiles, LinearLayout edit) {
        this.context = context;
        this.list = list;
        this.loadFile = loadFile;
        this.choseFiles = choseFiles;
        this.edit = edit;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.storage_item, null);
            viewHolder = new ViewHolder();
            viewHolder.fileName = (TextView)convertView.findViewById(R.id.file_name);
            viewHolder.fileSize = (TextView)convertView.findViewById(R.id.file_size);
            viewHolder.fileImage = (ImageView)convertView.findViewById(R.id.file_image);
            viewHolder.checkBox = (SmoothCheckBox)convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder =(ViewHolder)convertView.getTag();
        }
        final MediaFiles file = list.get(position);
        file.checkBox = viewHolder.checkBox;
        if(file.count == 1) {
            file.checkBox.setChecked(true);
        } else {
            file.checkBox.setChecked(false);
        }
        viewHolder.fileName.setText(file.getFileName());
        if(file.isFile){
            /**
             * 设置文件大小
             */
            StorageSize storageSize = new StorageSize();
            viewHolder.fileSize.setText(storageSize.typeChange(Double.parseDouble(file.getFileSize())));
            /**
             * 设置文件图片, 控件旋转至-90度
             */
            viewHolder.fileImage.setRotation(-90);
            String fileName = file.getFileName();
            switch (fileName.substring(fileName.lastIndexOf('.') + 1)){
                case "mp3" :
                    viewHolder.fileImage.setBackgroundResource(R.drawable.music_rotate);
                    break;
                case "mp4":
                case "rmvb" :
                case "mkv" :
                    viewHolder.fileImage.setBackgroundResource(R.drawable.video_rotate);
                    break;
                case "txt" :
                    viewHolder.fileImage.setBackgroundResource(R.drawable.txt_rotate);
                    break;
                case "pdf" :
                    viewHolder.fileImage.setBackgroundResource(R.drawable.pdf_rotate);
                    break;
                case "doc" :
                case "docx" :
                    viewHolder.fileImage.setBackgroundResource(R.drawable.doc_rotate);
                    break;
                case "xlsx" :
                    viewHolder.fileImage.setBackgroundResource(R.drawable.xlsx_rotate);
                    break;
                case "ppt" :
                case "pptx" :
                    viewHolder.fileImage.setBackgroundResource(R.drawable.ppt_rotate);
                    break;
                case "zip" :
                case "rar" :
                    viewHolder.fileImage.setBackgroundResource(R.drawable.zip_rotate);
                    break;
                default:
                    viewHolder.fileImage.setBackgroundResource(R.drawable.file);
                    break;
            }
        } else {
            /**
             * 文件夹中项目的个数，旋转至0度
             */
            viewHolder.fileImage.setRotation(0);
            viewHolder.fileSize.setText(file.getItemCount(file.getFilePath()) + "项");
            viewHolder.fileImage.setBackgroundResource(R.drawable.directory);
        }
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
                    for (aChoose = 0; aChoose < loadFile.getStorage().size(); aChoose++) {
                        if (loadFile.getStorage().get(aChoose).count == 1) {
                            break;
                        }
                    }
                    if (aChoose == loadFile.getStorage().size()) {
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
