package com.example.manager.Class;

/**
 * Created by Android on 2016/4/16.
 */
public class ImageFolder {

    /**
     * 图片文件夹路径
     */
    private String folderPath;

    /**
     * 第一张图片的路径
     */
    private String firstPath;

    /**
     * 图片数量
     */
    private int count;

    public void setFolderPath(String folderPath){
        this.folderPath = folderPath;
    }

    public void setFirstPath(String firstPath){
        this.firstPath = firstPath;
    }

    public void setCount(int count){
        this.count = count;
    }

    public String getFolderPath(){
        return folderPath;
    }

    public String getFirstPath(){
        return firstPath;
    }

    public int getCount(){
        return count;
    }

}
