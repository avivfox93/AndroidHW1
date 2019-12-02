package com.aei.androidhw1;

import android.content.Context;
import android.util.AttributeSet;

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

    private void init(){
        this.setImageResource(R.drawable.ic_love_heart_svg);
    }
}
