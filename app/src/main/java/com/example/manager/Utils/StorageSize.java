package com.example.manager.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by Android on 2016/4/17.
 */
public class StorageSize {

    public String externalPath;

    /**
     * 获取手机内部存储空间
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getInternalMemorySize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(path.getPath());
        return Formatter.formatFileSize(context, statFs.getTotalBytes());
    }

    /**
     * 获取手机内部可用空间
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getAvailableInternalMemorySize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(path.getPath());
        return Formatter.formatFileSize(context, statFs.getAvailableBytes());
    }

    /**
     * 判断SD卡是否可用
     */
    public Object externalStorageAvailable(){
        File file = Environment.getExternalStorageDirectory().getParentFile().getParentFile();
        File [] files = file.listFiles();
        for (File file1:files) {
            if(file1.getName().contains("ext_sdcard")
                    || file1.getName().contains("sdcard1")
                        || file1.getName().contains("sdcard2")){
                File [] aFile = file1.listFiles();
                if(aFile != null) {
                    externalPath = file1.getAbsolutePath();
                    return file1.getAbsolutePath();
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 获取SD卡存储空间
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getExternalMemorySize(Context context){
        if(externalPath != null) {
            StatFs statFs = new StatFs(externalPath);
            return Formatter.formatFileSize(context, statFs.getTotalBytes());
        } else {
            return null;
        }
    }

    /**
     * 获取SD卡可用空间
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getAvailableExternalMemorySize(Context context){
        if(externalPath != null) {
            StatFs statFs = new StatFs(externalPath);
            return Formatter.formatFileSize(context, statFs.getAvailableBytes());
        } else {
            return null;
        }
    }

    /**
     * 类型转换
     */
    public String typeChange(Double size){
        DecimalFormat df = new DecimalFormat("#.00");
        if(size > (1024 *1024 * 1024)){
            return df.format(size / 1024 / 1024 / 1024) + "GB";
        } else if(size > (1024 * 1024)){
            return df.format(size / 1024 / 1024) + "MB";
        } else if(size > 1024){
            return df.format(size / 1024) + "KB";
        } else {
            return new DecimalFormat("#").format(size) + "B";
        }
    }

}
