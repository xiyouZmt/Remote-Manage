package com.zmt.manager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zmt.manager.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Dangelo on 2016/7/24.
 */
public class PCFileAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, String>> list;

    public PCFileAdapter(Context context, List<Map<String, String>> list){
        this.list = list;
        this.context = context;
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
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.pc_file_item, null);
            viewHolder = new ViewHolder();
            viewHolder.fileImage = (ImageView) convertView.findViewById(R.id.file_image);
            viewHolder.fileName = (TextView) convertView.findViewById(R.id.file_name);
            viewHolder.fileSize = (TextView) convertView.findViewById(R.id.file_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.fileName.setText(list.get(position).get("fileName"));
//        if(list.get(position).get("fileStyle").equals("file")){
//            viewHolder.fileImage.setBackgroundResource(R.drawable.file);
//            viewHolder.fileSize.setText(list.get(position).get("fileLength"));
//        } else {
//            viewHolder.fileImage.setBackgroundResource(R.drawable.directory);
//            viewHolder.fileSize.setText(list.get(position).get("fileLength") + "项");
//        }
        if(list.get(position).get("fileStyle").equals("file")){
            /**
             * 设置文件大小
             */
            viewHolder.fileSize.setText(list.get(position).get("fileLength"));
            /**
             * 设置文件图片, 控件旋转至-90度
             */
            viewHolder.fileImage.setRotation(-90);
            String fileName = list.get(position).get("fileName");
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
            viewHolder.fileSize.setText(list.get(position).get("fileLength") + "项");
            viewHolder.fileImage.setBackgroundResource(R.drawable.directory);
        }

        return convertView;
    }

    class ViewHolder{
        ImageView fileImage;
        TextView fileName;
        TextView fileSize;
    }

}
