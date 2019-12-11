package com.aei.androidhw1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class MainGameActivity extends AppCompatActivity implements SensorEventListener {

    public static final int NUM_OF_LIVES = 3;
    public static final long GAME_SPEED_FAST = 750, GAME_SPEED_SLOW = 1500, GAME_SPEED_NORMAL = 1000;
    public static final int NUM_OF_LANES = 3;
    public static final int NUM_OF_ROWS = 4;
    private GameObject[][] roadObjects = new GameObject[NUM_OF_ROWS + 1][NUM_OF_LANES];
    private GridLayout gridLayout;
    private Button restartButton;
    private Button exitButton;
    private Runnable backgroundRunnable, gameTimerRunnable;
    private long gameSpeed;
    private Heart[] hearts = new Heart[NUM_OF_LIVES];
    private TextView gameOverText;
    private SensorManager sensorManager;
    private final Handler mHandler = new Handler();
    private boolean nowPlaying = true, paused = false, ended = false;
    private MediaPlayer backgroundPlayer, collidePlayer, gameOverPlayer;
    private long secondsPlayed = 0;

    private CarController carController;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        backgroundPlayer = MediaPlayer.create(this, R.raw.the_entertainer_8_bit);
        backgroundPlayer.setLooping(true);
        collidePlayer = MediaPlayer.create(this, R.raw.vodka_colide_sound);
        gameOverPlayer = MediaPlayer.create(this, R.raw.game_over);
        ImageView leftBtn = findViewById(R.id.turn_left_btn);
        ImageView rightBtn = findViewById(R.id.turn_right_btn);
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
            carController.reset();
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
            leftBtn.setOnTouchListener(arrowOnTouchListener(CarController.Direction.LEFT));
            rightBtn.setOnTouchListener(arrowOnTouchListener(CarController.Direction.RIGHT));
        }
        boolean sound = getIntent().getBooleanExtra(getString(R.string.sound_mode_prefs),true);
        if(!sound){
            backgroundPlayer.setVolume(0,0);
            collidePlayer.setVolume(0,0);
            gameOverPlayer.setVolume(0,0);
        }
        resetGame();
        carController = new CarController(roadObjects[NUM_OF_ROWS],hearts,collidePlayer);
    }

    /**
     * Init the Hearts Array
     */
    private void initLives(){
        LinearLayout heartsLayout = findViewById(R.id.hearts_layout);
        heartsLayout.removeAllViews();
        for(int i = 0 ; i < hearts.length ; i++) {
            hearts[i] = new Heart(this);
            heartsLayout.addView(hearts[i]);
            hearts[i].setVisible(true);
        }
    }

    /**
     * Show End Game Window and get player name if he made it to top 10 in scoreboard
     * @param visible show/hide
     */
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

    /**
     * Open ScoreBoard Activity
     */
    private void openScoreBoard(){
        Intent intent = new Intent(this, ScoreBoard.class);
        startActivity(intent);
    }

    /**
     * End Game, stop music and show End Game Window
     */
    private void endGame(){
        stopGame();
        backgroundPlayer.seekTo(0);
        gameOverPlayer.start();
        showEndGameWindow(true);
        ended = true;
    }

    /**
     * Stop the game and pause music
     */
    private void stopGame(){
        paused = true;
        nowPlaying = false;
        backgroundPlayer.pause();
    }

    /**
     * Reset the game, time played, initialize game grid, reset lives and start music from beginning
     */
    private void resetGame(){
        gameOverPlayer.seekTo(0);
        secondsPlayed = 0;
        ended = false;
        ((TextView)findViewById(R.id.TimerText)).setText(MyApp.getDateFormatter().format(new Date(0)));
        initGrid();
        initLives();
    }

    /**
     * Starts the game, start background timed thread and timer thread
     */
    private void startGame(){
        backgroundPlayer.start();
        nowPlaying = true;
        backgroundRunnable = () -> {
            if(!nowPlaying)
                return;
            runOnUiThread(()->{
                for(int i = 0 ; i < NUM_OF_LANES ; i++){
                    Bottle obj = (Bottle)roadObjects[NUM_OF_ROWS-1][i];
                    if(carController.getPos() == i)
                        carController.collide(obj);
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
                if(carController.getLives() <= 0)
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
                ((TextView)findViewById(R.id.TimerText)).setText(MyApp.getDateFormatter().format(d));
            });
            mHandler.postDelayed(gameTimerRunnable,1000);
        };
        mHandler.postDelayed(gameTimerRunnable,1000);
    }

    /**
     * Initialize game grid, sets the Bottle objects and Car objects in their place
     */
    private void initGrid(){
        gridLayout.removeAllViews();
        int newPos = (int)(Math.random()*(NUM_OF_LANES+1));
        for(int i = 0 ; i < NUM_OF_ROWS ; i++){
            for(int j = 0 ; j < NUM_OF_LANES ; j++) {
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.rowSpec = GridLayout.spec(i,1,GridLayout.CENTER,1);
                layoutParams.columnSpec = GridLayout.spec(j,1,GridLayout.CENTER,1);
                layoutParams.height = TableLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.width = TableLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.rightMargin = 5;
                layoutParams.topMargin = 5;
                Bottle bottle = new Bottle(this);
                bottle.setAsVodka();
                roadObjects[i][j] = bottle;
                bottle.setVisibility(j == newPos && i == 0 ? View.VISIBLE : View.INVISIBLE);
                gridLayout.addView(bottle,layoutParams);
            }
        }
        for(int i = 0 ; i < NUM_OF_LANES ; i++) {
            Car car = new Car(this);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.rowSpec = GridLayout.spec(NUM_OF_ROWS);
            layoutParams.columnSpec = GridLayout.spec(i,1,GridLayout.CENTER,1);
            roadObjects[NUM_OF_ROWS][i] = car;
            car.setVisibility(i == NUM_OF_LANES/2 ? View.VISIBLE : View.INVISIBLE);
            gridLayout.addView(car,layoutParams);
        }
    }

    /**
     * Used for Tilt mode, callback for tilt sensor change and use the value to control game speed
     * and car position
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
        int side = (int) event.values[0];
        int speed = (int) event.values[1];
        if(side >= 3)
            carController.move(CarController.Direction.LEFT);
        else if(side <= -3)
            carController.move(CarController.Direction.RIGHT);
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
    protected void onPause(){
        super.onPause();
        if(sensorManager != null)
            sensorManager.unregisterListener(this);
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
        if(sensorManager != null)
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
        gameOverPlayer.stop();
        gameOverPlayer.reset();
    }

    /**
     * Listener for direction buttons
     * @param dir LEFT/RIGHT
     * @return return the listener
     */
    private View.OnTouchListener arrowOnTouchListener(CarController.Direction dir){
        return (v,e)->{
            v.performClick();
            switch (e.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    carController.move(dir);
                    v.setScaleX(dir == CarController.Direction.RIGHT ? -1 : 1);v.setScaleY(1);
                    v.animate().setDuration(100).scaleY(0.5f).scaleX(dir == CarController.Direction.RIGHT ? -0.5f : 0.5f).start();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    v.animate().scaleX(dir == CarController.Direction.RIGHT ? -1 : 1).scaleY(1).setDuration(100).start();
                    return true;
            }
            return false;
        };
    }
}
