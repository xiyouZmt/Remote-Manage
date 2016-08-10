package com.example.manager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.manager.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Dangelo on 2016/7/27.
 */
public class DiskAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Map<String, String>> list;

    public DiskAdapter(Context context, List<Map<String, String>> list) {
        layoutInflater = LayoutInflater.from(context);
        this.list = list;
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
        LinearLayout linearLayout;
        ViewHolder viewHolder;
        if (convertView == null) {
            linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.driver_item, null);
            viewHolder = new ViewHolder();
            viewHolder.driverName = (TextView) linearLayout.findViewById(R.id.driverName);
            viewHolder.totalSize = (TextView) linearLayout.findViewById(R.id.totalSize);
            viewHolder.availableSize = (TextView) linearLayout.findViewById(R.id.availableSize);
            viewHolder.progressBar = (ProgressBar) linearLayout.findViewById(R.id.diskProgressBar);
            linearLayout.setTag(viewHolder);
        } else {
            linearLayout = (LinearLayout) convertView;
            viewHolder = (ViewHolder) linearLayout.getTag();
        }
        String total = list.get(position).get("totalSize");
        String available = list.get(position).get("availableSize");
        viewHolder.driverName.setText(list.get(position).get("diskName"));
        viewHolder.totalSize.setText("共" + total);
        viewHolder.availableSize.setText(available + "可用");
        /**
         * 总空间大小
         */
        String allSize = total.substring(0, total.lastIndexOf('B') - 1);
        String allSizeToInteger = String.valueOf(Double.parseDouble(allSize) * 100);
        int allSizeForInteger = Integer.parseInt(allSizeToInteger.substring(0, allSizeToInteger.lastIndexOf('.')));
        /**
         * 已用空间大小
         */
        String hasUsedSize = available.substring(0, available.lastIndexOf('B') - 1);
        String hasUsedSizeToInteger = String.valueOf(Double.parseDouble(hasUsedSize) * 100);
        int hasUsedSizeForInteger = Integer.parseInt(hasUsedSizeToInteger.substring(0, hasUsedSizeToInteger.lastIndexOf('.')));

        viewHolder.progressBar.setMax(allSizeForInteger);
        viewHolder.progressBar.setProgress(allSizeForInteger - hasUsedSizeForInteger);

        return linearLayout;
    }

    class ViewHolder {
        public TextView driverName;
        public TextView totalSize;
        public TextView availableSize;
        public ProgressBar progressBar;
    }
}
