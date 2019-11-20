package com.aei.androidhw1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class Bottle extends ImageView {

    private int collidePower;

    public Bottle(Context context) {
        super(context);
    }

    public Bottle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Bottle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Bottle(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int collide(){
        return this.collidePower;
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
}

