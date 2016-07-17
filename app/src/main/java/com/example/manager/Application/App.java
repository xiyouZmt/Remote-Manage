package com.example.manager.Application;

import android.app.Application;

import java.net.Socket;

/**
 * Created by Dangelo on 2016/5/19.
 */
public class App extends Application {

    public static User user = new User();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public User getUser(){
        return user;
    }
}
