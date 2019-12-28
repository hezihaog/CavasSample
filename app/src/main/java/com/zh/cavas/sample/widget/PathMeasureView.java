package com.zh.cavas.sample.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

/**
 * <b>Package:</b> com.zh.cavas.sample <br>
 * <b>Create Date:</b> 2019-12-25  10:12 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class PathMeasureView extends View {
    private Paint mPaint = new Paint();
    private float mRadius;//圆的半径
    private int duration = 800;//动画时长
    private float start; //路径开始位置
    private float end;   //路径的结束位置
    private PathMeasure measure;
    private Path path;
    private Path dst;
    private ValueAnimator mAnimator;
    private int mViewWidth;
    private int mViewHeight;

    public PathMeasureView(Context context) {
        this(context, null);
    }

    public PathMeasureView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathMeasureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //设置画笔样式为描边
        mPaint.setStyle(Paint.Style.STROKE);
        //设置颜色为红色
        mPaint.setColor(Color.RED);
        //设置画笔宽度
        mPaint.setStrokeWidth(15);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mRadius = (Math.min(w, h) / 2f) * 0.8f;

        //新建一个路径
        path = new Path();
        //添加圆为路径 参数 ： x坐标 y坐标 半径 顺势针/逆时针
        path.addCircle(0, 0, mRadius, Path.Direction.CW);
        //这是我们今天的主角，PathMeasure 关联上面的圆。 forceClosed 为true
        measure = new PathMeasure(path, true);

        //开启动画
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        //创建属性动画 动画的值 就是 从0 到 measure.getLength()
        //measure.getLength() 就是上面圆的路径的总长度
        mAnimator = ValueAnimator.ofFloat(0, measure.getLength());
        //根据getAnimatedValue 改变要画弧线的开始与结束位置
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                start = (float) animation.getAnimatedValue();
                end = start * 3 / 2;
                if (end - start > 150) {
                    end = start + 150;
                }
                if (end > measure.getLength()) {
                    end = measure.getLength();
                }
                invalidate();
            }
        });
        //添加线性插值器
        mAnimator.setInterpolator(new LinearInterpolator());
        //设置重复次数为3
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        //设置动画时长
        mAnimator.setDuration(duration);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        //再建一个路径
        dst = new Path();
        //通过PathMeasure的getSegment方法截取一段路径保存在dst路径中
        measure.getSegment(start, end, dst, true);
        //画出截取的路径
        canvas.drawPath(dst, mPaint);
    }
}