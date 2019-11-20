package com.aei.androidhw1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.TimerTask;


public class MainGame extends AppCompatActivity implements SensorEventListener {

    public static final int NUM_OF_LIVES = 3;
    public static final long GAME_SPEED_FAST = 750, GAME_SPEED_SLOW = 1500, GAME_SPEED_NORMAL = 1000;
    public static final int NUM_OF_LANES = 3;
    public static final int NUM_OF_ROWS = 4;
    private ImageView[][] roadObjects = new ImageView[NUM_OF_ROWS + 1][NUM_OF_LANES];
    private GridLayout gridLayout;
    private Button leftBtn,rightBtn,restartButton,exitButton;
    private int carPos = NUM_OF_LANES/2;
    private Runnable backgroundRunnable;
    private long gameSpeed ;
    private int lives = NUM_OF_LIVES, coins = 0;
    private LinearLayout heartsLayout;
    private ImageView[] hearts = new ImageView[3];
    private TextView gameOverText;
    private SensorManager sensorManager;
    private final Handler mHandler = new Handler();
    private boolean nowPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        leftBtn = findViewById(R.id.TurnLeftBtn);
        rightBtn = findViewById(R.id.TurnRightBtn);
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
            leftBtn.setOnClickListener(e->moveCar(true));
            rightBtn.setOnClickListener(e->moveCar(false));
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
        showEndGameWindow(true);
    }

    private void startGame(){
        nowPlaying = true;
        lives = NUM_OF_LIVES;
        initGrid();
        roadObjects[NUM_OF_ROWS][carPos].setVisibility(View.VISIBLE);
        heartsLayout = findViewById(R.id.HeartsLayout);
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

    private void moveCar(boolean left){
        if(carPos <= 0 && left)
            carPos = 0;
        else if(carPos >= (NUM_OF_LANES-1) && !left)
            carPos = NUM_OF_LANES-1;
        else{
            roadObjects[NUM_OF_ROWS][left ? carPos--:carPos++].setVisibility(View.INVISIBLE);
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
            moveCar(true);
        else if(side <= -3)
            moveCar(false);
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
}
