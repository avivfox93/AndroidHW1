package com.aei.androidhw1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class ScoreBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        TableLayout scoreBoardLayout = findViewById(R.id.score_board_list_layout);
        ArrayList<Score> scoreArrayList = getScoreList(this);
        for(int i = 0 ; i < scoreArrayList.size() ; i++){
            TableRow row = new TableRow(this);
            TextView place = new TextView(this);
            place.setTextSize(30);
            place.setText(String.format(Locale.ENGLISH,"(%d)",i+1));
            row.addView(place);
            TextView name = new TextView(this);
            name.setPadding(25,5,25,5);
            name.setTextSize(30);
            name.setText(scoreArrayList.get(i).getName());
            row.addView(name);
            TextView time = new TextView(this);
            time.setTextSize(30);
            time.setText(scoreArrayList.get(i).getPrettyTime());
            time.setTextColor(Color.GREEN);
            row.addView(time);
            scoreBoardLayout.addView(row);
        }
        findViewById(R.id.close_btn).setOnClickListener(e-> finish());
    }

    public static ArrayList<Score> getScoreList(Context cntx){
        String scoresStr = MyApp.getPrefs().getString(cntx.getString(R.string.score_prefs),"");
        ArrayList<Score> scoreList = new ArrayList<>();
        if(scoresStr.isEmpty())
            return scoreList;
        for(String str : scoresStr.split(","))
            scoreList.add(Score.fromString(str));
        Collections.sort(scoreList);
        return scoreList;
    }

    public static void saveScoreList(Context cntx, ArrayList<Score> scoreList){
        if(scoreList.isEmpty())
            return;
        StringBuilder sb = new StringBuilder();
        sb.append(scoreList.get(0));
        for(int i = 1 ; i < scoreList.size() ; i++)
            sb.append(',').append(scoreList.get(i));
        MyApp.getPrefs().putString(cntx.getString(R.string.score_prefs),sb.toString());
    }
}

class Score implements Comparable<Score>{
    private String name;
    private int time;
    public Score(String name, int time){
        this.name = name;
        this.time = time;
    }
    public static Score fromString(String str){
        if(!str.contains(":"))
            return null;
        String[] arr = str.split(":");
        String name = arr[0];
        int time = Integer.valueOf(arr[1]);
        return new Score(name,time);
    }

    @Override
    public String toString(){
        return this.name + ":" + this.time;
    }


    public String getPrettyTime(){
        return MyApp.getDateFormatter().format(new Date(time*1000));
    }

    @Override
    public int compareTo(Score o) {
        return o.getTime() - this.time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}