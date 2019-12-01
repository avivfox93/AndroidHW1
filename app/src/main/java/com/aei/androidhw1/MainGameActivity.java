package com.aei.androidhw1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MainGameActivity extends AppCompatActivity implements SensorEventListener {

    public static final int NUM_OF_LIVES = 3;
    public static final long GAME_SPEED_FAST = 750, GAME_SPEED_SLOW = 1500, GAME_SPEED_NORMAL = 1000;
    public static final int NUM_OF_LANES = 3;
    public static final int NUM_OF_ROWS = 4;
    private ImageView[][] roadObjects = new ImageView[NUM_OF_ROWS + 1][NUM_OF_LANES];
    private GridLayout gridLayout;
    private Button restartButton;
    private Button exitButton;
    private Runnable backgroundRunnable, gameTimerRunnable;
    private long gameSpeed;
    private ImageView[] hearts = new ImageView[NUM_OF_LIVES];
    private TextView gameOverText;
    private SensorManager sensorManager;
    private final Handler mHandler = new Handler();
    private boolean nowPlaying = true, paused = false, ended = false;
    private MediaPlayer backgroundPlayer, collidePlayer;
    private long secondsPlayed = 0;

    private Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        backgroundPlayer = MediaPlayer.create(this, R.raw.the_entertainer_8_bit);
        backgroundPlayer.setLooping(true);
        collidePlayer = MediaPlayer.create(this, R.raw.vodka_colide_sound);
        Button leftBtn = findViewById(R.id.turn_left_btn);
        Button rightBtn = findViewById(R.id.turn_right_btn);
        restartButton = findViewById(R.id.restart_btn);
        exitButton = findViewById(R.id.exit_btn);
        gameOverText = findViewById(R.id.game_over_text);
        gridLayout = findViewById(R.id.GameGrid);
        gridLayout.setRowCount(NUM_OF_ROWS + 1);
        gridLayout.setColumnCount(NUM_OF_LANES);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        restartButton.setOnClickListener(e->{
            showEndGameWindow(false);
            resetGame();
            startGame();
            car.reset();
        });
        exitButton.setOnClickListener(e-> finish());
        MainActivity.GameSpeed speed = (MainActivity.GameSpeed) getIntent().getSerializableExtra(getString(R.string.speed_prefs));
        switch (speed){
            case FAST:
                gameSpeed = GAME_SPEED_FAST;
                break;
            case SLOW:
                gameSpeed = GAME_SPEED_SLOW;
                break;
            default:
                gameSpeed = GAME_SPEED_NORMAL;
                break;
        }
        boolean tilt = getIntent().getBooleanExtra(getString(R.string.tilt_mode_prefs),false);
        if(tilt){
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            leftBtn.setVisibility(View.INVISIBLE);
            rightBtn.setVisibility(View.INVISIBLE);
        }else{
            leftBtn.setOnClickListener(e->car.move(Car.Direction.LEFT));
            rightBtn.setOnClickListener(e->car.move(Car.Direction.RIGHT));
        }
        boolean sound = getIntent().getBooleanExtra(getString(R.string.sound_mode_prefs),true);
        if(!sound){
            backgroundPlayer.setVolume(0,0);
            collidePlayer.setVolume(0,0);
        }
        resetGame();
        car = new Car(roadObjects[NUM_OF_ROWS],hearts,collidePlayer);
    }

    private void initLives(){
        LinearLayout heartsLayout = findViewById(R.id.hearts_layout);
        heartsLayout.removeAllViews();
        for(int i = 0 ; i < hearts.length ; i++) {
            hearts[i] = new ImageView(this);
            hearts[i].setImageResource(R.drawable.ic_love_heart_svg);
            hearts[i].setVisibility(View.VISIBLE);
            heartsLayout.addView(hearts[i]);
        }
    }

    private void showEndGameWindow(boolean visible){
        int val = visible ? View.VISIBLE : View.INVISIBLE;
        restartButton.setVisibility(val);
        exitButton.setVisibility(val);
        gameOverText.setVisibility(val);
        if(visible){
            Score score = new Score("eled",(int)secondsPlayed);
            ArrayList<Score> scores = ScoreBoard.getScoreList(this);
            if(scores.size() >= 10 && scores.get(9).compareTo(score) <= 0)
                return;
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.score_promt, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            final EditText input = promptsView.findViewById(R.id.editTextDialogUserInput);
            alertDialogBuilder.setView(promptsView);
            alertDialogBuilder.setCancelable(false).setPositiveButton(android.R.string.ok,(dialog, which) -> {
                dialog.dismiss();
                score.setName(input.getText().toString().replace(",","").replace(":",""));
                if(scores.size() >= 10)
                    scores.remove(9);
                scores.add(score);
                ScoreBoard.saveScoreList(this,scores);
                openScoreBoard();
            }).create().show();
        }
    }

    private void openScoreBoard(){
        Intent intent = new Intent(this, ScoreBoard.class);
        startActivity(intent);
    }

    private void endGame(){
        stopGame();
        backgroundPlayer.seekTo(0);
        showEndGameWindow(true);
        ended = true;
    }

    private void stopGame(){
        paused = true;
        nowPlaying = false;
        backgroundPlayer.pause();
    }

    private void resetGame(){
        secondsPlayed = 0;
        ended = false;
        SimpleDateFormat formatter= new SimpleDateFormat("mm:ss", Locale.ENGLISH);
        ((TextView)findViewById(R.id.TimerText)).setText(formatter.format(new Date(0)));
        initGrid();
        initLives();
    }

    private void startGame(){
        backgroundPlayer.start();
        nowPlaying = true;
        backgroundRunnable = () -> {
            if(!nowPlaying)
                return;
            runOnUiThread(()->{
                for(int i = 0 ; i < NUM_OF_LANES ; i++){
                    Bottle obj = (Bottle)roadObjects[NUM_OF_ROWS-1][i];
                    if(car.getPos() == i)
                        car.collide(obj);
                    obj.setVisibility(View.INVISIBLE);
                }
                for(int i = NUM_OF_ROWS - 1 ; i > 0 ; i--) {
                    for (int j = 0; j < NUM_OF_LANES; j++)
                        roadObjects[i][j].setVisibility(roadObjects[i - 1][j].getVisibility());
                }
                int newPos = (int)(Math.random()*(NUM_OF_LANES+1));
                for(int i = 0 ; i < NUM_OF_LANES ; i++){
                    roadObjects[0][i].setVisibility(newPos == i ? View.VISIBLE : View.INVISIBLE);
                }
                if(car.getLives() <= 0)
                    endGame();
                mHandler.postDelayed(backgroundRunnable,gameSpeed);
            });
        };
        mHandler.postDelayed(backgroundRunnable,gameSpeed);
        gameTimerRunnable = ()->{
            if(!nowPlaying)
                return;
            secondsPlayed++;
            runOnUiThread(()->{
                Date d = new Date(secondsPlayed*1000);
                SimpleDateFormat formatter= new SimpleDateFormat("mm:ss", Locale.ENGLISH);
                ((TextView)findViewById(R.id.TimerText)).setText(formatter.format(d));
            });
            mHandler.postDelayed(gameTimerRunnable,1000);
        };
        mHandler.postDelayed(gameTimerRunnable,1000);
    }

    private void initGrid(){
        gridLayout.removeAllViews();
        int newPos = (int)(Math.random()*(NUM_OF_LANES+1));
        for(int i = 0 ; i < NUM_OF_ROWS ; i++){
            for(int j = 0 ; j < NUM_OF_LANES ; j++) {
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.rowSpec = GridLayout.spec(i,1,GridLayout.CENTER,1);
                layoutParams.columnSpec = GridLayout.spec(j,1,GridLayout.CENTER,1);
                Bottle img = new Bottle(this);
                img.setAsVodka();
                img.setForegroundGravity(Gravity.CENTER);
                img.setScaleType(ImageView.ScaleType.CENTER);
                img.setRotation((float)(Math.random()*20 - 10));
                roadObjects[i][j] = img;
                img.setVisibility(j == newPos && i == 0 ? View.VISIBLE : View.INVISIBLE);
                gridLayout.addView(img,layoutParams);
            }
        }
        for(int i = 0 ; i < NUM_OF_LANES ; i++) {
            ImageView img = new ImageView(this);
            img.setImageResource(R.drawable.pickup_truck);
            img.setScaleType(ImageView.ScaleType.CENTER);
            img.setForegroundGravity(Gravity.CENTER);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.rowSpec = GridLayout.spec(NUM_OF_ROWS);
            layoutParams.columnSpec = GridLayout.spec(i,1,GridLayout.CENTER,1);
            roadObjects[NUM_OF_ROWS][i] = img;
            img.setVisibility(i == NUM_OF_LANES/2 ? View.VISIBLE : View.INVISIBLE);
            gridLayout.addView(img,layoutParams);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
        int side = (int) event.values[0];
        int speed = (int) event.values[1];
        if(side >= 3)
            car.move(Car.Direction.LEFT);
        else if(side <= -3)
            car.move(Car.Direction.RIGHT);
        if(speed > 4)
            gameSpeed = GAME_SPEED_SLOW;
        else if(speed < -4)
            gameSpeed = GAME_SPEED_FAST;
        else
            gameSpeed = GAME_SPEED_NORMAL;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onStop(){
        super.onStop();
        stopGame();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(ended)
            return;
        if(!paused)
            resetGame();
        startGame();
        paused = false;
        if(sensorManager == null)
            return;
        // Register this class as a listener for the accelerometer sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        nowPlaying = false;
        backgroundPlayer.stop();
        backgroundPlayer.reset();
        collidePlayer.stop();
        collidePlayer.reset();
    }
}
