package com.zh.cavas.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zh.cavas.sample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Package:</b> com.zh.cavas.sample.widget <br>
 * <b>Create Date:</b> 2020-01-14  16:44 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class ShareView extends View {
    /**
     * View默认最小宽度
     */
    private static final int DEFAULT_MIN_WIDTH = 100;

    /**
     * 将360度分成多少份，调整这个变量，可以调整角度s
     */
    private int mNum = 8;

    /**
     * 360度对应的弧度（为什么2π就是360度？弧度的定义：弧长 / 半径，一个圆的周长是2πr，如果是一个360度的圆，它的弧长就是2πr，如果这个圆的半径r长度为1，那么它的弧度就是，2πr / r = 2π）
     */
    private double mPiDouble = 2 * Math.PI;
    /**
     * Math类的sin和cos需要传入的角度值是弧度制，所以这里的中心角的角度，也是弧度制的弧度
     */
    private float mCenterAngle = (float) (mPiDouble / mNum);

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
    private Paint mLinePaint;
    /**
     * 外圆的半径
     */
    private float mRadius;
    /**
     * 小圆的半径
     */
    private float mSmallRadius;
    /**
     * 小圆的描边画笔
     */
    private Paint mSmallCircleStrokePaint;
    /**
     * 小圆画笔
     */
    private Paint mSmallCirclePaint;
    /**
     * 3个圆中心的坐标点
     */
    private List<PointF> mPointList;
    private PointF mCenterPoint;
    private PointF mRightPoint;
    private PointF mThirdPoint;
    /**
     * 小圆描边线宽
     */
    private float mStrokeWidth;
    /**
     * 颜色
     */
    private int mColor;
    /**
     * 小圆的内部填充颜色
     */
    private int mSmallCircleSolidColor;

    public ShareView(Context context) {
        this(context, null);
    }

    public ShareView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShareView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        //取消硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //线的画笔
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(mColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setStrokeWidth(mStrokeWidth);
        //小圆的画笔
        mSmallCirclePaint = new Paint();
        mSmallCirclePaint.setAntiAlias(true);
        mSmallCirclePaint.setColor(mSmallCircleSolidColor);
        mSmallCirclePaint.setStyle(Paint.Style.FILL);
        mSmallCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mSmallCirclePaint.setStrokeWidth(mStrokeWidth);
        //小圆的描边画笔
        mSmallCircleStrokePaint = new Paint();
        mSmallCircleStrokePaint.setAntiAlias(true);
        mSmallCircleStrokePaint.setColor(mColor);
        mSmallCircleStrokePaint.setStyle(Paint.Style.STROKE);
        mSmallCircleStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mSmallCircleStrokePaint.setStrokeWidth(mStrokeWidth);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int defaultColor = Color.argb(255, 0, 0, 0);
        int defaultSmallCircleSolidColor = Color.argb(255, 255, 255, 255);
        int defaultLineWidth = dip2px(context, 1f);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ShareView, defStyleAttr, 0);
            mColor = array.getColor(R.styleable.ShareView_sv_color, defaultColor);
            mSmallCircleSolidColor = array.getColor(R.styleable.ShareView_sv_small_circle_solid_color, defaultSmallCircleSolidColor);
            mStrokeWidth = array.getDimension(R.styleable.ShareView_sv_line_width, defaultLineWidth);
            array.recycle();
        } else {
            mColor = defaultColor;
            mSmallCircleSolidColor = defaultSmallCircleSolidColor;
            mStrokeWidth = defaultLineWidth;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算外圆的半径
        mRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.8f;
        //计算小圆的半径
        mSmallRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.15f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //移动画布
        canvas.translate(mViewWidth / 3f, mViewHeight / 2f);
        //旋转画布，让画的线居中
        float degrees = (360f / mNum) / 2;
        canvas.rotate(-degrees);
        //生成点坐标
        generatePoint();
        //画路径
        drawPath(canvas);
        //画点
        drawSmallCircle(canvas);
    }

    /**
     * 创建点
     */
    private void generatePoint() {
        if (mPointList == null) {
            mPointList = new ArrayList<>();
            //中心点坐标
            mCenterPoint = new PointF(0, 0);
            mPointList.add(mCenterPoint);
            //右边的点
            mRightPoint = new PointF(mRadius, 0);
            mPointList.add(mRightPoint);
            //第三个点
            float thirdPointX = (float) Math.cos(mCenterAngle) * mRadius;
            float thirdPointY = (float) Math.sin(mCenterAngle) * mRadius;
            mThirdPoint = new PointF(thirdPointX, thirdPointY);
            mPointList.add(mThirdPoint);
        }
    }

    /**
     * 画路径
     */
    private void drawPath(Canvas canvas) {
        Path path = new Path();
        path.reset();
        //1.移动到中心点
        path.moveTo(mCenterPoint.x, mCenterPoint.y);
        //2.画右边的点
        path.lineTo(mRightPoint.x, mRightPoint.y);
        //移动回中心点
        path.moveTo(mCenterPoint.x, mCenterPoint.y);
        //4.画第三个点
        path.lineTo(mThirdPoint.x, mThirdPoint.y);
        path.close();
        //画路径
        canvas.drawPath(path, mLinePaint);
    }

    /**
     * 画小圆
     */
    private void drawSmallCircle(Canvas canvas) {
        for (PointF point : mPointList) {
            //画小圆描边
            canvas.drawCircle(point.x, point.y, mSmallRadius, mSmallCircleStrokePaint);
            //计算小圆的圆半径，为整圆半径减去线宽
            float radius = mSmallRadius - mStrokeWidth;
            //画小圆
            canvas.drawCircle(point.x, point.y, radius, mSmallCirclePaint);
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