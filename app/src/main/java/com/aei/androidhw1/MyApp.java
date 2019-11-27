package com.aei.androidhw1;

import android.app.Application;

public class MyApp extends Application {

    private static MySharedPrefs mySharedPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        mySharedPrefs = new MySharedPrefs(this);
    }

    public static MySharedPrefs getPrefs(){
        return mySharedPrefs;
    }
}

