package com.zh.cavas.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.zh.cavas.sample.R;

import androidx.annotation.Nullable;

/**
 * <b>Package:</b> com.zh.cavas.sample <br>
 * <b>Create Date:</b> 2019-12-28  09:35 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 导航栏图标View <br>
 */
public class NavigationBarIconView extends View {
    /**
     * View默认最小宽度
     */
    private static final int DEFAULT_MIN_WIDTH = 80;

    /**
     * 图形：三角形
     */
    private static final int SHAPE_TRIANGLE = 1;
    /**
     * 图形：圆形
     */
    private static final int SHAPE_CIRCLE = 2;
    /**
     * 图形：内嵌圆
     */
    private static final int SHAPE_NESTED_CIRCLE = 3;
    /**
     * 图形：正方形
     */
    private static final int SHAPE_SQUARE = 4;

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
     * 线颜色
     */
    private int mColor;
    /**
     * 线宽
     */
    private float mLineWidth;
    /**
     * 图形策略
     */
    private ShapeStrategy mShapeStrategy;

    public NavigationBarIconView(Context context) {
        this(context, null);
    }

    public NavigationBarIconView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationBarIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        mPaint = new Paint();
        //设置拐角形状为圆形
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mLineWidth);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int defaultColor = Color.argb(255, 0, 0, 0);
        int defaultLineWidth = dip2px(context, 1.5f);
        //图形
        int shape;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NavigationBarIconView, defStyleAttr, 0);
            mColor = array.getColor(R.styleable.NavigationBarIconView_naviv_color, defaultColor);
            mLineWidth = array.getDimension(R.styleable.NavigationBarIconView_naviv_line_width, defaultLineWidth);
            shape = array.getInt(R.styleable.NavigationBarIconView_naviv_shape, SHAPE_TRIANGLE);
            array.recycle();
        } else {
            mColor = defaultColor;
            mLineWidth = defaultLineWidth;
            shape = SHAPE_TRIANGLE;
        }
        if (shape == SHAPE_TRIANGLE) {
            mShapeStrategy = new TriangleStrategy();
        } else if (shape == SHAPE_CIRCLE) {
            mShapeStrategy = new CircleStrategy();
        } else if (shape == SHAPE_NESTED_CIRCLE) {
            mShapeStrategy = new NestedCircleStrategy();
        } else if (shape == SHAPE_SQUARE) {
            mShapeStrategy = new SquareStrategy();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mShapeStrategy.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布中心移动到中心点
        canvas.translate(mViewWidth / 2f, mViewHeight / 2f);
        //让策略
        mShapeStrategy.onDraw(canvas);
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

    /**
     * 抽象图形策略接口
     */
    public interface ShapeStrategy {
        /**
         * 大小改变回调
         */
        void onSizeChanged(int w, int h, int oldw, int oldh);

        /**
         * 绘制回调
         */
        void onDraw(Canvas canvas);
    }

    /**
     * 基础图形
     */
    public abstract static class BaseShapeStrategy implements ShapeStrategy {
        protected int mViewWidth;
        protected int mViewHeight;

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            mViewWidth = w;
            mViewHeight = h;
        }
    }

    /**
     * 三角形
     */
    public class TriangleStrategy extends BaseShapeStrategy {
        /**
         * 多边形的边数
         */
        private int mNum;
        /**
         * 多边形中心角的角度（每个多边形的内角和为360度，一个多边形2个相邻角顶点和中心的连线所组成的角为中心角
         * 中心角的角度都是一样的，所以360度除以多边形的边数，就是一个中心角的角度），这里注意，因为后续要用到Math类的三角函数
         * Math类的sin和cos需要传入的角度值是弧度制，所以这里的中心角的角度，也是弧度制的弧度
         */
        private float mCenterAngle;
        /**
         * 多边形的半径
         */
        private float mRadius;
        /**
         * 360度对应的弧度（为什么2π就是360度？弧度的定义：弧长 / 半径，一个圆的周长是2πr，如果是一个360度的圆，它的弧长就是2πr，如果这个圆的半径r长度为1，那么它的弧度就是，2πr / r = 2π）
         */
        private final double mPiDouble = 2 * Math.PI;
        /**
         * 路径
         */
        private Path mPath;

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            //三角形
            mNum = 3;
            //计算中心角弧度
            mCenterAngle = (float) (mPiDouble / mNum);
            //计算最小的多边形的半径
            mRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.95f;
        }

        @Override
        public void onDraw(Canvas canvas) {
            //将三角形的方向向左
            canvas.rotate(180);
            //多边形边角顶点的x坐标
            float pointX;
            //多边形边角顶点的y坐标
            float pointY;
            //总的圆的半径，就是全部多边形的半径之和
            if (mPath == null) {
                mPath = new Path();
            }
            //画前先重置路径
            mPath.reset();
            for (int i = 1; i <= mNum; i++) {
                //cos三角函数，中心角的邻边 / 斜边，斜边的值刚好就是半径，cos值乘以斜边，就能求出邻边，而这个邻边的长度，就是点的x坐标
                pointX = (float) (Math.cos(i * mCenterAngle) * mRadius);
                //sin三角函数，中心角的对边 / 斜边，斜边的值刚好就是半径，sin值乘以斜边，就能求出对边，而这个对边的长度，就是点的y坐标
                pointY = (float) (Math.sin(i * mCenterAngle) * mRadius);
                //如果是一个点，则移动到这个点，作为起点
                if (i == 1) {
                    mPath.moveTo(pointX, pointY);
                } else {
                    //其他的点，就可以连线了
                    mPath.lineTo(pointX, pointY);
                }
            }
            mPath.close();
            canvas.drawPath(mPath, mPaint);
        }
    }

    /**
     * 圆形
     */
    public class CircleStrategy extends BaseShapeStrategy {
        /**
         * 圆形半径s
         */
        protected float mRadius;

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mRadius = (Math.min(mViewWidth, mViewHeight) * 0.90f) / 2f;
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawCircle(0, 0, mRadius, mPaint);
        }
    }

    /**
     * 内嵌圆形
     */
    public class NestedCircleStrategy extends CircleStrategy {
        @Override
        public void onDraw(Canvas canvas) {
            canvas.save();
            //画外边圆，描边风格
            mPaint.setStyle(Paint.Style.STROKE);
            super.onDraw(canvas);
            //画内圆，填充风格
            mPaint.setStyle(Paint.Style.FILL);
            float scaleValue = 0.75f;
            canvas.scale(scaleValue, scaleValue);
            canvas.drawCircle(0, 0, mRadius, mPaint);
            canvas.restore();
        }
    }

    /**
     * 正方形
     */
    public class SquareStrategy extends BaseShapeStrategy {
        /**
         * 圆形半径s
         */
        private float mRadius;
        /**
         * 圆角矩形的圆角半径
         */
        private float mRoundRectCircle;
        /**
         * 圆角矩形位置
         */
        private RectF mRectF;

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mRadius = (Math.min(mViewWidth, mViewHeight) * 0.90f) / 2f;
            mRoundRectCircle = (Math.min(mViewWidth, mViewHeight) * 0.1f) / 2f;
            if (mRectF == null) {
                mRectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
            } else {
                mRectF.set(-mRadius, -mRadius, mRadius, mRadius);
            }
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawRoundRect(mRectF, mRoundRectCircle, mRoundRectCircle, mPaint);
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}