package com.aei.androidhw1;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MyApp extends Application {

    private static final String SCORES_FILE_NAME = "ArakRacersScores.json";

    private static MySharedPrefs mySharedPrefs;
    private static MyVibrator myVibrator;
    private static SimpleDateFormat formatter;
    private static File scoreFile;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        mySharedPrefs = new MySharedPrefs(this);
        myVibrator = new MyVibrator(this);
        formatter = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
        context = this;
    }

    public static void saveScoreList(String json){
        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state))
            return;

        //Create a new file that points to the root directory, with the given name:
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), SCORES_FILE_NAME);

        //This point and below is responsible for the write operation
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();

            outputStream = new FileOutputStream(file, false);

            outputStream.write(json.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readScoreList(){

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return "";
        }

        //Create a new file that points to the root directory, with the given name:
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), SCORES_FILE_NAME);
        if(!file.exists())
            return "";

        //This point and below is responsible for the read operation
        try {
            StringBuilder result = new StringBuilder();
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                result.append(strLine);
            }
            in.close();
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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

