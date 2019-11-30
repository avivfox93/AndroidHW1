package com.aei.androidhw1;

import android.app.Application;

public class MyApp extends Application {

    private static MySharedPrefs mySharedPrefs;
    private static MyVibrator myVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        mySharedPrefs = new MySharedPrefs(this);
        myVibrator = new MyVibrator(this);
    }

    public static MySharedPrefs getPrefs(){
        return mySharedPrefs;
    }

    public static MyVibrator getVibrator(){
        return myVibrator;
    }
}

