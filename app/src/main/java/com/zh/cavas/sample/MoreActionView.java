package com.zh.cavas.sample;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * <b>Package:</b> com.zh.cavas.sample <br>
 * <b>Create Date:</b> 2019-12-28  09:35 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 更多操作 <br>
 */
public class MoreActionView extends View {
    /**
     * 水平排列
     */
    private static final int ORIENTATION_HORIZONTAL = 1;
    /**
     * 垂直排列
     */
    private static final int ORIENTATION_VERTICAL = 2;

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
     * 点的半径
     */
    private float mDotRadius;
    /**
     * 每个点之间的距离
     */
    private float mDotDistance;
    /**
     * 排列方向
     */
    private int mOrientation;
    /**
     * 点颜色
     */
    private int mColor;

    public MoreActionView(Context context) {
        this(context, null);
    }

    public MoreActionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoreActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mDotRadius);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MoreActionView, defStyleAttr, 0);
        mColor = array.getColor(R.styleable.MoreActionView_mav_color, Color.argb(255, 0, 0, 0));
        mOrientation = array.getInt(R.styleable.MoreActionView_mav_orientation, ORIENTATION_HORIZONTAL);
        mDotRadius = array.getDimension(R.styleable.MoreActionView_mav_dot_radius, dip2px(context, 2f));
        array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算半径
        float radius = Math.min(mViewWidth, mViewHeight) / 2f;
        //计算每个点之间的距离，半径的一般，再减去原点的直径
        mDotDistance = (radius / 2f) - (mDotRadius * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        //垂直排列
        if (mOrientation == ORIENTATION_VERTICAL) {
            canvas.rotate(90);
        } else {
            //水平排列
            canvas.rotate(0);
        }
        //画中心的点
        canvas.drawCircle(0, 0, mDotRadius, mPaint);
        //画左侧的点
        canvas.drawCircle(-mDotDistance, 0, mDotRadius, mPaint);
        //画右侧的点
        canvas.drawCircle(mDotDistance, 0, mDotRadius, mPaint);
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