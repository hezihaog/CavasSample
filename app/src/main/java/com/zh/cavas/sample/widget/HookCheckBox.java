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
 * <b>Create Date:</b> 2020-01-17  14:48 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 钩子CheckBox <br>
 */
public class HookCheckBox extends View {
    /**
     * View默认最小宽度
     */
    private static final int DEFAULT_MIN_WIDTH = 80;

    /**
     * 控件宽
     */
    private int mViewWidth;
    /**
     * 控件高
     */
    private int mViewHeight;
    /**
     * 原型半径
     */
    private float mRadius;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 钩子的线长度
     */
    private float mHookLineLength;
    /**
     * 是否选中
     */
    private boolean isCheck;
    /**
     * 选中时，圆的颜色
     */
    private int mCheckCircleColor;
    /**
     * 未选中时，圆的颜色
     */
    private int mUncheckCircleColor;
    /**
     * 钩子的颜色
     */
    private int mHookColor;

    public HookCheckBox(Context context) {
        this(context, null);
    }

    public HookCheckBox(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HookCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        //画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mUncheckCircleColor);
        mPaint.setStrokeWidth(dip2px(context, 1.5f));
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheck = !isCheck;
                invalidate();
            }
        });
        //View禁用掉GPU硬件加速，切换到软件渲染模式
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        //默认的选中颜色
        int defaultCheckCircleColor = Color.argb(255, 101, 118, 213);
        //默认的未选中颜色
        int defaultUncheckCircleColor = Color.argb(255, 126, 127, 126);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HookCheckBox, defStyleAttr, 0);
            mCheckCircleColor = array.getColor(R.styleable.HookCheckBox_hcb_check_circle_color, defaultCheckCircleColor);
            mUncheckCircleColor = array.getColor(R.styleable.HookCheckBox_hcb_uncheck_circle_color, defaultUncheckCircleColor);
            mHookColor = array.getColor(R.styleable.HookCheckBox_hcb_hook_color, mCheckCircleColor);
            isCheck = array.getBoolean(R.styleable.HookCheckBox_hcb_is_check, false);
            array.recycle();
        } else {
            mCheckCircleColor = defaultCheckCircleColor;
            mUncheckCircleColor = defaultUncheckCircleColor;
            mHookColor = defaultCheckCircleColor;
            isCheck = false;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算圆的半径
        mRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.90f;
        //计算对勾的长度
        mHookLineLength = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.8f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left = -mViewWidth / 2f;
        float top = -mViewHeight / 2f;
        //保存图层
        int layerId = canvas.saveLayer(left, top, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        //将画布中心移动到中心点
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        //画圆形背景
        drawCircleBg(canvas);
        //勾选状态时，才画钩子
        if (isCheck) {
            //画钩子
            drawHook(canvas);
        }
        //恢复图层
        canvas.restoreToCount(layerId);
    }

    /**
     * 画圆形背景
     */
    private void drawCircleBg(Canvas canvas) {
        //设置背景圆的颜色
        if (isCheck) {
            mPaint.setColor(mCheckCircleColor);
        } else {
            mPaint.setColor(mUncheckCircleColor);
        }
        canvas.drawCircle(0, 0, mRadius, mPaint);
    }

    /**
     * 画钩子
     */
    private void drawHook(Canvas canvas) {
        canvas.save();
        //设置钩子的颜色
        mPaint.setColor(mHookColor);
        //画布向下平移一半的半径长度
        canvas.translate(-(mRadius / 8f), mRadius / 3f);
        //旋转画布45度
        canvas.rotate(-45);
        Path path = new Path();
        path.reset();
        path.moveTo(0, 0);
        //向右画一条线
        path.lineTo(mHookLineLength, 0);
        //回到中心点
        path.moveTo(0, 0);
        //向上画一条线
        path.lineTo(0, -mHookLineLength / 2f);
        //画路径
        canvas.drawPath(path, mPaint);
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

    public boolean isCheck() {
        return isCheck;
    }

    public HookCheckBox setCheck(boolean check) {
        isCheck = check;
        return this;
    }
}