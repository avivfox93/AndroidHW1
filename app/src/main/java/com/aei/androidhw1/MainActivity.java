package com.aei.androidhw1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    enum GameType{
        FAST,SLOW,TILT

        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startFast = findViewById(R.id.FastBtn);
        Button startSlow = findViewById(R.id.SlowBtn);
        Button startTilt = findViewById(R.id.TiltBtn);
        startFast.setOnClickListener(e->startGame(GameType.FAST));
        startSlow.setOnClickListener(e->startGame(GameType.SLOW));
        startTilt.setOnClickListener(e->startGame(GameType.TILT));
    }

    private void startGame(GameType type){
        Intent intent = new Intent(this,MainGame.class);
        intent.putExtra("gameType",type);
        startActivity(intent);
    }
}
