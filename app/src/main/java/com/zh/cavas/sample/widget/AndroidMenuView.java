package com.zh.cavas.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zh.cavas.sample.R;

/**
 * <b>Package:</b> com.zh.cavas.sample.widget <br>
 * <b>Create Date:</b> 2019-12-28  10:12 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 三条杠的菜单View <br>
 */
public class AndroidMenuView extends View {
    /**
     * View默认最小宽度
     */
    private static final int DEFAULT_MIN_WIDTH = 100;

    /**
     * 控件宽
     */
    private int mViewWidth;
    /**
     * 控件高
     */
    private int mViewHeight;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 每条线之间的距离
     */
    private float mLineDistance;
    /**
     * 每条线的长度
     */
    private float mLineLength;
    /**
     * 一半的线长度
     */
    private float mHalfLineLength;
    /**
     * 线的颜色
     */
    private int mColor;
    /**
     * 线高
     */
    private float mLineHeight;

    public AndroidMenuView(Context context) {
        this(context, null);
    }

    public AndroidMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AndroidMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mLineHeight);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int defaultColor = Color.argb(255, 0, 0, 0);
        int defaultLineHeight = dip2px(context, 2f);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AndroidMenuView, defStyleAttr, 0);
            mColor = array.getColor(R.styleable.AndroidMenuView_amv_color, defaultColor);
            mLineHeight = array.getDimension(R.styleable.AndroidMenuView_amv_line_height, defaultLineHeight);
            array.recycle();
        } else {
            mColor = defaultColor;
            mLineHeight = defaultLineHeight;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算半径
        float radius = Math.min(mViewWidth, mViewHeight) / 2f;
        //计算每条线之间的距离
        mLineDistance = (radius / 3f);
        //每条线的长度
        mLineLength = radius;
        //计算一半的线的长度
        mHalfLineLength = mLineLength / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = mViewWidth / 2;
        int centerY = mViewHeight / 2;
        canvas.translate(centerX, centerY);
        //画中间的线
        canvas.drawLine(-mHalfLineLength, 0, mHalfLineLength, 0, mPaint);
        //画顶部的线
        canvas.drawLine(-mHalfLineLength, -mLineDistance, mHalfLineLength, -mLineDistance, mPaint);
        //画底部的线
        canvas.drawLine(-mHalfLineLength, mLineDistance, mHalfLineLength, mLineDistance, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(handleMeasure(widthMeasureSpec), handleMeasure(heightMeasureSpec));
    }

    /**
     * 处理MeasureSpec
     */
    private int handleMeasure(int measureSpec) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            //处理wrap_content的情况
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}