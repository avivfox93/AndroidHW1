package com.aei.androidhw1;

import android.app.Application;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyApp extends Application {

    private static MySharedPrefs mySharedPrefs;
    private static MyVibrator myVibrator;
    private static SimpleDateFormat formatter;

    @Override
    public void onCreate() {
        super.onCreate();
        mySharedPrefs = new MySharedPrefs(this);
        myVibrator = new MyVibrator(this);
        formatter = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
    }

    public static MySharedPrefs getPrefs(){
        return mySharedPrefs;
    }

    public static MyVibrator getVibrator(){
        return myVibrator;
    }

    public static SimpleDateFormat getDateFormatter(){
        return formatter;
    }
}

