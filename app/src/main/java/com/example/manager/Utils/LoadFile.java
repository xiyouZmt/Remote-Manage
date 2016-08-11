package com.example.manager.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.manager.Fragment.FileFragment;
import com.example.manager.Model.ImageFolder;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private List<MediaFiles> imageList = new ArrayList<>();
    private List<String> folderNames = new ArrayList<>();
    private List<ImageFolder> imageFolders = new ArrayList<>();

    private List<MediaFiles> wordList = new ArrayList<>();
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
        String musicSort = MediaStore.Audio.Media.DISPLAY_NAME;
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
//        Cursor videoCursor = contentResolver.query(
//                Uri.parse("content://media/external/file"), null,
//                MediaStore.Files.FileColumns.DATA + " like ?",
//                new String[]{"%.mp4"}, videoSort);
        Cursor videoCursor = contentResolver.query(FileFragment.videoUri, null, null, null, videoSort);
        if(videoCursor != null){
            while(videoCursor.moveToNext()){
                String title = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String size = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                String path = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String thumb = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try{
                    retriever.setDataSource(thumb);
                }catch(IllegalArgumentException e){
                    Log.e("IllegalArgument--->", e.toString());
                }
                Bitmap bitmap = retriever.getFrameAtTime();
                retriever.release();
//                Bitmap bitmap = getVideoThumbnail(path, 180, 160,
//                        MediaStore.Images.Thumbnails.MICRO_KIND);
                MediaFiles video = new MediaFiles();
                video.setFileName(title);
                video.setFileSize(size);
                video.setFilePath(path);
                video.setFileThumb(bitmap);
                videoList.add(video);
            }
        }
        return videoCursor;
    }

    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public Cursor loadImage(ContentResolver contentResolver){
        String imageSort = MediaStore.Images.Media.DATE_ADDED;
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
                    String folderName = parentFile.getName();
                    if (!folderNames.contains(folderName)) {
                        folderNames.add(folderName);
                        ImageFolder imageFolder = new ImageFolder();
                        imageFolder.setFolderName(folderName);
                        imageFolder.setFolderPath(parentFile.getPath());
//                        imageFolder.setFirstImagePath(file.getPath());
                        imageFolder.setCount(getImageCount(parentFile.getPath()));
                        imageFolders.add(imageFolder);
                    }
                }
            }
        }
        return imageCursor;
    }

    public List<MediaFiles> loadWord(ContentResolver contentResolver){
        Cursor wordCursor = contentResolver.query(
                Uri.parse("content://media/external/file"), null,
                MediaStore.Files.FileColumns.DATA + "=? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ?",
                new String[]{"%.doc", "%.docx", "%.ppt", "%.pptx", "%.pdf", "%.xlsx", "%.xls", "%.txt"},
                MediaStore.Files.FileColumns.DATE_ADDED);
        if(wordCursor != null){
            while(wordCursor.moveToNext()){
                String title = wordCursor.getString(wordCursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                String size = wordCursor.getString(wordCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String path = wordCursor.getString(wordCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                File file = new File(path);
                if(file.exists() && file.isFile()){
                    MediaFiles word = new MediaFiles();
                    word.setFileName(title);
                    word.setFileSize(size);
                    word.setFilePath(path);
                    wordList.add(word);
                }
            }
            wordCursor.close();
        }
        return wordList;
    }

    public List<MediaFiles> loadWord (String path, boolean isIterative){
        File [] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isFile()){
                if(file.getPath().endsWith(".txt_rotate") || file.getPath().endsWith(".pdf_rotate")
                        || file.getPath().endsWith(".docx") || file.getPath().endsWith(".pptx")
                        || file.getPath().endsWith(".xlsx_rotate")) {
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

    public List<MediaFiles> loadZip(ContentResolver contentResolver){
        Cursor zipCursor = contentResolver.query(
                Uri.parse("content://media/external/file"), null,
                MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ?",
                new String[]{"%.zip", "%.rar"}, MediaStore.Files.FileColumns.DATE_ADDED);
        if(zipCursor != null){
            while(zipCursor.moveToNext()){
                String title = zipCursor.getString(zipCursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                String size = zipCursor.getString(zipCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String path = zipCursor.getString(zipCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                File file = new File(path);
                if(file.exists() && file.isFile()){
                    MediaFiles zip = new MediaFiles();
                    zip.setFileName(title);
                    zip.setFileSize(size);
                    zip.setFilePath(path);
                    zipList.add(zip);
                }
            }
            zipCursor.close();
        }
        return zipList;
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

    public List<MediaFiles> loadApk(ContentResolver contentResolver){
        List<MediaFiles> apkList = new ArrayList<>();
        Cursor apkCursor = contentResolver.query(
                Uri.parse("content://media/external/file"), null,
                MediaStore.Files.FileColumns.DATA + " like ?",
                new String[]{"%.apk"}, MediaStore.Files.FileColumns.DATE_ADDED);
        if(apkCursor != null){
            while(apkCursor.moveToNext()){
                String title = apkCursor.getString(apkCursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                String size = apkCursor.getString(apkCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String path = apkCursor.getString(apkCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                File file = new File(path);
                if(file.exists() && file.isFile()){
                    MediaFiles apk = new MediaFiles();
                    apk.setFileName(title);
                    apk.setFileSize(size);
                    apk.setFilePath(path);
                    apkList.add(apk);
                }
            }
            apkCursor.close();
        }
        return apkList;
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
                            || file.getPath().endsWith(".docx") || file.getPath().endsWith(".doc")
                            || file.getPath().endsWith(".pptx") || file.getPath().endsWith(".ppt")
                            || file.getPath().endsWith(".xlsx"))) {
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
            if(file.isFile() && file.exists()
                    && (file.getAbsolutePath().endsWith("jpg")
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
     * 获取对应路径下第一张图片的路径
     */
    public String getFirstImagePath(String path){
        File [] files = new File(path).listFiles();
        for (File file : files) {
            if(file.isFile() && file.exists()
                    && (file.getAbsolutePath().endsWith("jpg")
                    || file.getAbsolutePath().endsWith(".png")
                    || file.getAbsolutePath().endsWith("gif")
                    || file.getAbsolutePath().endsWith("jpeg"))) {
                return file.getPath();
            }
        }
        return null;
    }

    /**
     * 获取对应路径下图片的个数
     */
    public int getImageCount(String path){
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
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath + sourceFile.getName());
        if(targetFile.exists()){
            return 0;
        }
        if(sourceFile.isFile() && sourceFile.exists()){
            try {
                InputStream fis = new FileInputStream(sourceFile);
                OutputStream fos = new FileOutputStream(targetFile);
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
            targetFile = new File(targetPath + sourceFile.getName());
            if(!targetFile.exists()) {
                targetFile.mkdirs();
            } else {
                return 0;
            }
            File [] files = sourceFile.listFiles();
            if(files.length == 0){
                return 1;
            } else {
                for (File file : files) {
                    copyFiles(file.getAbsolutePath(), targetFile.getPath());
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
    public boolean moveFile(String oldPath, String newPath){
        boolean move;
        File oldFile = new File(oldPath);
        File newFile = new File(newPath + oldFile.getName());
        if(!oldFile.exists() || newFile.exists()
                || oldPath.equals(newPath + oldFile.getName()) ){
            move = false;
        } else if(oldFile.isFile()){
            move = oldFile.renameTo(new File(newPath + File.separator + oldFile.getName()));
        } else {
            if(!newFile.exists()){
                newFile.mkdirs();
                File [] sourceFile = oldFile.listFiles();
                for(File file : sourceFile){
                    if(file.isFile()){
                        oldFile.renameTo(new File(newFile.getPath() + '/' + File.separator + file.getName()));
                    }
                    if(file.isDirectory()){
                        moveFile(file.getAbsolutePath(), newFile.getAbsolutePath() + File.separator + file.getName());
                    }
                }
                move = true;
            } else {
                move = false;
            }
        }
        return move;
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

