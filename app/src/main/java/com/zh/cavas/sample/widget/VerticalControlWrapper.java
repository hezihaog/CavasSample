package com.zh.cavas.sample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zh.cavas.sample.R;

/**
 * 垂直控制包裹View，包含进度控制和显示文本，因为进度需求有0 ~ 1，0.5 ~ 1.5，-3 ~ 3，这3种，而VerticalSeekBar只是一个正数区间的控制
 * 需要支持以上3种区间，就要做特殊处理，进度值需要做转换，因不想特殊的进度需求，改动到这个View，所以才有这个包裹类，进行进度换算的控制
 */
public class VerticalControlWrapper extends FrameLayout {
    /**
     * 最大值
     */
    private float mMax = 1.5f;
    /**
     * 零值
     */
    private float mZero = 1f;
    /**
     * 当前比值
     */
    private float mCurrentPercent;
    /**
     * 垂直进度条
     */
    private VerticalSeekBar vSeekBar;
    /**
     * 进度文本
     */
    private TextView vProgressText;
    /**
     * 进度回调
     */
    private OnProgressChangeListener mOnProgressChangeListener;
    /**
     * 进度渲染文本
     */
    private ProgressTextRender mProgressTextRender;

    public VerticalControlWrapper(@NonNull Context context) {
        this(context, null);
    }

    public VerticalControlWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalControlWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_vertical_control_wrapper, this);
        findView(this);
        bindView();
    }

    private void findView(View view) {
        vSeekBar = view.findViewById(R.id.seek_bar);
        vProgressText = view.findViewById(R.id.progress_text);
    }

    private void bindView() {
        vSeekBar.setMax(1000);
        vSeekBar.setOnProgressUpdateListener(new VerticalSeekBar.SimpleProgressUpdateListener() {
            @Override
            public void onProgressUpdate(VerticalSeekBar seekBar, float progress, boolean fromUser) {
                super.onProgressUpdate(seekBar, progress, fromUser);
                //同步进度百分比
                mCurrentPercent = (progress - 500) / 500f;
                setCurrentPercent((progress - 500) / 500f, fromUser);
                //移动到一半的进度
                if (progress > 497 && progress < 503 && fromUser) {
                    if (mOnProgressChangeListener != null) {
                        mOnProgressChangeListener.onProgressMiddle(fromUser);
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {
                super.onStopTrackingTouch(seekBar);
                if (mOnProgressChangeListener != null) {
                    mOnProgressChangeListener.onStopTrackingTouch();
                }
            }
        });
    }

    /**
     * 设置进度值
     */
    public void setCurrentProgress(float progress) {
        setCurrentPercent((progress - mZero) / (mMax - mZero), false);
    }

    /**
     * 设置当前百分比值
     *
     * @param currentPercent 百分比值
     */
    public void setCurrentPercent(float currentPercent) {
        setCurrentPercent(currentPercent, false);
    }

    /**
     * 设置当前百分比值
     *
     * @param currentPercent 百分比值
     * @param fromUser       是否用户拖拽触发
     */
    public void setCurrentPercent(float currentPercent, boolean fromUser) {
        this.mCurrentPercent = currentPercent;
        //设置进度
        vSeekBar.setProgress(Math.round(currentPercent * 500f + 500));
        //设置进度文本
        if (mProgressTextRender != null) {
            float currentValue = (mMax - mZero) * currentPercent + mZero;
            String text = mProgressTextRender.onRenderProgressText(currentValue);
            vProgressText.setText(text);
        } else {
            vProgressText.setText("");
        }
        //回调外部
        if (mOnProgressChangeListener != null) {
            mOnProgressChangeListener.onProgress((mMax - mZero) * currentPercent + mZero, fromUser);
        }
    }

    /**
     * 获取当前进度百分比值
     */
    public float getCurrentPercent() {
        return mCurrentPercent;
    }

    /**
     * 设置零值
     */
    public void setZero(float zero) {
        this.mZero = zero;
    }

    /**
     * 设置最大值
     */
    public void setMax(float max) {
        this.mMax = max;
    }

    /**
     * 设置进度背景颜色
     */
    public void setBgColor(int bgColor) {
        vSeekBar.setBgColor(bgColor);
    }

    /**
     * 设置已有进度的背景颜色
     */
    public void setProgressBgColor(int progressBgColor) {
        vSeekBar.setProgressBgColor(progressBgColor);
    }

    public abstract static class OnProgressChangeListener {
        /**
         * 进度更新时回调
         *
         * @param progress 进度值
         * @param fromUser 是否是因为用户拽托发生改变
         */
        public void onProgress(float progress, boolean fromUser) {
        }

        /**
         * 进度更新到一半
         */
        public void onProgressMiddle(boolean fromUser) {
        }

        /**
         * 松开进度条
         */
        public void onStopTrackingTouch() {
        }
    }

    public void setOnProgressChangeListener(OnProgressChangeListener onProgressChangeListener) {
        this.mOnProgressChangeListener = onProgressChangeListener;
    }

    public interface ProgressTextRender {
        /**
         * 进度渲染回调
         *
         * @param currentValue 当前值
         * @return 要渲染的文本
         */
        String onRenderProgressText(float currentValue);
    }

    public void setProgressTextRender(ProgressTextRender progressTextRender) {
        mProgressTextRender = progressTextRender;
    }
}