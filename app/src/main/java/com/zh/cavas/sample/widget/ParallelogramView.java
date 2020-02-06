package com.zh.cavas.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zh.cavas.sample.R;

/**
 * <b>Package:</b> com.zh.cavas.sample.widget <br>
 * <b>Create Date:</b> 2020-02-05  22:25 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 平行四边形View <br>
 */
public class ParallelogramView extends View {
    /**
     * View默认最小宽度
     */
    private static final int DEFAULT_MIN_WIDTH = 80;
    /**
     * View默认最小高度
     */
    private static final int DEFAULT_MIN_HEIGHT = 100;

    /**
     * 风格，填满
     */
    private static final int STYLE_FILL = 1;
    /**
     * 风格，描边
     */
    private static int STYLE_STROKE = 2;

    /**
     * 控件宽
     */
    private int mViewWidth;
    /**
     * 控件高
     */
    private int mViewHeight;
    /**
     * 长的半径
     */
    private float mLongRadius;
    /**
     * 短的半径
     */
    private float mShortRadius;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 菱形Path
     */
    private Path mPath;
    /**
     * 颜色
     */
    private int mColor;
    /**
     * 风格
     */
    private int mStyle;
    /**
     * 线宽
     */
    private float mLineWidth;

    public ParallelogramView(Context context) {
        this(context, null);
    }

    public ParallelogramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallelogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        //取消硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //背景圆的画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        if (mStyle == STYLE_FILL) {
            mPaint.setStyle(Paint.Style.FILL);
        } else if (mStyle == STYLE_STROKE) {
            mPaint.setStyle(Paint.Style.STROKE);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
        }
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mLineWidth);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int defaultColor = Color.argb(255, 0, 0, 0);
        int defaultLineWidth = dip2px(context, 1.5f);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ParallelogramView, defStyleAttr, 0);
            mColor = array.getColor(R.styleable.ParallelogramView_pallv_color, defaultColor);
            mStyle = array.getInt(R.styleable.ParallelogramView_pallv_style, STYLE_STROKE);
            mLineWidth = array.getDimension(R.styleable.ParallelogramView_pallv_line_width, defaultLineWidth);
            array.recycle();
        } else {
            mColor = defaultColor;
            mStyle = STYLE_STROKE;
            mLineWidth = defaultLineWidth;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算半径
        mLongRadius = (Math.max(mViewWidth, mViewHeight) / 2f) * 0.95f;
        mShortRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.6f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布中心移动到中心点
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        //画平行四边形
        drawParallelogram(canvas);
    }

    /**
     * 画平行四边形
     */
    private void drawParallelogram(Canvas canvas) {
        //先将画布旋转一定角度，再画一个菱形，就变成了平行四边形
        canvas.rotate(-30f);
        //用路径，画一个菱形
        if (mPath == null) {
            mPath = new Path();
        }
        //顶部端点
        mPath.moveTo(0, -mShortRadius);
        //右边端点
        mPath.lineTo(mLongRadius, 0);
        //底部端点
        mPath.lineTo(0, mShortRadius);
        //左边端点
        mPath.lineTo(-mLongRadius, 0);
        //闭合路径
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(handleMeasure(widthMeasureSpec, true),
                handleMeasure(heightMeasureSpec, false));
    }

    /**
     * 处理MeasureSpec
     */
    private int handleMeasure(int measureSpec, boolean isWidth) {
        int result;
        if (isWidth) {
            result = DEFAULT_MIN_WIDTH;
        } else {
            result = DEFAULT_MIN_HEIGHT;
        }
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