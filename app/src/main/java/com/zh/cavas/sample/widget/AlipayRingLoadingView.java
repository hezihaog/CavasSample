package com.zh.cavas.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zh.cavas.sample.R;

/**
 * <b>Package:</b> com.zh.cavas.sample.widget <br>
 * <b>Create Date:</b> 2020-01-15  14:13 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class AlipayRingLoadingView extends View implements Runnable {
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
     * 间隔时间
     */
    private static final int INTERVAL_TIME = 30;
    /**
     * 总旋转角度
     */
    private static final int TOTAL_ROTATION_ANGLE = 360;
    /**
     * 圆的半径
     */
    private float mRadius;
    /**
     * 当前旋转到的角度
     */
    private int mCurrentAngle = 0;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 圆的背景颜色
     */
    private int mBgCircleColor;
    /**
     * 圆弧颜色
     */
    private int mArcColor;

    public AlipayRingLoadingView(Context context) {
        this(context, null);
    }

    public AlipayRingLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlipayRingLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        //画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mBgCircleColor);
        mPaint.setStrokeWidth(dip2px(context, 1.5f));
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int defaultArcColor = Color.argb(255, 25, 108, 199);
        int defaultBgCircleColor = Color.argb(50, 0, 0, 0);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AlipayRingLoadingView, defStyleAttr, 0);
            mArcColor = array.getColor(R.styleable.AlipayRingLoadingView_arlv_arc_color, defaultArcColor);
            mBgCircleColor = array.getColor(R.styleable.AlipayRingLoadingView_arlv_bg_circle_color, defaultBgCircleColor);
            array.recycle();
        } else {
            mArcColor = defaultArcColor;
            mBgCircleColor = defaultBgCircleColor;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算半径
        mRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.8f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布中心移动到中心点
        canvas.translate(mViewWidth / 2f, mViewHeight / 2f);
        //旋转画布，让圆环旋转起来
        canvas.rotate(mCurrentAngle, 0, 0);
        //画圆
        drawCircle(canvas);
        //画圆弧
        drawArc(canvas);
    }

    /**
     * 画圆
     */
    private void drawCircle(Canvas canvas) {
        mPaint.setColor(mBgCircleColor);
        canvas.drawCircle(0, 0, mRadius, mPaint);
    }

    private void drawArc(Canvas canvas) {
        mPaint.setColor(mArcColor);
        canvas.drawArc(new RectF(-mRadius, -mRadius, mRadius, mRadius), 0, 45, false, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(handleMeasure(widthMeasureSpec), handleMeasure(heightMeasureSpec));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    public void run() {
        if (mCurrentAngle >= TOTAL_ROTATION_ANGLE) {
            mCurrentAngle = mCurrentAngle - TOTAL_ROTATION_ANGLE;
        } else {
            //每次叠加10步长
            mCurrentAngle += 10;
        }
        //通知重绘
        invalidate();
        postDelayed(this, INTERVAL_TIME);
    }

    private void start() {
        postDelayed(this, INTERVAL_TIME);
    }

    private void stop() {
        removeCallbacks(this);
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