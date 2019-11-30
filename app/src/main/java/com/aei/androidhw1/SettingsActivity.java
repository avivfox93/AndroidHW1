package com.aei.androidhw1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private boolean tilt,sound;
    private MainActivity.GameSpeed speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ArrayList<String> speeds = new ArrayList<>();
        for(MainActivity.GameSpeed s : MainActivity.GameSpeed.values())
            speeds.add(s.toString());
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,speeds);
        Switch tiltSwitch = findViewById(R.id.tilt_switch);
        Switch soundSwitch = findViewById(R.id.sound_switch);
        Spinner speedSelector = findViewById(R.id.speed_selector);
        Button saveBtn = findViewById(R.id.save_btn);
        Button cancelBtn = findViewById(R.id.cancel_btn);

        speedSelector.setAdapter(listAdapter);

        tiltSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> tilt = isChecked);

        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked)-> sound = isChecked);

        speedSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position < 0)
                    return;
                speed = MainActivity.GameSpeed.valueOf(speeds.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tilt = MyApp.getPrefs().getBoolean(getString(R.string.tilt_mode_prefs),false);
        sound = MyApp.getPrefs().getBoolean(getString(R.string.sound_mode_prefs),true);
        speed = MainActivity.GameSpeed.valueOf(MyApp.getPrefs()
                .getString(getString(R.string.speed_prefs),getString(R.string.default_speed)));

        tiltSwitch.setChecked(tilt);
        soundSwitch.setChecked(sound);
        speedSelector.setSelection(speeds.indexOf(speed.toString()));

        saveBtn.setOnClickListener(e->{
            MyApp.getPrefs().putBoolean(getString(R.string.tilt_mode_prefs),tilt);
            MyApp.getPrefs().putBoolean(getString(R.string.sound_mode_prefs),sound);
            MyApp.getPrefs().putString(getString(R.string.speed_prefs),speed.toString());
            finish();
        });
        cancelBtn.setOnClickListener(e->finish());
    }
}
