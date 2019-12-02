package com.aei.androidhw1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class Bottle extends GameObject {

    private int collidePower;

    public Bottle(Context context) {
        super(context);
        init();
    }

    public Bottle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Bottle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setAsArak(){
        this.setImageResource(R.drawable.ic_vodka);
        this.collidePower = 1;
    }

    public void setAsVodka(){
        this.setImageResource(R.drawable.ic_vodka);
        this.collidePower = -1;
    }

    public boolean isVodka(){
        return collidePower == -1;
    }

    private void init(){
        this.setForegroundGravity(Gravity.CENTER);
        this.setScaleType(ImageView.ScaleType.CENTER);
        this.setRotation((float)(Math.random()*20 - 10));
    }
}

