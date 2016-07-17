package com.example.manager.Class;

import android.widget.ImageView;

import java.io.File;

/**
 * Created by Dangelo on 2016/4/3.
 */
public class MediaFiles{

    public int fileImage;
    public String fileSize;
    public String fileName;
    public String artist;
    public String filePath;
    public ImageView check;
    public int count = 0;
    public boolean isFile = false;

    public void setFileImage(int fileImage){
        this.fileImage = fileImage;
    }

    public void setFileSize(String fileSize){
        this.fileSize = fileSize;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public int getFileImage(){
        return fileImage;
    }

    public String getFileSize(){
        return fileSize;
    }

    public String getFileName(){
        return fileName;
    }

    public String getArtist(){
        return artist;
    }

    public String getFilePath(){
        return filePath;
    }

    /**
     * 获取文件夹下目录的个数
     */
    public int getItemCount(String path){
        if(!isFile){
            File [] files = new File(path).listFiles();
            return files.length;
        } else {
            return 1;
        }
    }

}
