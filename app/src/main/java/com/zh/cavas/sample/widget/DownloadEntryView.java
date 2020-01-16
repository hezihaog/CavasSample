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
 * <b>Create Date:</b> 2020-01-16  15:05 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 下载入口 <br>
 */
public class DownloadEntryView extends View {
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
     * 一半箭头的长度
     */
    private float mHalfArrowLength;
    /**
     * 计算箭头边的长度
     */
    private float mArrowEdgeLength;
    /**
     * 颜色
     */
    private int mColor;
    /**
     * 线宽
     */
    private float mLineWidth;

    public DownloadEntryView(Context context) {
        this(context, null);
    }

    public DownloadEntryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadEntryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int defaultColor = Color.argb(255, 0, 0, 0);
        int defaultLineWidth = dip2px(context, 1.5f);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DownloadEntryView, defStyleAttr, 0);
            mColor = array.getColor(R.styleable.DownloadEntryView_dev_color, defaultColor);
            mLineWidth = array.getDimension(R.styleable.DownloadEntryView_dev_line_width, defaultLineWidth);
            array.recycle();
        } else {
            mColor = defaultColor;
            mLineWidth = defaultLineWidth;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算一般箭头长度
        mHalfArrowLength = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.4f;
        //计算箭头边的长度
        mArrowEdgeLength = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布中心移动到中心点
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        //画箭头
        drawArrow(canvas);
        //画底部的线
        drawBottomLine(canvas);
    }

    /**
     * 画箭头
     */
    private void drawArrow(Canvas canvas) {
        //先从中点向下偏移一半长度
        canvas.translate(0, mHalfArrowLength);
        //从中点向上画一条2倍长度的线
        canvas.drawLine(0, 0, 0, -(mHalfArrowLength * 2), mPaint);
        //将画布向左旋转45度
        canvas.save();
        canvas.rotate(-45);
        //画直角的第一条边线
        canvas.drawLine(0, 0, 0, -mArrowEdgeLength, mPaint);
        //画直角的第二条边线
        canvas.drawLine(0, 0, mArrowEdgeLength, 0, mPaint);
        canvas.restore();
    }

    /**
     * 画底部的线
     */
    private void drawBottomLine(Canvas canvas) {
        //计算箭头和线之间的距离，定义为箭头长度的一半
        float distance = mHalfArrowLength / 2f;
        canvas.save();
        canvas.translate(0, distance);
        //向左画一条线
        canvas.drawLine(0, 0, -mArrowEdgeLength, 0, mPaint);
        //向右画一条线
        canvas.drawLine(0, 0, mArrowEdgeLength, 0, mPaint);
        canvas.restore();
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