package com.example.manager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manager.R;

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
        if(list.get(position).get("fileStyle").equals("file")){
            viewHolder.fileImage.setBackgroundResource(R.drawable.file);
            viewHolder.fileSize.setText(list.get(position).get("fileLength"));
        } else {
            viewHolder.fileImage.setBackgroundResource(R.drawable.directory);
            viewHolder.fileSize.setText(list.get(position).get("fileLength") + "项");
        }
        return convertView;
    }

    class ViewHolder{
        ImageView fileImage;
        TextView fileName;
        TextView fileSize;
    }

}
