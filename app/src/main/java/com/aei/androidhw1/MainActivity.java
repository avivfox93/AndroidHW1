package com.aei.androidhw1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_ALL_PERMISSIONS = 15;

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
        getPermissions();
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

    public void getPermissions(){
        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int writeLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        ArrayList<String> permissions = new ArrayList<>();
        if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(writeLocationPermission != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissions.size() > 0)
            ActivityCompat.requestPermissions(this,
                    permissions.toArray(new String[0]),REQUEST_CODE_ALL_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_ALL_PERMISSIONS) {
            int grantResultsLength = grantResults.length;
            if (!(grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Please Grant Permissions!",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
