package com.example.manager.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.manager.Fragment.FileFragment;
import com.example.manager.Class.ImageFolder;
import com.example.manager.Class.MediaFiles;
import com.example.manager.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dangelo on 2016/4/3.
 */
public class LoadFile {

    private List<MediaFiles> musicList = new ArrayList<>();
    private List<MediaFiles> videoList = new ArrayList<>();
    private List<String> parentPath = new ArrayList<>();
    private List<MediaFiles> imageList = new ArrayList<>();
    private List<MediaFiles> wordList = new ArrayList<>();

    private List<ImageFolder> imageFolders = new ArrayList<>();
    private List<MediaFiles> zipList = new ArrayList<>();
    private List<MediaFiles> apkList = new ArrayList<>();
    private List<MediaFiles> filesList = new ArrayList<>();
    private List<MediaFiles> storage = new ArrayList<>();
    private Map<String, List<MediaFiles> > map = new HashMap<>();

    private Context context;

    public LoadFile(Context context){
        this.context = context;
    }

    public Cursor loadMusic(ContentResolver contentResolver){
        String musicSort = MediaStore.Audio.Media.TITLE;
        Cursor musicCursor = contentResolver.query(FileFragment.musicUri, null, null, null, musicSort);
        if(musicCursor != null){
            while(musicCursor.moveToNext()){
                String title = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String size = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                String path = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                MediaFiles song = new MediaFiles();
                song.setFileName(title);
                song.setFileSize(size);
                song.setFilePath(path);
                song.setArtist(artist);
                musicList.add(song);
            }
        }
        return musicCursor;
    }

    public Cursor loadVideo(ContentResolver contentResolver){
        String videoSort = MediaStore.Video.Media.DISPLAY_NAME;
        Cursor videoCursor = contentResolver.query(FileFragment.videoUri, null, null, null, videoSort);
        if(videoCursor != null){
            while(videoCursor.moveToNext()){
                String title = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String size = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                String path = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                MediaFiles video = new MediaFiles();
                video.setFileName(title);
                video.setFileSize(size);
                video.setFilePath(path);
                videoList.add(video);
            }
        }
        return videoCursor;
    }

    public Cursor loadImage(ContentResolver contentResolver){
        String imageSort = MediaStore.Images.Media.TITLE;
        Cursor imageCursor = contentResolver.query(FileFragment.imageUri, null, null, null, imageSort);
        if(imageCursor != null){
            while(imageCursor.moveToNext()){
                String path = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                File file = new File(path);
                if(file.exists()) {
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null) {
                        continue;
                    }
                    String folderPath = parentFile.getAbsolutePath();
                    if (!parentPath.contains(folderPath)) {
                        parentPath.add(folderPath);
                        ImageFolder imageFolder = new ImageFolder();
                        imageFolder.setFolderPath(folderPath);
                        imageFolder.setFirstPath(file.getPath());
                        imageFolder.setCount(getFileCount(folderPath));
                        imageFolders.add(imageFolder);
                    }
                }
            }
            parentPath = null;
        }
        return imageCursor;
    }

    public List<MediaFiles> loadWord (String path, boolean isIterative){
        File [] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isFile()){
                if(file.getPath().endsWith(".txt") || file.getPath().endsWith(".pdf")
                        || file.getPath().endsWith(".docx") || file.getPath().endsWith(".pptx")
                        || file.getPath().endsWith(".xlsx")) {
                    MediaFiles mediaFiles = new MediaFiles();
                    mediaFiles.setFileName(file.getName());
                    mediaFiles.setFilePath(file.getPath());
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        mediaFiles.setFileSize(String.valueOf(fis.available()));          //获取文件大小
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    wordList.add(mediaFiles);
                }
                if (!isIterative) {
                    break;
                }
            } else if(file.isDirectory()) {
                loadWord(file.getPath(), isIterative);
            }
        }
        return wordList;
    }

    public List<MediaFiles> loadZip (String path, boolean isIterative){
        File [] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isFile()){
                if(file.getPath().endsWith(".zip") || file.getPath().endsWith(".rar")){
                    MediaFiles mediaFiles = new MediaFiles();
                    mediaFiles.setFileName(file.getName());
                    mediaFiles.setFilePath(file.getPath());
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        mediaFiles.setFileSize(String.valueOf(fis.available()));          //获取文件大小
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    zipList.add(mediaFiles);
                }
                if (!isIterative) {
                    break;
                }
            } else if(file.isDirectory()) {
                loadZip(file.getPath(), isIterative);
            }
        }
        return zipList;
    }

    public Map< String, List<MediaFiles> > loadSuffixFiles (String path, boolean isIterative){
        File [] files = new File(path).listFiles();
        if(files != null && files.length != 0){
            for (File file : files) {
                if (file.isFile()) {
                    MediaFiles mediaFiles = new MediaFiles();
                    mediaFiles.setFileName(file.getName());
                    mediaFiles.setFilePath(file.getPath());
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        mediaFiles.setFileSize(String.valueOf(fis.available()));          //获取文件大小
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if ((file.getPath().endsWith(".txt") || file.getPath().endsWith(".pdf")
                            || file.getPath().endsWith("docx") || file.getPath().endsWith(".pptx")
                            || file.getPath().endsWith("xlsx"))) {
                        wordList.add(mediaFiles);
                    } else if (file.getPath().endsWith(".zip") || file.getPath().endsWith(".rar")) {
                        zipList.add(mediaFiles);
                    } else if (file.getPath().endsWith(".apk")) {
                        apkList.add(mediaFiles);
                    }
                    if (!isIterative) {
                        break;
                    }
                } else if (file.isDirectory()) {
                    File[] file1 = file.listFiles();
                    if (file1 != null && file1.length != 0) {
                        loadSuffixFiles(file.getAbsolutePath(), isIterative);
                    }
                }
            }
        }
        map.put("wordList", wordList);
        map.put("zipList", zipList);
        map.put("apkList", apkList);
        return map;
    }

    /**
     * 搜索目录， 文件后缀名， 是否进入子文件夹
     */
    public List<MediaFiles> loadFiles (String path, String suffix, boolean isIterative){
        File [] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isFile()){
                if(file.getPath().endsWith(suffix)) {
                    MediaFiles mediaFiles = new MediaFiles();
                    mediaFiles.setFileName(file.getName());
                    mediaFiles.setFilePath(file.getPath());
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        mediaFiles.setFileSize(String.valueOf(fis.available()));          //获取文件大小
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    filesList.add(mediaFiles);
                }
                if (!isIterative) {
                    break;
                }
            } else if(file.isDirectory()) {
                loadFiles(file.getPath(), suffix, isIterative);
            }
        }
        return filesList;
    }

    /**
     * 获取对应路径下的所有文件
     */
    public List<MediaFiles> loadStorage(String path){
        File [] files = new File(path).listFiles();
        for(File file : files){
            MediaFiles mediaFiles = new MediaFiles();
            mediaFiles.setFileName(file.getName());
            mediaFiles.setFilePath(file.getPath());
            if(file.isFile()){
                try {
                    FileInputStream fis = new FileInputStream(file);
                    mediaFiles.setFileSize(String.valueOf(fis.available()));          //获取文件大小
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaFiles.isFile = true;
            }
            storage.add(mediaFiles);
        }
        return storage;
    }

    /**
     * 获取上一级目录的全部文件
     */
    public List<MediaFiles> getLastFile(MediaFiles file){
        String path = file.getFilePath();
        File aFile = new File(path).getParentFile().getParentFile();
        File [] files = aFile.listFiles();
        storage.clear();
        for(File file1:files){
            MediaFiles mediaFiles = new MediaFiles();
            mediaFiles.setFilePath(file1.getPath());
            mediaFiles.setFileName(file1.getName());
            if(file1.isFile()){
                try {
                    FileInputStream fis = new FileInputStream(file1);
                    mediaFiles.setFileSize(String.valueOf(fis.available()));          //获取文件大小
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaFiles.isFile = true;
            }
            storage.add(mediaFiles);
        }
        return storage;
    }

    /**
     * 获取对应路径下的所有图片
     */
    public List<MediaFiles> getImage(String path){
        File [] files = new File(path).listFiles();
        for(File file : files){
            if(file.isFile() && (file.getAbsolutePath().endsWith("jpg")
                    || file.getAbsolutePath().endsWith(".png")
                        || file.getAbsolutePath().endsWith("gif")
                            || file.getAbsolutePath().endsWith("jpeg"))){
                MediaFiles mediaFiles = new MediaFiles();
                mediaFiles.setFileName(file.getName());
                mediaFiles.setFilePath(file.getPath());
                if(file.isFile()){
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        mediaFiles.setFileSize(String.valueOf(fis.available()));          //获取文件大小
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaFiles.isFile = true;
                }
                imageList.add(mediaFiles);
            }
        }
        return imageList;
    }

    /**
     * 获取对应路径下图片的个数
     */
    public int getFileCount(String path){
        int count = 0;
        File [] files = new File(path).listFiles();
        if(files == null){
            return 0;
        }
        for (File file : files) {
            if (file.isFile() && (file.getAbsolutePath().endsWith("jpg")
                    || file.getAbsolutePath().endsWith(".png")
                    || file.getAbsolutePath().endsWith("gif")
                    || file.getAbsolutePath().endsWith("jpeg"))) {
                count++;
            }
        }
        return count;
    }

    /**
     * 复制文件
     */
    public int copyFiles(String sourcePath, String targetPath){

        File file1 = new File(sourcePath);
        if(file1.isFile() && file1.exists()){
            try {
                File file = new File(targetPath);
                InputStream fis = new FileInputStream(sourcePath);
                OutputStream fos = new FileOutputStream(file.getParent() + '/' + file1.getName());
                byte [] b = new byte[1024];
                int c;
                while((c = fis.read(b)) > 0){
                    fos.write(b, 0, c);
                }
                fos.close();
                fis.close();
                return 1;
            } catch (IOException e) {
                Log.e("e", e.toString());
                return -1;
            }
        } else {
            File file2 = new File(targetPath + '/' + file1.getName());
            if(!file2.exists()) {
                file2.mkdirs();
            } else {
                return 0;
            }
            File [] files = file1.listFiles();
            if(files.length == 0){
                return 1;
            } else {
                for (File file : files) {
                    copyFiles(file.getAbsolutePath(), file2.getPath());
                }
                return 1;
            }
        }
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile() && file.exists()) {
                return file.delete();
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    return file.delete();
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                return file.delete();
            }
        }
        return false;
    }

    /**
     * 移动文件
     */
    public int moveFile(String oldPath, String newPath){
        File oldFile = new File(oldPath);
        if(!oldFile.exists()){
            return 0;
        }
        File newFile = new File(newPath);
        if(!newFile.exists()){
            newFile.mkdirs();
        }
        if(oldFile.isFile()){
            oldFile.renameTo(new File(newPath + File.separator + oldFile.getName()));
            return 1;
        } else if(oldFile.isDirectory()){
            File [] sourceFile = oldFile.listFiles();
            for(File file : sourceFile){
                if(file.isFile()){
                    oldFile.renameTo(new File(newPath + File.separator + file.getName()));
                }
                if(file.isDirectory()){
                    moveFile(file.getAbsolutePath(), newFile.getAbsolutePath() + File.separator + file.getName());
                }
            }
            return 1;
        }
        return 0;
    }

    public List<MediaFiles> getMusicList(){
        return musicList;
    }

    public List<MediaFiles> getVideoList(){
        return videoList;
    }

    public List<MediaFiles> getImageList(){
        return imageList;
    }

    public List<MediaFiles> getStorage(){
        return storage;
    }

    public List<ImageFolder> getFolderList(){
        return imageFolders;
    }

    public void addView(RelativeLayout no_files_image, RelativeLayout no_files_text, int resources){

//        LinearLayout linearLayout = new LinearLayout(context);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
//                (LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        linearLayout.setLayoutParams(layoutParams);

        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(resources);
        RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(dpToPx(50),dpToPx(50));
        imageView.setLayoutParams(imageLayoutParams);

        TextView textView = new TextView(context);
        textView.setText(R.string.no_files);
        textView.setTextSize(15);
        textView.setTextColor(context.getResources().getColor(R.color.color_8));
        RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textLayoutParams);

        no_files_image.addView(imageView);
        no_files_text.addView(textView);
    }

    public int dpToPx(float dp){
        float px = context.getResources().getDisplayMetrics().density;
        return (int)(dp * px + 0.5f);
    }

}

