package com.zh.cavas.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zh.cavas.sample.R;

/**
 * <b>Package:</b> com.zh.cavas.sample.widget <br>
 * <b>Create Date:</b> 2020-01-15  09:20 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 多边形Loading效果 <br>
 */
public class PolygonLoadingView extends View implements Runnable {
    /**
     * 默认开始颜色，蓝色
     */
    private final int mDefaultStartColor = Color.argb(255, 67, 97, 239);
    /**
     * 默认结束颜色，白色
     */
    private final int mDefaultEndColor = Color.argb(255, 255, 255, 255);
    /**
     * 圆环的开始、结束颜色
     */
    private int[] mColors;

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
     * 间隔时间
     */
    private static final int INTERVAL_TIME = 80;
    /**
     * 总旋转角度
     */
    private static final int TOTAL_ROTATION_ANGLE = 360;
    /**
     * 当前旋转到的角度
     */
    private int mCurrentAngle = 0;
    /**
     * 多边形的边数
     */
    private int mNum;
    /**
     * 多边形层数
     */
    private int mPolygonLayerCount;
    /**
     * 最小的多边形的半径
     */
    private float mRadius;
    /**
     * 360度对应的弧度（为什么2π就是360度？弧度的定义：弧长 / 半径，一个圆的周长是2πr，如果是一个360度的圆，它的弧长就是2πr，如果这个圆的半径r长度为1，那么它的弧度就是，2πr / r = 2π）
     */
    private final double mPiDouble = 2 * Math.PI;
    /**
     * 多边形中心角的角度（每个多边形的内角和为360度，一个多边形2个相邻角顶点和中心的连线所组成的角为中心角
     * 中心角的角度都是一样的，所以360度除以多边形的边数，就是一个中心角的角度），这里注意，因为后续要用到Math类的三角函数
     * Math类的sin和cos需要传入的角度值是弧度制，所以这里的中心角的角度，也是弧度制的弧度
     */
    private float mCenterAngle;
    /**
     * 平均角度，角度制
     */
    private float mAverageAngle;
    /**
     * 多边形的边的画笔
     */
    private Paint mPaint;

    public PolygonLoadingView(Context context) {
        this(context, null);
    }

    public PolygonLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolygonLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        //取消硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //多边形的边的画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mDefaultStartColor);
        mPaint.setStrokeWidth(dip2px(context, 1f));
        mPaint.setStyle(Paint.Style.STROKE);
        //连接的外边缘以圆弧的方式相交
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        //线条结束处绘制一个半圆
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //设置渐变着色器
        mPaint.setShader(new SweepGradient(0, 0, mColors, null));
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        //默认边数和最小边数
        int defaultNum = 6;
        int minNum = 3;
        //默认层数和最小层数
        int defaultLayerCount = 3;
        int minLayerCount = 1;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PolygonLoadingView, defStyleAttr, 0);
            //处理颜色
            int startColor = array.getColor(R.styleable.PolygonLoadingView_plv_start_color, mDefaultStartColor);
            int endColor = array.getColor(R.styleable.PolygonLoadingView_plv_end_color, mDefaultEndColor);
            mColors = new int[]{startColor, endColor};
            //处理边数
            int num = array.getInt(R.styleable.PolygonLoadingView_plv_num, defaultNum);
            mNum = num <= minNum ? minNum : num;
            //处理层数
            int layerCount = array.getInt(R.styleable.PolygonLoadingView_plv_layer_count, defaultLayerCount);
            mPolygonLayerCount = layerCount <= minLayerCount ? minLayerCount : layerCount;
            array.recycle();
        } else {
            mColors = new int[]{mDefaultStartColor, mDefaultEndColor};
            mNum = defaultNum;
            mPolygonLayerCount = defaultLayerCount;
        }
        //计算中心角弧度
        mCenterAngle = (float) (mPiDouble / mNum);
        //计算平均角度
        mAverageAngle = TOTAL_ROTATION_ANGLE / mNum;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算最小的多边形的半径
        mRadius = ((Math.min(mViewWidth, mViewHeight) / 2f) / mPolygonLayerCount) * 0.95f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布中心移动到中心点
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        //默认0度在x轴水平线，逆时针为负角度，顺时针为正角度，旋转-90则0度转到顶部的y轴开始
        canvas.rotate(-90);
        //不断旋转画布
        canvas.rotate(mCurrentAngle, 0, 0);
        //画多边形
        drawPolygon(canvas);
    }

    /**
     * 画多边形
     */
    private void drawPolygon(Canvas canvas) {
        //多边形边角顶点的x坐标
        float pointX;
        //多边形边角顶点的y坐标
        float pointY;
        //总的圆的半径，就是全部多边形的半径之和
        Path path = new Path();
        //循环画出每个多边形
        for (int j = 1; j <= mPolygonLayerCount; j++) {
            //多边形属性图，就是多少层的多边形的半径叠加，循环多遍就能组成多层
            float radius = j * mRadius;
            //画前先重置路径
            path.reset();
            for (int i = 1; i <= mNum; i++) {
                //cos三角函数，中心角的邻边 / 斜边，斜边的值刚好就是半径，cos值乘以斜边，就能求出邻边，而这个邻边的长度，就是点的x坐标
                pointX = (float) (Math.cos(i * mCenterAngle) * radius);
                //sin三角函数，中心角的对边 / 斜边，斜边的值刚好就是半径，sin值乘以斜边，就能求出对边，而这个对边的长度，就是点的y坐标
                pointY = (float) (Math.sin(i * mCenterAngle) * radius);
                //如果是一个点，则移动到这个点，作为起点
                if (i == 1) {
                    path.moveTo(pointX, pointY);
                } else {
                    //其他的点，就可以连线了
                    path.lineTo(pointX, pointY);
                }
            }
            path.close();
            canvas.drawPath(path, mPaint);
        }
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
            mCurrentAngle += mAverageAngle;
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