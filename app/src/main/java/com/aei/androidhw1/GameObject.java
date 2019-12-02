package com.aei.androidhw1;


import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public abstract class GameObject extends AppCompatImageView {
    public GameObject(Context context) {
        super(context);
    }

    public GameObject(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameObject(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
