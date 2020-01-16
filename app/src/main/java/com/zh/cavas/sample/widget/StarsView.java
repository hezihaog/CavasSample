package com.zh.cavas.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zh.cavas.sample.R;

/**
 * <b>Package:</b> com.zh.cavas.sample.widget <br>
 * <b>Create Date:</b> 2020-01-15  16:26 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 画星星，参考慕课网视频教程 link{https://www.imooc.com/video/3488?t=1474563809992} <br>
 */
public class StarsView extends View {
    /**
     * View默认最小宽度
     */
    private static final int DEFAULT_MIN_WIDTH = 100;
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
     * 外边大圆的半径
     */
    private float mOutCircleRadius;
    /**
     * 里面小圆的的半径
     */
    private float mInnerCircleRadius;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 多少个角的五角星
     */
    private int mAngleNum;
    /**
     * 星星的路径
     */
    private Path mPath;
    /**
     * 星星的颜色
     */
    private int mColor;
    /**
     * 边的线宽
     */
    private float mEdgeLineWidth;
    /**
     * 填充风格
     */
    private int mStyle;

    public StarsView(Context context) {
        this(context, null);
    }

    public StarsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        //取消硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        if (mStyle == STYLE_FILL) {
            mPaint.setStyle(Paint.Style.FILL);
        } else if (mStyle == STYLE_STROKE) {
            mPaint.setStyle(Paint.Style.STROKE);
        }
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mEdgeLineWidth);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int defaultColor = Color.argb(255, 0, 0, 0);
        int defaultNum = 5;
        int mineNum = 2;
        float defaultEdgeLineWidth = dip2px(context, 1f);
        int defaultStyle = STYLE_STROKE;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.StarsView, defStyleAttr, 0);
            mColor = array.getColor(R.styleable.StarsView_stv_color, defaultColor);
            int num = array.getInt(R.styleable.StarsView_stv_num, defaultNum);
            mAngleNum = num <= mineNum ? mineNum : num;
            mEdgeLineWidth = array.getDimension(R.styleable.StarsView_stv_edge_line_width, defaultEdgeLineWidth);
            mStyle = array.getInt(R.styleable.StarsView_stv_style, defaultStyle);
            array.recycle();
        } else {
            mColor = defaultColor;
            mAngleNum = defaultNum;
            mEdgeLineWidth = defaultEdgeLineWidth;
            mStyle = defaultStyle;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算外边大圆的半径
        mOutCircleRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.95f;
        //计算里面小圆的的半径
        mInnerCircleRadius = (Math.min(mViewWidth, mViewHeight) / 2f) * 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布中心移动到中心点
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        //画星星
        drawStars(canvas);
    }

    /**
     * 画星星
     */
    private void drawStars(Canvas canvas) {
        //计算平均角度，例如360度分5份，每一份角都为72度
        float averageAngle = 360f / mAngleNum;
        //计算大圆的外角的角度，从右上角为例计算，90度的角减去一份角，得出剩余的小角的角度，例如90 - 72 = 18 度
        float outCircleAngle = 90 - averageAngle;
        //一份平均角度的一半，例如72 / 2 = 36度
        float halfAverageAngle = averageAngle / 2f;
        //计算出小圆内角的角度，36 + 18 = 54 度
        float internalAngle = halfAverageAngle + outCircleAngle;
        //创建2个点
        Point outCirclePoint = new Point();
        Point innerCirclePoint = new Point();
        if (mPath == null) {
            mPath = new Path();
        }
        mPath.reset();
        for (int i = 0; i < mAngleNum; i++) {
            //计算大圆上的点坐标
            //x = Math.cos((18 + 72 * i) / 180f * Math.PI) * 大圆半径
            //y = -Math.sin((18 + 72 * i)/ 180f * Math.PI) * 大圆半径
            outCirclePoint.x = (int) (Math.cos(angleToRadian(outCircleAngle + i * averageAngle)) * mOutCircleRadius);
            outCirclePoint.y = (int) -(Math.sin(angleToRadian(outCircleAngle + i * averageAngle)) * mOutCircleRadius);
            //计算小圆上的点坐标
            //x = Math.cos((54 + 72 * i) / 180f * Math.PI ) * 小圆半径
            //y = -Math.sin((54 + 72 * i) / 180 * Math.PI ) * 小圆半径
            innerCirclePoint.x = (int) (Math.cos(angleToRadian(internalAngle + i * averageAngle)) * mInnerCircleRadius);
            innerCirclePoint.y = (int) -(Math.sin(angleToRadian(internalAngle + i * averageAngle)) * mInnerCircleRadius);
            //第一次，先移动到第一个大圆上的点
            if (i == 0) {
                mPath.moveTo(outCirclePoint.x, outCirclePoint.y);
            }
            //坐标连接，先大圆角上的点，再到小圆角上的点
            mPath.lineTo(outCirclePoint.x, outCirclePoint.y);
            mPath.lineTo(innerCirclePoint.x, innerCirclePoint.y);
        }
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 角度转弧度，由于Math的三角函数需要传入弧度制，而不是角度值，所以要角度换算为弧度，角度 / 180 * π
     *
     * @param angle 角度
     * @return 弧度
     */
    private double angleToRadian(float angle) {
        return angle / 180f * Math.PI;
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