package com.example.spms;

import android.app.Application;

import java.util.HashMap;

public class MainApplication extends Application {

    private static MainApplication mApp;
    public HashMap<String,String> mInfoMap= new HashMap<>();

    public static MainApplication getInstance(){
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }
}
