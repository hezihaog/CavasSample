package com.zh.cavas.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zh.cavas.sample.R;

/**
 * <b>Package:</b> com.zh.cavas.sample.widget <br>
 * <b>Create Date:</b> 2019-12-31  16:50 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 下载进度View <br>
 */
public class DownloadProgressView extends View {
    /**
     * View默认最小宽度
     */
    private int mDefaultMinWidth;
    /**
     * View默认最小高度
     */
    private int mDefaultMinHeight;

    /**
     * 控件宽
     */
    private int mViewWidth;
    /**
     * 控件高
     */
    private int mViewHeight;

    /**
     * 圆角弧度
     */
    private float mRadius;
    /**
     * 背景画笔
     */
    private Paint mBgPaint;
    /**
     * 进度画笔
     */
    private Paint mProgressPaint;
    /**
     * 文字画笔
     */
    private Paint mTextPaint;
    /**
     * 当前进度
     */
    private int mProgress;
    /**
     * 背景颜色
     */
    private int mBgColor;
    /**
     * 进度背景颜色
     */
    private int mProgressBgColor;
    /**
     * 进度百分比文字的颜色
     */
    private int mPercentageTextColor;
    /**
     * 第二层进度百分比文字的颜色
     */
    private int mPercentageTextColor2;
    /**
     * 进度百分比文字的字体大小
     */
    private float mPercentageTextSize;
    /**
     * 最大进度值
     */
    private int mMaxProgress;
    /**
     * 进度更新监听
     */
    private OnProgressUpdateListener mOnProgressUpdateListener;

    public DownloadProgressView(Context context) {
        this(context, null);
    }

    public DownloadProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        //背景画笔
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mBgColor);
        //进度画笔
        mProgressPaint = new Paint();
        mProgressPaint.setColor(mProgressBgColor);
        mProgressPaint.setAntiAlias(true);
        //文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mPercentageTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        //计算默认宽、高
        mDefaultMinWidth = dip2px(context, dip2px(context, 180f));
        mDefaultMinHeight = dip2px(context, dip2px(context, 25f));
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressView, defStyleAttr, 0);
        mBgColor = array.getColor(R.styleable.DownloadProgressView_dpv_bg, Color.argb(100, 169, 169, 169));
        mProgressBgColor = array.getColor(R.styleable.DownloadProgressView_dpv_progress_bg, Color.GRAY);
        //进度百分比文字的颜色，默认和进度背景颜色一致
        mPercentageTextColor = array.getColor(R.styleable.DownloadProgressView_dpv_percentage_text_color, mProgressBgColor);
        //第二层进度百分比文字的颜色
        mPercentageTextColor2 = array.getColor(R.styleable.DownloadProgressView_dpv_percentage_text_color2, Color.WHITE);
        mPercentageTextSize = array.getDimension(R.styleable.DownloadProgressView_dpv_percentage_text_size, sp2px(context, 15f));
        //当前进度值
        mProgress = array.getInt(R.styleable.DownloadProgressView_dpv_progress, 0);
        //最大进度值
        mMaxProgress = array.getInteger(R.styleable.DownloadProgressView_dpv_max_progress, 100);
        array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mRadius = Math.min(mViewWidth, mViewHeight) / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //裁剪圆角
        clipRound(canvas);
        //画背景
        drawBg(canvas);
        //画进度条
        drawProgress(canvas);
        //画文字图层
        drawText(canvas);
    }

    private float getFrameLeft() {
        return getPaddingStart();
    }

    private float getFrameRight() {
        return mViewWidth - getPaddingEnd();
    }

    private float getFrameTop() {
        return getPaddingTop();
    }

    private float getFrameBottom() {
        return mViewHeight - getPaddingBottom();
    }

    /**
     * 裁剪圆角
     */
    private void clipRound(Canvas canvas) {
        Path path = new Path();
        RectF roundRect = new RectF(getFrameLeft(), getFrameTop(), getFrameRight(), getFrameBottom());
        path.addRoundRect(roundRect, mRadius, mRadius, Path.Direction.CW);
        canvas.clipPath(path);
    }

    /**
     * 画背景
     */
    private void drawBg(Canvas canvas) {
        canvas.drawRect(new RectF(getFrameLeft(), getPaddingTop(), getFrameRight(), getFrameBottom()), mBgPaint);
    }

    /**
     * 画进度
     */
    private void drawProgress(Canvas canvas) {
        RectF rect = new RectF(getFrameLeft(), getFrameTop(), getFrameRight() * getProgressRatio(), getFrameBottom());
        canvas.drawRect(rect, mProgressPaint);
    }

    /**
     * 画文字
     */
    private void drawText(Canvas canvas) {
        mTextPaint.setColor(mPercentageTextColor);
        //创建文字图层
        Bitmap textBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
        Canvas textCanvas = new Canvas(textBitmap);
        String textContent = mProgress + "%";
        //计算文字Y轴坐标
        float textY = mViewHeight / 2.0f - (mTextPaint.getFontMetricsInt().descent / 2.0f + mTextPaint.getFontMetricsInt().ascent / 2.0f);
        textCanvas.drawText(textContent, mViewWidth / 2.0f, textY, mTextPaint);
        //画最上层的白色图层，未相交时不会显示出来
        mTextPaint.setColor(mPercentageTextColor2);
        mTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        textCanvas.drawRect(new RectF(getFrameLeft(), getFrameTop(), getFrameRight() * getProgressRatio(), getFrameBottom()), mTextPaint);
        //画结合后的图层
        canvas.drawBitmap(textBitmap, getFrameLeft(), getFrameTop(), mTextPaint);
        mTextPaint.setXfermode(null);
        textBitmap.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(handleMeasure(widthMeasureSpec, true), handleMeasure(heightMeasureSpec, false));
    }

    /**
     * 处理MeasureSpec
     */
    private int handleMeasure(int measureSpec, boolean isWidth) {
        int result;
        if (isWidth) {
            result = mDefaultMinWidth;
        } else {
            result = mDefaultMinHeight;
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

    /**
     * 设置进度
     */
    public void setProgress(int progress) {
        if (progress >= 0 && progress <= 100) {
            mProgress = progress;
            invalidate();
            if (mOnProgressUpdateListener != null) {
                mOnProgressUpdateListener.onProgressUpdate(progress);
            }
        }
    }

    /**
     * 获取当前进度
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * 获取最大进度
     */
    public int getMaxProgress() {
        return mMaxProgress;
    }

    public interface OnProgressUpdateListener {
        /**
         * 进度更新时回调
         *
         * @param progress 当前进度
         */
        void onProgressUpdate(int progress);
    }

    public void setOnProgressUpdateListener(OnProgressUpdateListener onProgressUpdateListener) {
        mOnProgressUpdateListener = onProgressUpdateListener;
    }

    /**
     * 获取当前进度值比值
     */
    private float getProgressRatio() {
        return (mProgress / (mMaxProgress * 1.0f));
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}