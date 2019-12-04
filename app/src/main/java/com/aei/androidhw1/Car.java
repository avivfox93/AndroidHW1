package com.aei.androidhw1;

import android.animation.Animator;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

public class Car extends GameObject{
    public Car(Context context) {
        super(context);
        init();
    }

    public Car(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Car(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Init the Car object
     */
    private void init(){
        this.setImageResource(R.drawable.pickup_truck);
        this.setScaleType(ImageView.ScaleType.CENTER);
        this.setForegroundGravity(Gravity.CENTER);
    }
}

/**
 * Manage the car movement and lives
 */
class CarController {
    private GameObject[] lanes;
    private Heart[] hearts;
    private int pos,lives,coins;
    private MediaPlayer collideSound;

    public enum Direction{
        RIGHT,LEFT
    }

    /**
     *
     * @param lanes GameObject Array that represents the car lanes
     * @param hearts Heart Array that contains the lives of the car
     * @param collideSound MediaPlayer that initialized with collide sound
     */
    public CarController(GameObject[] lanes, Heart[] hearts, MediaPlayer collideSound){
        this.lanes = lanes;
        this.hearts = hearts;
        this.collideSound = collideSound;
        reset();
    }

    public int getPos(){
        return pos;
    }

    public int getLives(){
        return lives;
    }

    /**
     * Move the Car one step in dir direction
     * @param dir can be LEFT or RIGHT
     */
    public void move(Direction dir){
        if(dir == Direction.RIGHT && pos >= lanes.length-1)
            return;
        if(dir == Direction.LEFT && pos <= 0)
            return;
        if(dir == Direction.RIGHT)
            pos++;
        else
            pos--;
        for(int i = 0 ; i < lanes.length ; i++){
            if(i == pos)
                lanes[i].setVisibility(View.VISIBLE);
            else
                lanes[i].setVisibility(View.INVISIBLE);
        }
        MyApp.getVibrator().vibrate(50);
    }

    /**
     * Collide the Car with a Bottle
     * @param bottle
     */
    public void collide(Bottle bottle){
        if(bottle.getVisibility() == View.INVISIBLE)
            return;
        MyApp.getVibrator().vibrate(500);
        collideSound.start();
        if(bottle.isVodka()) {
            int temp = --lives;
            if(temp < 0)
                return;
            hearts[hearts.length - lives - 1].setVisible(false);
        } else {
            coins++;
        }
        for (ImageView lane : lanes) {
            lane.setScaleX(0.2f);lane.setScaleY(0.2f);
            lane.animate().scaleY(1f).scaleX(1f).setInterpolator(new BounceInterpolator()).start();
        }
    }

    /**
     * Resets the car lives and position the car in the middle lane
     */
    public void reset(){
        for(Heart heart : hearts)
            heart.setVisible(true);
        lives = hearts.length;
        pos = lanes.length/2;
        lanes[pos].setVisibility(View.VISIBLE);
    }
}
