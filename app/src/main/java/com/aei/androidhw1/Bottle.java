package com.aei.androidhw1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class Bottle extends GameObject {

    boolean isVodka;

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


    /**
     * For next assignment
     */
    public void setAsArak(){
        this.setImageResource(R.drawable.ic_arak);
        this.isVodka = false;
    }

    /**
     * Make this Bottle a vodka bottle
     */
    public void setAsVodka(){
        this.setImageResource(R.drawable.ic_vodka);
        this.isVodka = true;
    }

    public boolean isVodka(){
        return isVodka;
    }

    /**
     * Init the bottle and give him a random rotation
     */
    private void init(){
        this.setForegroundGravity(Gravity.CENTER);
        this.setScaleType(ImageView.ScaleType.CENTER);
        this.setRotation((float)(Math.random()*20 - 10));
        this.isVodka = true;
        this.setAsVodka();
    }
}

