package com.aei.androidhw1;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;

public class Heart extends GameObject {
    public Heart(Context context) {
        super(context);
        init();
    }

    public Heart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Heart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Set the visibility of the Heart, when set to false it also use animation to make the hearth disappear
     * @param visible
     */
    public void setVisible(boolean visible){
        if(visible){
            setScaleX(1);
            setScaleY(1);
            setRotation(0);
            setVisibility(VISIBLE);
        }else {
            clearAnimation();
            setRotation(0);setScaleX(1);setScaleY(1);
            animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(View.INVISIBLE);
                    setScaleX(1);
                    setScaleY(1);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).setDuration(500)
                    .setStartDelay(0).scaleY(0).scaleX(0).rotation(360)
                    .setInterpolator(new BounceInterpolator()).start();
        }
    }

    private void init(){
        this.setImageResource(R.drawable.ic_love_heart_svg);
    }
}