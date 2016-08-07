package com.example.manager.Application;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.net.Socket;

/**
 * Created by Dangelo on 2016/5/19.
 */
public class App extends Application {

    public static User user = new User();

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);
    }

    public User getUser(){
        return user;
    }
}
