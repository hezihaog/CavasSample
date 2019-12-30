package com.zh.cavas.sample;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * <b>Package:</b> com.zh.cavas.sample <br>
 * <b>Create Date:</b> 2019-12-29  10:49 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public abstract class BaseActivity extends AppCompatActivity {
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float differenceX = e1.getX() - e2.getX();
                float differenceY = e2.getY() - e2.getY();
                //左右滑才处理，上下滑不管
                if (Math.abs(differenceX) > Math.abs(differenceY)) {
                    if (differenceX < 0) {
                        //右滑返回
                        finish();
                        return true;
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}