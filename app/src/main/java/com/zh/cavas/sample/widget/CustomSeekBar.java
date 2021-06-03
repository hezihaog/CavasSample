package com.zh.cavas.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.zh.cavas.sample.R;

public class CustomSeekBar extends View {
    /**
     * View默认最小宽度
     */
    private int mDefaultWidth;
    /**
     * View默认最小高度
     */
    private int mDefaultHeight;
    /**
     * 文字和进度条之间的距离
     */
    private float mProgressTextDistance;
    /**
     * 控件宽
     */
    private int mViewWidth;
    /**
     * 控件高
     */
    private int mViewHeight;
    /**
     * 背景颜色
     */
    private int mBgColor;
    /**
     * 进度背景颜色
     */
    private int mProgressBgColor;
    /**
     * 进度的高度
     */
    private int mProgressHeight;
    /**
     * 滑块颜色
     */
    private int mThumbColor;
    /**
     * 滑块圆的半径
     */
    private int mThumbRadius;
    /**
     * 进度的文字颜色
     */
    private int mProgressTextColor;
    /**
     * 进度的文字大小
     */
    private float mProgressTextSize;
    /**
     * 当前进度
     */
    private int mProgress;
    /**
     * 最小进度值
     */
    private int mMin;
    /**
     * 最大进度值
     */
    private int mMax;
    /**
     * 是否有进度文字
     */
    private boolean hasProgressText;
    /**
     * 背景画笔
     */
    private Paint mBgPaint;
    /**
     * 进度画笔
     */
    private Paint mProgressPaint;
    /**
     * 滑块画笔
     */
    private Paint mThumbPaint;
    /**
     * 文字画笔
     */
    private Paint mTextPaint;
    /**
     * 进度更新监听
     */
    private OnProgressUpdateListener mOnProgressUpdateListener;
    /**
     * 按下时Down事件的x坐标
     */
    private float mTouchDownX;

    public CustomSeekBar(Context context) {
        this(context, null);
    }

    public CustomSeekBar(Context context,
                         @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        //取消硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //背景画笔
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setStyle(Paint.Style.FILL);
        //进度画笔
        mProgressPaint = new Paint();
        mProgressPaint.setColor(mProgressBgColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.FILL);
        //滑块画笔
        mThumbPaint = new Paint();
        mThumbPaint.setAntiAlias(true);
        mThumbPaint.setColor(mThumbColor);
        mThumbPaint.setStyle(Paint.Style.FILL);
        //文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mProgressTextColor);
        mTextPaint.setTextSize(mProgressTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        //文字和进度条之间的距离
        mProgressTextDistance = dip2px(getContext(), 13f);
        //计算默认宽、高
        mDefaultWidth = dip2px(context, 180f);
        if (hasProgressText) {
            //有文字，计算公式：高度 = 文字高度 + 间隔距离 + 滑块高度
            mDefaultHeight = (int) ((mThumbRadius * 2)
                    + getPaintTextHeight(mTextPaint, "1分钟")
                    + mProgressTextDistance
                    + getPaddingTop()
                    + getPaddingBottom()
            );
        } else {
            //没有文字，计算公式：滑块高度
            mDefaultHeight = (mThumbRadius * 2) + getPaddingTop() + getPaddingBottom();
        }
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int defaultBgColor = Color.argb(100, 169, 169, 169);
        int defaultProgressBgColor = Color.GRAY;
        int defaultProgressHeight = dip2px(context, 2f);
        int defaultThumbColor = Color.WHITE;
        int defaultThumbRadius = dip2px(context, 7f);
        int defaultProgressTextColor = Color.BLACK;
        int defaultProgressTextSize = sp2px(context, 12f);
        int defaultProgress = 0;
        int defaultMinProgress = 0;
        int defaultMaxProgress = 100;
        if (attrs != null) {
            TypedArray array =
                    context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar, defStyleAttr, 0);
            //进度背景颜色
            mBgColor = array.getColor(R.styleable.CustomSeekBar_csb_bg, defaultBgColor);
            //已有进度的背景颜色
            mProgressBgColor =
                    array.getColor(R.styleable.CustomSeekBar_csb_progress_bg, defaultProgressBgColor);
            //进度条高度
            mProgressHeight =
                    array.getDimensionPixelSize(R.styleable.CustomSeekBar_csb_progress_height,
                            defaultProgressHeight);
            //滑块颜色
            mThumbColor = array.getColor(R.styleable.CustomSeekBar_csb_thumb_color, defaultThumbColor);
            //滑块圆的半径
            mThumbRadius = array.getDimensionPixelSize(R.styleable.CustomSeekBar_csb_thumb_radius,
                    defaultThumbRadius);
            //进度的文字颜色
            mProgressTextColor = array.getColor(R.styleable.CustomSeekBar_csb_progress_text_color,
                    defaultProgressTextColor);
            //进度的文字大小
            mProgressTextSize = array.getDimension(R.styleable.CustomSeekBar_csb_progress_text_size,
                    defaultProgressTextSize);
            //当前进度值
            mProgress = array.getInteger(R.styleable.CustomSeekBar_csb_progress, defaultProgress);
            //最小进度值
            mMin = array.getInteger(R.styleable.CustomSeekBar_csb_min_progress, defaultMinProgress);
            //最大进度值
            mMax = array.getInteger(R.styleable.CustomSeekBar_csb_max_progress, defaultMaxProgress);
            //是否有进度文字
            hasProgressText = array.getBoolean(R.styleable.CustomSeekBar_csb_has_progress_text, false);
            array.recycle();
        } else {
            mBgColor = defaultBgColor;
            mProgressBgColor = defaultProgressBgColor;
            mProgressHeight = defaultProgressHeight;
            mThumbColor = defaultThumbColor;
            mThumbRadius = defaultThumbRadius;
            mProgress = defaultProgress;
            mMin = defaultMinProgress;
            mMax = defaultMaxProgress;
            hasProgressText = false;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画背景
        drawBg(canvas);
        //画进度条
        drawProgress(canvas);
        //画滑块
        drawThumb(canvas);
        //画文字
        if (hasProgressText) {
            drawText(canvas);
        }
    }

    //------------ getFrameXxx()方法都是处理padding ------------

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

    //------------ getFrameXxx()方法都是处理padding ------------

    /**
     * 画背景
     */
    private void drawBg(Canvas canvas) {
        float halfProgressHeight = mProgressHeight / 2f;
        float halfViewHeight = mViewHeight / 2f;
        canvas.drawRect(new RectF(getFrameLeft() + mThumbRadius, halfViewHeight - halfProgressHeight,
                        getFrameRight() - mThumbRadius, halfViewHeight + halfProgressHeight),
                mBgPaint);
    }

    /**
     * 画进度
     */
    private void drawProgress(Canvas canvas) {
        float halfProgressHeight = mProgressHeight / 2f;
        float halfViewHeight = mViewHeight / 2f;
        RectF rect = new RectF(getFrameLeft() + mThumbRadius, halfViewHeight - halfProgressHeight,
                (getFrameRight() - mThumbRadius) * getProgressRatio(),
                halfViewHeight + halfProgressHeight);
        canvas.drawRect(rect, mProgressPaint);
    }

    /**
     * 绘制滑块
     */
    private void drawThumb(Canvas canvas) {
        float halfViewHeight = mViewHeight / 2f;
        float x = (getFrameLeft() + mThumbRadius) + ((getFrameRight() - (mThumbRadius * 2)) * getProgressRatio());
        canvas.drawCircle(x, halfViewHeight, mThumbRadius,
                mThumbPaint);
    }

    /**
     * 画文字
     */
    private void drawText(Canvas canvas) {
        //创建文字图层
        String textContent = mProgress + "分钟";
        //计算文字X轴坐标
        float x = getFrameRight() * getProgressRatio();
        //计算文字Y轴坐标，一半的高度 - 滑块的半径 - 间隔距离的一半
        float textY = (mViewHeight / 2f) - mThumbRadius - (mProgressTextDistance / 2f);
        //绘字
        canvas.drawText(textContent, x, textY, mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(handleMeasure(widthMeasureSpec, true),
                handleMeasure(heightMeasureSpec, false));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        //拦截事件，然后让父类不进行拦截
        if (action == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
            mTouchDownX = event.getX();
            if (mOnProgressUpdateListener != null) {
                mOnProgressUpdateListener.onStartTrackingTouch(this);
            }
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        //保存Down事件时的x坐标
        if (action == MotionEvent.ACTION_DOWN) {
            return true;
        } else if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
            //Move或Up的时候，计算拽托进度
            float endX = event.getX();
            //计算公式：百分比值 = 移动距离 / 总长度
            float distanceX = Math.abs(endX - mTouchDownX);
            float ratio = (distanceX * 1.0f) / (getFrameRight() - getFrameLeft());
            //计算百分比应该有的进度：进度 = 总进度 * 进度百分比值
            float progress = mMax * ratio;
            setProgress((int) progress, true);
            if (action == MotionEvent.ACTION_UP) {
                if (mOnProgressUpdateListener != null) {
                    mOnProgressUpdateListener.onStopTrackingTouch(this);
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 处理MeasureSpec
     */
    private int handleMeasure(int measureSpec, boolean isWidth) {
        int result;
        if (isWidth) {
            result = mDefaultWidth;
        } else {
            result = mDefaultHeight;
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
     * 设置进度背景颜色
     */
    public void setBgColor(int bgColor) {
        this.mBgColor = bgColor;
        mBgPaint.setColor(bgColor);
        invalidate();
    }

    /**
     * 设置已有进度的背景颜色
     */
    public void setProgressBgColor(int progressBgColor) {
        this.mProgressBgColor = progressBgColor;
        mProgressPaint.setColor(progressBgColor);
        invalidate();
    }

    /**
     * 设置滑块的颜色
     */
    public void setThumbColor(int thumbColor) {
        this.mThumbColor = thumbColor;
        mThumbPaint.setColor(thumbColor);
        invalidate();
    }

    /**
     * 设置滑块的半径
     */
    public void setThumbRadius(int thumbRadius) {
        this.mThumbRadius = thumbRadius;
        invalidate();
    }

    /**
     * 设置进度
     */
    public void setProgress(int progress) {
        setProgress(progress, false);
    }

    /**
     * 设置进度
     *
     * @param fromUser 是否是用户触摸发生的改变
     */
    public void setProgress(int progress, boolean fromUser) {
        if (progress >= mMin && progress <= mMax) {
            mProgress = progress;
            invalidate();
            if (mOnProgressUpdateListener != null) {
                mOnProgressUpdateListener.onProgressUpdate(this, progress, fromUser);
            }
        }
    }

    /**
     * 获取当前进度
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * 设置进度最小值
     */
    public CustomSeekBar setMin(int min) {
        this.mMin = min;
        invalidate();
        return this;
    }

    /**
     * 获取最小进度
     */
    public int getMin() {
        return mMin;
    }

    /**
     * 设置进度最大值
     */
    public CustomSeekBar setMax(int max) {
        this.mMax = max;
        invalidate();
        return this;
    }

    /**
     * 获取最大进度
     */
    public int getMax() {
        return mMax;
    }

    public interface OnProgressUpdateListener {
        /**
         * 按下时回调
         */
        void onStartTrackingTouch(CustomSeekBar seekBar);

        /**
         * 进度更新时回调
         *
         * @param progress 当前进度
         * @param fromUser 是否是用户改变的
         */
        void onProgressUpdate(CustomSeekBar seekBar, int progress, boolean fromUser);

        void onStopTrackingTouch(CustomSeekBar seekBar);
    }

    public void setOnProgressUpdateListener(
            OnProgressUpdateListener onProgressUpdateListener) {
        mOnProgressUpdateListener = onProgressUpdateListener;
    }

    /**
     * 获取当前进度值比值
     */
    public float getProgressRatio() {
        return (mProgress / (mMax * 1.0f));
    }

    /**
     * 获取滑块的半径
     */
    public int getThumbRadius() {
        return mThumbRadius;
    }

    /**
     * 获取文字Paint画笔，画出来的文字的高度
     */
    private float getPaintTextHeight(Paint paint, String text) {
        return paint.measureText(text);
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
