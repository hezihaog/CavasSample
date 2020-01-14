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
 * <b>Create Date:</b> 2020-01-14  14:06 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class ShutdownView extends View {
    /**
     * View默认最小宽度
     */
    private static final int DEFAULT_MIN_WIDTH = 150;

    /**
     * 控件宽
     */
    private int mViewWidth;
    /**
     * 控件高
     */
    private int mViewHeight;

    /**
     * 圆的画笔
     */
    private Paint mCirclePaint;
    /**
     * 线的画笔
     */
    private Paint mLinePaint;
    /**
     * 外圆半径
     */
    private float mCircleRadius;
    /**
     * 内圆半径
     */
    private float mInnerCircleRadius;
    /**
     * 背景圆的颜色
     */
    private int mBgCircleColor;
    /**
     * 线的颜色
     */
    private int mLineColor;
    /**
     * 线宽
     */
    private float mLineWidth;
    /**
     * 线的长度
     */
    private float mLineLength;

    public ShutdownView(Context context) {
        this(context, null);
    }

    public ShutdownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShutdownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        //圆的画笔
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mBgCircleColor);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setStrokeWidth(mLineWidth);
        //线的画笔
        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mLineWidth);
        //设置圆形笔触
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int defaultBgCircleColor = Color.argb(255, 137, 31, 37);
        int defaultLineColor = Color.argb(255, 255, 255, 255);
        int defaultLineWidth = dip2px(context, 1f);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ShutdownView, defStyleAttr, 0);
            mBgCircleColor = array.getColor(R.styleable.ShutdownView_sdv_bg_circle_color, defaultBgCircleColor);
            mLineColor = array.getColor(R.styleable.ShutdownView_sdv_line_color, defaultLineColor);
            mLineWidth = array.getDimension(R.styleable.ShutdownView_sdv_line_width, defaultLineWidth);
            array.recycle();
        } else {
            mBgCircleColor = defaultBgCircleColor;
            mLineColor = defaultLineColor;
            mLineWidth = defaultLineWidth;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算外圆半径
        mCircleRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.9f;
        //计算内圆半径
        mInnerCircleRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.4f;
        //计算线的长度
        mLineLength = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.45f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //移动画布中心在控件中心
        canvas.translate(mViewWidth / 2f, mViewHeight / 2f);
        //画圆形背景
        drawCircleBg(canvas);
        //画中心到顶部的竖线
        drawVerticalLine(canvas);
        //画圆弧
        drawArc(canvas);
    }

    /**
     * 画圆形背景
     */
    private void drawCircleBg(Canvas canvas) {
        canvas.drawCircle(0, 0, mCircleRadius, mCirclePaint);
    }

    /**
     * 画中心到顶部的竖线
     */
    private void drawVerticalLine(Canvas canvas) {
        canvas.drawLine(0, 0, 0, -mLineLength, mLinePaint);
    }

    /**
     * 画圆弧
     */
    private void drawArc(Canvas canvas) {
        canvas.save();
        //先旋转画布
        canvas.rotate(-90);
        //规定出内圆的区域
        float left = -mInnerCircleRadius;
        float top = -mInnerCircleRadius;
        float right = mInnerCircleRadius;
        float bottom = mInnerCircleRadius;
        //抛弃的角度
        int abandonAngle = 40;
        //开始角度，为抛弃的角度的一半
        int startAngle = abandonAngle / 2;
        //总角度
        int fullAngle = 360;
        //扫过的角度，总角度 - 抛弃的角度
        int sweepAngle = fullAngle - abandonAngle;
        canvas.drawArc(new RectF(left, top, right, bottom), startAngle, sweepAngle, false, mLinePaint);
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