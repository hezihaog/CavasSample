package com.zh.cavas.sample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * <b>Package:</b> com.zh.cavas.sample.widget <br>
 * <b>Create Date:</b> 2020/2/26  1:56 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 4个点旋转自定义View <br>
 */
public class FourRotateDotView extends View implements Runnable {
    /**
     * View默认最小宽度
     */
    private static final int DEFAULT_MIN_WIDTH = 80;
    /**
     * 间隔时间
     */
    private static final int INTERVAL_TIME = 100;

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
     * 每个点的颜色值
     */
    private int[] mColors = {
            Color.argb(255, 254, 159, 155),
            Color.argb(255, 254, 119, 114),
            Color.argb(255, 253, 71, 64),
            Color.argb(255, 253, 107, 101)
    };
    /**
     * 计算View的半径
     */
    private float mRadius;
    /**
     * 点的半径
     */
    private float mDotRadius;
    /**
     * 旋转角度
     */
    private int mRotateAngle;

    public FourRotateDotView(Context context) {
        this(context, null);
    }

    public FourRotateDotView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FourRotateDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        //取消硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.argb(255, 253, 71, 64));
        mPaint.setStrokeWidth(dip2px(context, 2f));
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算View的半径
        mRadius = (Math.max(mViewWidth, mViewHeight) / 2f) * 0.95f;
        //计算点的半径
        mDotRadius = (Math.max(mViewWidth, mViewHeight) / 2f) * 0.4f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布中心移动到中心点
        canvas.translate(mViewWidth / 2f, mViewHeight / 2f);
        //保存画布
        canvas.save();
        //旋转画布，造成旋转效果
        canvas.rotate(mRotateAngle, 0, 0);
        //画4个点
        drawFourDot(canvas);
        //恢复画布
        canvas.restore();
    }

    /**
     * 画4个点
     */
    private void drawFourDot(Canvas canvas) {
        //缩放画布，由于下面我们画的4个点都比较靠边，所以通过缩放，让他们距离圆心更加紧凑一点
        canvas.scale(0.8f, 0.8f);
        //依次画4个点
        for (int i = 0; i < mColors.length; i++) {
            canvas.save();
            canvas.rotate(90f * i, 0, 0);
            //设置颜色值
            mPaint.setColor(mColors[i]);
            //画圆
            canvas.drawCircle(-mRadius + mDotRadius, -mRadius + mDotRadius, mDotRadius, mPaint);
            canvas.restore();
        }
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
        int result;
        result = DEFAULT_MIN_WIDTH;
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
        //每次叠加步长
        mRotateAngle += 30;
        invalidate();
        postDelayed(this, INTERVAL_TIME);
    }

    public void start() {
        postDelayed(this, INTERVAL_TIME);
    }

    public void stop() {
        removeCallbacks(this);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}