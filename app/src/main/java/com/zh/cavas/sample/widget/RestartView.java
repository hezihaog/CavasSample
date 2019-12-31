package com.zh.cavas.sample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * <b>Package:</b> com.zh.cavas.sample.widget <br>
 * <b>Create Date:</b> 2019-12-31  15:21 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class RestartView extends View {
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
     * 圆的半径
     */
    private float mCircleRadius;
    private int mLineWidth;

    public RestartView(Context context) {
        this(context, null);
    }

    public RestartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RestartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mPaint = new Paint();
        mPaint.setColor(Color.argb(255, 0, 0, 0));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mLineWidth = dip2px(context, 1.5f);
        mPaint.setStrokeWidth(mLineWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算半径
        mCircleRadius = (Math.min(mViewWidth, mViewHeight) * 0.5f) / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //移动画布中心在控件中心
        canvas.translate(mViewWidth / 2f, mViewHeight / 2f);
        //画圆弧
        drawArc(canvas);
        //画三角形
        drawArrow(canvas);
    }

    /**
     * 画圆弧
     */
    private void drawArc(Canvas canvas) {
        float left = -mCircleRadius;
        float top = -mCircleRadius;
        float right = mCircleRadius;
        float bottom = mCircleRadius;
        Path path = new Path();
        path.addArc(left, top, right, bottom, 40, 320);
        canvas.drawPath(path, mPaint);
    }

    private void drawArrow(Canvas canvas) {
        //保存画布
        canvas.save();
        int arrowLength = dip2px(getContext(), 5f);
        canvas.translate(mCircleRadius + mLineWidth, dip2px(getContext(), -3f));
        canvas.rotate(100f);
        Path arrowPath = new Path();
        arrowPath.moveTo(0, 0);
        arrowPath.lineTo(arrowLength, 0);
        arrowPath.lineTo(0, arrowLength);
        arrowPath.lineTo(arrowLength, 0);
        canvas.drawPath(arrowPath, mPaint);
        //恢复画布
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