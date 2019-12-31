package com.zh.cavas.sample;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import androidx.appcompat.app.AppCompatActivity;

/**
 * <b>Package:</b> com.zh.cavas.sample <br>
 * <b>Create Date:</b> 2019-12-29  10:49 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public abstract class BaseActivity extends AppCompatActivity {
    private MotionEvent mDownEvent;
    private VelocityTracker mVelocityTracker;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (mDownEvent != null) {
                mDownEvent.recycle();
            }
            //记录按下时的事件
            mDownEvent = MotionEvent.obtain(ev);
        } else if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
            float downX = mDownEvent.getX();
            float downY = mDownEvent.getY();
            float upX = ev.getX();
            float upY = ev.getY();
            float distanceX = Math.abs(upX - downX);
            float distanceY = Math.abs(upY - downY);
            //上下滑和左滑，不算
            if (distanceY > distanceX || downX > upX) {
                return super.dispatchTouchEvent(ev);
            }
            //右滑返回手势检测
            int pointerId = ev.getPointerId(0);
            int maximumFlingVelocity = ViewConfiguration.get(this).getScaledMaximumFlingVelocity();
            //最小Fling的速度
            int minimumFlingVelocity = ViewConfiguration.get(this).getScaledMinimumFlingVelocity();
            mVelocityTracker.computeCurrentVelocity(1000, maximumFlingVelocity);
            final float velocityX = mVelocityTracker.getXVelocity(pointerId);
            //左边缘检测，可根据需要调整，单位像素
            boolean isEdgeDrag = mDownEvent.getX() <= dip2px(this, 50f);
            //有效触发距离，可根据需要调整，单位像素
            boolean isEffectiveRange = ev.getX() - mDownEvent.getX() <= getScreenWidth(this) / 2f;
            //是Fling操作
            boolean isFling = Math.abs(velocityX) >= minimumFlingVelocity;
            if (
//                    isEdgeDrag &&
                    isEffectiveRange && isFling) {
                onBackPressed();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 返回屏幕的宽度
     */
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}