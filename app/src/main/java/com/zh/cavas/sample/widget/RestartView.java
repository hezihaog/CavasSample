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
 * <b>Create Date:</b> 2019-12-31  15:21 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 重启View <br>
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
     * 线的数量
     */
    private int mLineCount;

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
        initAttr(context, attrs, defStyleAttr);
        //圆的画笔
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mBgCircleColor);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setStrokeWidth(dip2px(context, 1f));
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
        int defaultLineCount = 8;
        int defaultBgCircleColor = Color.argb(255, 40, 110, 145);
        int defaultLineColor = Color.argb(255, 255, 255, 255);
        int defaultLineWidth = dip2px(context, 1f);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RestartView, defStyleAttr, 0);
            mLineCount = array.getInt(R.styleable.RestartView_rtv_line_count, defaultLineCount);
            mBgCircleColor = array.getColor(R.styleable.RestartView_rtv_bg_circle_color, defaultBgCircleColor);
            mLineColor = array.getColor(R.styleable.RestartView_rtv_line_color, defaultLineColor);
            mLineWidth = array.getDimension(R.styleable.RestartView_rtv_line_width, defaultLineWidth);
            array.recycle();
        } else {
            mLineCount = defaultLineCount;
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
        mCircleRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.8f;
        //计算内圆半径
        mInnerCircleRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //移动画布中心在控件中心
        canvas.translate(mViewWidth / 2f, mViewHeight / 2f);
        //画圆形背景
        drawCircleBg(canvas);
        //画线
        drawLine(canvas);
    }

    /**
     * 画圆形背景
     */
    private void drawCircleBg(Canvas canvas) {
        canvas.drawCircle(0, 0, mCircleRadius, mCirclePaint);
    }

    private void drawLine(Canvas canvas) {
        //计算平均角度
        float angle = 360f / mLineCount;
        float lineLength = mCircleRadius * 0.2f;
        canvas.save();
        for (int i = 1; i <= mLineCount; i++) {
            canvas.rotate(angle);
            canvas.drawLine(mInnerCircleRadius, 0, mInnerCircleRadius + lineLength, 0, mLinePaint);
        }
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