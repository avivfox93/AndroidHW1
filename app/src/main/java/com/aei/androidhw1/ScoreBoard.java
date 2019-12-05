package com.aei.androidhw1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
        findViewById(R.id.open_map_btn).setOnClickListener(e->{
            Intent intent = new Intent(this, ScoresMap.class);
            startActivity(intent);
        });
    }

    /**
     * Load ScoreList using SharedPrefs
     * @param cntx context
     * @return Sorted ScoreList
     */
    public static ArrayList<Score> getScoreList(Context cntx){
        Gson gson = new Gson();
        String scoresStr = MyApp.readScoreList();
        ArrayList<Score> scoreList = null;
        try {
            scoreList = gson.fromJson(scoresStr, new TypeToken<List<Score>>() {
            }.getType());
        }catch (RuntimeException e){
            Log.e("SCORES",scoresStr);
            e.printStackTrace();
        }
        if(scoreList == null)
            return new ArrayList<>();
        Collections.sort(scoreList);
        return scoreList;
    }

    /**
     * Save ScoreList using SharedPrefs
     * @param cntx context
     * @param scoreList ScoreList to save
     */
    public static void saveScoreList(Context cntx, ArrayList<Score> scoreList){
        Gson gson = new Gson();
        MyApp.saveScoreList(gson.toJson(scoreList));
    }
}

class Score implements Comparable<Score>{
    private String name;
    private int time;
    private MyLocation location;
    public Score(String name, int time){
        this.name = name;
        this.time = time;
    }

    public void setLocation(MyLocation location){
        this.location = location;
    }

    public MyLocation getLocation(){
        return location;
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