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
   * 滑块中心点，距离最左边和最右边的偏移量，一般是滑块的半径
   */
  private int mThumbOffset;
  /**
   * 当前进度
   */
  private float mProgress;
  /**
   * 最小进度值
   */
  private float mMin;
  /**
   * 最大进度值
   */
  private float mMax;
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
    //计算默认宽、高
    mDefaultMinWidth = dip2px(context, 180f);
    mDefaultMinHeight = mThumbRadius * 2;
  }

  private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    int defaultBgColor = Color.argb(100, 169, 169, 169);
    int defaultProgressBgColor = Color.GRAY;
    int defaultProgressHeight = dip2px(context, 2f);
    int defaultThumbColor = Color.WHITE;
    int defaultThumbRadius = dip2px(context, 7f);
    float defaultProgress = 0;
    float defaultMinProgress = 0f;
    float defaultMaxProgress = 100f;

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
      //滑块的偏移量，默认是滑块的半径
      mThumbOffset =
          array.getDimensionPixelSize(R.styleable.CustomSeekBar_csb_thumb_offset, mThumbRadius);
      //当前进度值
      mProgress = array.getFloat(R.styleable.CustomSeekBar_csb_progress, defaultProgress);
      //最小进度值
      mMin = array.getFloat(R.styleable.CustomSeekBar_csb_min_progress, defaultMinProgress);
      //最大进度值
      mMax = array.getFloat(R.styleable.CustomSeekBar_csb_max_progress, defaultMaxProgress);
      array.recycle();
    } else {
      mBgColor = defaultBgColor;
      mProgressBgColor = defaultProgressBgColor;
      mProgressHeight = defaultProgressHeight;
      mThumbColor = defaultThumbColor;
      mThumbRadius = defaultThumbRadius;
      mThumbOffset = mThumbRadius;
      mProgress = defaultProgress;
      mMin = defaultMinProgress;
      mMax = defaultMaxProgress;
    }
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mViewWidth = w;
    mViewHeight = h;
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    //画背景
    drawBg(canvas);
    //画进度条
    drawProgress(canvas);
    //画滑块
    drawThumb(canvas);
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
    canvas.drawRect(new RectF(getFrameLeft(), halfViewHeight - halfProgressHeight,
            getFrameRight(), halfViewHeight + halfProgressHeight),
        mBgPaint);
  }

  /**
   * 画进度
   */
  private void drawProgress(Canvas canvas) {
    float halfProgressHeight = mProgressHeight / 2f;
    float halfViewHeight = mViewHeight / 2f;
    RectF rect = new RectF(getFrameLeft(), halfViewHeight - halfProgressHeight,
        getFrameRight() * getProgressRatio(),
        halfViewHeight + halfProgressHeight);
    canvas.drawRect(rect, mProgressPaint);
  }

  /**
   * 绘制滑块
   */
  private void drawThumb(Canvas canvas) {
    float halfViewHeight = mViewHeight / 2f;
    float x = getFrameRight() * getProgressRatio();
    float finalX;
    //修复到最左侧时的偏移
    if (mProgress <= (mMax * 0.1f)) {
      finalX = x + mThumbOffset;
    } else if (mProgress >= (mMax - (mMax * 0.1f))) {
      //修复到最右侧时的偏移
      finalX = x - mThumbOffset;
    } else {
      finalX = x;
    }
    canvas.drawCircle(finalX, halfViewHeight, mThumbRadius,
        mThumbPaint);
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
      return true;
    }
    return super.dispatchTouchEvent(event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int action = event.getAction();
    //包裹Down事件时的x坐标
    if (action == MotionEvent.ACTION_DOWN) {
      mTouchDownX = event.getX();
      return true;
    } else if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
      //Move或Up的时候，计算拽托进度
      float endX = event.getX();
      //计算公式：百分比值 = 移动距离 / 总长度
      float distanceX = Math.abs(endX - mTouchDownX);
      float ratio = (distanceX * 1.0f) / (getFrameRight() - getFrameLeft());
      //计算百分比应该有的进度：进度 = 总进度 * 进度百分比值
      float progress = mMax * ratio;
      setProgress((int) progress);
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
    if (progress >= mMin && progress <= mMax) {
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
  public float getProgress() {
    return mProgress;
  }

  /**
   * 设置进度最小值
   */
  public CustomSeekBar setMin(float min) {
    this.mMin = min;
    invalidate();
    return this;
  }

  /**
   * 获取最小进度
   */
  public float getMin() {
    return mMin;
  }

  /**
   * 设置进度最大值
   */
  public CustomSeekBar setMax(float max) {
    this.mMax = max;
    invalidate();
    return this;
  }

  /**
   * 获取最大进度
   */
  public float getMax() {
    return mMax;
  }

  public interface OnProgressUpdateListener {
    /**
     * 进度更新时回调
     *
     * @param progress 当前进度
     */
    void onProgressUpdate(int progress);
  }

  public void setOnProgressUpdateListener(
      OnProgressUpdateListener onProgressUpdateListener) {
    mOnProgressUpdateListener = onProgressUpdateListener;
  }

  /**
   * 获取当前进度值比值
   */
  private float getProgressRatio() {
    return (mProgress / (mMax * 1.0f));
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
