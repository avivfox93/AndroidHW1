package com.aei.androidhw1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    enum GameSpeed {
        FAST, NORMAL, SLOW
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startBtn = findViewById(R.id.start_btn);
        Button settingsBtn = findViewById(R.id.settings_btn);
        Button scoreBoardBtn = findViewById(R.id.score_board_btn);
        startBtn.setOnClickListener(e -> startGame(GameSpeed.valueOf(MyApp.getPrefs()
                .getString(getString(R.string.speed_prefs),getString(R.string.default_speed))),
                MyApp.getPrefs().getBoolean(getString(R.string.sound_mode_prefs),false),
                MyApp.getPrefs().getBoolean(getString(R.string.tilt_mode_prefs),false)));
        settingsBtn.setOnClickListener(e->openSettings());
        scoreBoardBtn.setOnClickListener(e->openScoreBoard());
    }

    /**
     *
     * @param type GameSpeed(SLOW, NORMAL, FAST)
     * @param sound Sound on/off
     * @param tilt Tilt mode on/off
     */
    private void startGame(GameSpeed type, boolean sound, boolean tilt) {
        Intent intent = new Intent(this, MainGameActivity.class);
        intent.putExtra(getString(R.string.speed_prefs), type);
        intent.putExtra(getString(R.string.sound_mode_prefs),sound);
        intent.putExtra(getString(R.string.tilt_mode_prefs),tilt);
        startActivity(intent);
    }

    /**
     * Open Settings Activity
     */
    private void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Open ScoreBoard Activity
     */
    private void openScoreBoard(){
        Intent intent = new Intent(this, ScoreBoard.class);
        startActivity(intent);
    }
}
