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

    private void init(){
        this.setImageResource(R.drawable.pickup_truck);
        this.setScaleType(ImageView.ScaleType.CENTER);
        this.setForegroundGravity(Gravity.CENTER);
    }
}

class CarController {
    private GameObject[] lanes;
    private Heart[] hearts;
    private int pos,lives,coins;
    private MediaPlayer collideSound;

    public enum Direction{
        RIGHT,LEFT
    }

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

    public void reset(){
        for(int i = 0 ; i < hearts.length ; i++) {
            hearts[i].setVisibility(View.VISIBLE);hearts[i].setRotation(-360);hearts[i].setScaleX(0);hearts[i].setScaleY(0);
            hearts[i].animate().setDuration(700).scaleX(1).scaleY(1).rotation(0).setInterpolator(new BounceInterpolator()).setStartDelay(i*500).start();
        }
        lives = hearts.length;
        pos = lanes.length/2;
        lanes[pos].setVisibility(View.VISIBLE);
    }
}
