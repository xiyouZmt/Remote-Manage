package com.example.manager.Model;

/**
 * Created by Android on 2016/4/16.
 */
public class ImageFolder {

    /**
     * 图片文件夹名称
     */
    private String folderName;

    /**
     * 图片文件夹路径
     */
    private String folderPath;

    /**
     * 第一张图片的路径
     */
    private String firstImagePath;

    /**
     * 文件夹下图片数量
     */
    private int count;

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderPath(String folderPath){
        this.folderPath = folderPath;
    }

    public void setFirstImagePath(String firstImagePath){
        this.firstImagePath = firstImagePath;
    }

    public void setCount(int count){
        this.count = count;
    }

    public String getFolderPath(){
        return folderPath;
    }

    public String getFirstImagePath(){
        return firstImagePath;
    }

    public int getCount(){
        return count;
    }

}
