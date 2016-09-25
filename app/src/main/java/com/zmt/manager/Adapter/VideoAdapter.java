package com.zmt.manager.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
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
import com.zmt.manager.Utils.ImageLoader;
import com.zmt.manager.Utils.StorageSize;

import java.util.List;

/**
 * Created by Dangelo on 2016/8/4.
 */
public class VideoAdapter extends BaseAdapter {

    private Context context;
    private List<MediaFiles> videoList;
    private List<MediaFiles> choseFiles;
    private LinearLayout edit;
    private ImageLoader imageLoader;

    public VideoAdapter(Context context, List<MediaFiles> videoList, List<MediaFiles> choseFiles, LinearLayout edit){
        this.context = context;
        this.videoList = videoList;
        this.choseFiles = choseFiles;
        this.edit = edit;
        imageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.video_item, null);
            viewHolder = new ViewHolder();
            viewHolder.videoThumb = (ImageView) convertView.findViewById(R.id.videoThumb);
            viewHolder.videoName = (TextView) convertView.findViewById(R.id.videoName);
            viewHolder.videoSize = (TextView) convertView.findViewById(R.id.videoSize);
            viewHolder.checkBox = (SmoothCheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        final MediaFiles file = videoList.get(position);
        file.checkBox = viewHolder.checkBox;
        if(file.count == 1) {
            file.checkBox.setChecked(true);
        } else {
            file.checkBox.setChecked(false);
        }
        if(file.getFileThumb() == null){
            viewHolder.videoThumb.setBackgroundResource(R.drawable.pictures_no);
        }
        viewHolder.videoName.setText(file.getFileName());
        StorageSize storageSize = new StorageSize();
        viewHolder.videoSize.setText(storageSize.typeChange(Double.parseDouble(file.getFileSize())));
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
                    for (aChoose = 0; aChoose < videoList.size(); aChoose++) {
                        if (videoList.get(aChoose).count == 1) {
                            break;
                        }
                    }
                    if (aChoose == videoList.size()) {
                        edit.setVisibility(View.GONE);
                    }
                }
            }
        });
        BitmapWorker bitmapWorker = new BitmapWorker(viewHolder.videoThumb);
        bitmapWorker.execute(file.getFileThumb());
        return convertView;
    }

    class ViewHolder{
        ImageView videoThumb;
        TextView videoName;
        TextView videoSize;
        SmoothCheckBox checkBox;
    }

    class BitmapWorker extends AsyncTask{

        private ImageView imageView;

        public BitmapWorker(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Bitmap bitmap = (Bitmap)params[0];
            if(bitmap == null){
                return null;
            } else {
                return zoomImage(bitmap);
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if(o != null){
                imageView.setImageBitmap((Bitmap)o);
            }
        }
    }

    public Bitmap zoomImage(Bitmap bitmap) {
        // 获取这个图片的宽和高
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale((float)0.1, (float)0.1);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) width,
                (int) height, matrix, true);
        return newBitmap;
    }
}
