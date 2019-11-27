package com.aei.androidhw1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainGame extends AppCompatActivity implements SensorEventListener {

    public static final int NUM_OF_LIVES = 3;
    public static final long GAME_SPEED_FAST = 750, GAME_SPEED_SLOW = 1500, GAME_SPEED_NORMAL = 1000;
    public static final int NUM_OF_LANES = 3;
    public static final int NUM_OF_ROWS = 4;
    private ImageView[][] roadObjects = new ImageView[NUM_OF_ROWS + 1][NUM_OF_LANES];
    private GridLayout gridLayout;
    private Button restartButton;
    private Button exitButton;
    private int carPos = NUM_OF_LANES/2;
    private Runnable backgroundRunnable, gameTimerRunnable;
    private long gameSpeed ;
    private int lives = NUM_OF_LIVES, coins = 0;
    private ImageView[] hearts = new ImageView[3];
    private TextView gameOverText;
    private SensorManager sensorManager;
    private final Handler mHandler = new Handler();
    private boolean nowPlaying = true;
    private MediaPlayer backgroundPlayer, collidePlayer;
    private long secondsPlayed = 0;

    private enum Direction{
        LEFT,RIGHT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        backgroundPlayer = MediaPlayer.create(this, R.raw.the_entertainer_8_bit);
        backgroundPlayer.setLooping(true);
        collidePlayer = MediaPlayer.create(this, R.raw.vodka_colide_sound);
        Button leftBtn = findViewById(R.id.TurnLeftBtn);
        Button rightBtn = findViewById(R.id.TurnRightBtn);
        restartButton = findViewById(R.id.RestartButton);
        exitButton = findViewById(R.id.ExitButton);
        gameOverText = findViewById(R.id.GameOverText);
        gridLayout = findViewById(R.id.GameGrid);
        gridLayout.setRowCount(NUM_OF_ROWS + 1);
        gridLayout.setColumnCount(NUM_OF_LANES);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        restartButton.setOnClickListener(e->{
            showEndGameWindow(false);
            startGame();
        });
        exitButton.setOnClickListener(e-> finish());
        MainActivity.GameType gameType = (MainActivity.GameType) getIntent().getSerializableExtra("gameType");
        switch (gameType){
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
        if(gameType == MainActivity.GameType.TILT){
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            leftBtn.setVisibility(View.INVISIBLE);
            rightBtn.setVisibility(View.INVISIBLE);
        }else{
            leftBtn.setOnClickListener(e->moveCar(Direction.LEFT));
            rightBtn.setOnClickListener(e->moveCar(Direction.RIGHT));
        }
        startGame();
    }

    private void showEndGameWindow(boolean visible){
        int val = visible ? View.VISIBLE : View.INVISIBLE;
        restartButton.setVisibility(val);
        exitButton.setVisibility(val);
        gameOverText.setVisibility(val);
    }

    private void stopGame(){
        nowPlaying = false;
        backgroundPlayer.pause();
        backgroundPlayer.seekTo(0);
        showEndGameWindow(true);
    }

    private void startGame(){
        backgroundPlayer.start();
        nowPlaying = true;
        lives = NUM_OF_LIVES;
        ((TextView)findViewById(R.id.TimerText)).setText("00:00");
        initGrid();
        roadObjects[NUM_OF_ROWS][carPos].setVisibility(View.VISIBLE);
        LinearLayout heartsLayout = findViewById(R.id.HeartsLayout);
        heartsLayout.removeAllViews();
        for(int i = 0 ; i < hearts.length ; i++) {
            hearts[i] = new ImageView(this);
            hearts[i].setImageResource(R.drawable.ic_love_heart_svg);
            hearts[i].setVisibility(View.VISIBLE);
            heartsLayout.addView(hearts[i]);
        }
        backgroundRunnable = () -> {
            if(!nowPlaying)
                return;
            runOnUiThread(()->{
                int newPos = (int)(Math.random()*(NUM_OF_LANES+1));
                for(int i = 0 ; i < NUM_OF_LANES ; i++){
                    Bottle obj = (Bottle)roadObjects[NUM_OF_ROWS-1][i];
                    if(obj.getVisibility() == View.VISIBLE && carPos == i){
                        collidePlayer.start();
                        if(obj.collide() > 0) coins++;
                        else lives--;
                    }
                    obj.setVisibility(View.INVISIBLE);
                }
                for(int i = NUM_OF_ROWS - 1 ; i > 0 ; i--)
                    for(int j = 0 ; j < NUM_OF_LANES ; j++)
                        roadObjects[i][j].setVisibility(roadObjects[i-1][j].getVisibility());
                for(int i = 0 ; i < NUM_OF_LANES ; i++){
                    roadObjects[0][i].setVisibility(newPos == i ? View.VISIBLE : View.INVISIBLE);
                }
                updateLives();
                mHandler.postDelayed(backgroundRunnable,gameSpeed);
            });
        };
        mHandler.postDelayed(backgroundRunnable,gameSpeed);
        secondsPlayed = 0;
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

    private void updateLives(){
        if(lives >= hearts.length || lives < 0)
            return;
        hearts[lives].setVisibility(View.INVISIBLE);
        if(lives == 0) {
            stopGame();
        }
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
            img.setVisibility(View.INVISIBLE);
            gridLayout.addView(img,layoutParams);
        }
    }

    private void moveCar(Direction dir){
        if(carPos <= 0 && (dir == Direction.LEFT))
            carPos = 0;
        else if(carPos >= (NUM_OF_LANES-1) && (dir == Direction.RIGHT))
            carPos = NUM_OF_LANES-1;
        else{
            roadObjects[NUM_OF_ROWS][(dir == Direction.LEFT) ? carPos--:carPos++].setVisibility(View.INVISIBLE);
            roadObjects[NUM_OF_ROWS][carPos].setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
        int side = (int) event.values[0];
        int speed = (int) event.values[1];
        if(side >= 3)
            moveCar(Direction.LEFT);
        else if(side <= -3)
            moveCar(Direction.RIGHT);
        if(speed > 10)
            gameSpeed = GAME_SPEED_FAST;
        else if(speed < -10)
            gameSpeed = GAME_SPEED_SLOW;
        else
            gameSpeed = GAME_SPEED_NORMAL;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume()
    {
        super.onResume();
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
