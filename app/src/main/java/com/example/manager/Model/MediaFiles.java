package com.example.manager.Model;

import com.example.manager.CheckBox.SmoothCheckBox;

import java.io.File;

/**
 * Created by Dangelo on 2016/4/3.
 */
public class MediaFiles{

    public String fileSize;
    public String fileName;
    public String artist;
    public String filePath;
    public SmoothCheckBox checkBox;
    public int count = 0;
    public boolean isFile = false;

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
