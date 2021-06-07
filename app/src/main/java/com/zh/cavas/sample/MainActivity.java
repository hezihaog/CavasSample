package com.zh.cavas.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zh.cavas.sample.service.CoreAccessibilityService;
import com.zh.cavas.sample.util.AppBroadcastManager;
import com.zh.cavas.sample.widget.BackArrowView;
import com.zh.cavas.sample.widget.CustomSeekBar;
import com.zh.cavas.sample.widget.DownloadProgressView;
import com.zh.cavas.sample.widget.HookCheckBox;
import com.zh.cavas.sample.widget.MoreActionView;
import com.zh.cavas.sample.widget.NavigationBarIconView;
import com.zh.cavas.sample.widget.VerticalControlWrapper;
import com.zh.cavas.sample.widget.VerticalSeekBar;

import java.text.DecimalFormat;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar vToolbar;
    private MoreActionView vMoreActionView;
    private DownloadProgressView vDownloadProgressView;
    private BackArrowView vBackArrowView;
    private VerticalSeekBar vVolumeSeekBar;
    private VerticalControlWrapper vSpeedSeekBar;
    private VerticalControlWrapper vPitchSeekBar;
    private Switch vCustomSeekBarSwitch;
    private TextView vIndicator;
    private CustomSeekBar vCustomSeekBar;
    private SeekBar vVivoSeekBar;
    private SeekBar vVivoSeekBarGray;
    private HookCheckBox vHookCheckBox;
    private ImageView vVivoLoadingProgress;
    private ImageView vVivoPolygonLoading;
    private NavigationBarIconView vNavBack;
    private NavigationBarIconView vNavHome;
    private NavigationBarIconView vNavTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View layout = findViewById(android.R.id.content);
        findView(layout);
        bindView();
        //开启辅助服务
        CoreAccessibilityService.startSelf(this.getApplicationContext());
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        //隐藏虚拟键
//        hideNavigationBar();
//    }

    private void findView(View view) {
        vToolbar = view.findViewById(R.id.toolbar);
        vMoreActionView = view.findViewById(R.id.more_action);
        vBackArrowView = view.findViewById(R.id.back_arrow);
        //音量
        vVolumeSeekBar = view.findViewById(R.id.volume_seek_bar);
        //倍速
        vSpeedSeekBar = view.findViewById(R.id.speed_seek_bar);
        //音调
        vPitchSeekBar = view.findViewById(R.id.pitch_seek_bar);
        vCustomSeekBarSwitch = view.findViewById(R.id.custom_seek_bar_switch);
        vIndicator = view.findViewById(R.id.indicator);
        vCustomSeekBar = view.findViewById(R.id.custom_seek_bar);
        vDownloadProgressView = view.findViewById(R.id.download_progress);
        vVivoSeekBar = view.findViewById(R.id.vivo_seek_bar);
        vVivoSeekBarGray = view.findViewById(R.id.vivo_seek_bar_gray);
        vHookCheckBox = view.findViewById(R.id.hook_checkbox);
        vVivoLoadingProgress = view.findViewById(R.id.vivo_loading_progress);
        vVivoPolygonLoading = view.findViewById(R.id.vivo_polygon_loading);
        vNavBack = view.findViewById(R.id.nav_back);
        vNavHome = view.findViewById(R.id.nav_home);
        vNavTask = view.findViewById(R.id.nav_task);
    }

    private void bindView() {
        setSupportActionBar(vToolbar);
        vBackArrowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        vMoreActionView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.openOptionsMenu();
                }
            }
        });
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vDownloadProgressView.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        setupVerticalSeekBar();
        vCustomSeekBarSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //切换为启用样式
                    vCustomSeekBar.setBgColor(Color.parseColor("#F6F7FC"));
                    vCustomSeekBar.setProgressBgColor(Color.parseColor("#161731"));
                    vCustomSeekBar.setThumbColor(Color.parseColor("#161731"));
                } else {
                    //切换为禁用样式
                    vCustomSeekBar.setBgColor(Color.parseColor("#F6F7FC"));
                    vCustomSeekBar.setProgressBgColor(Color.parseColor("#A3A3AD"));
                    vCustomSeekBar.setThumbColor(Color.parseColor("#A3A3AD"));
                }
            }
        });
        //自定义SeekBar
        vCustomSeekBar.setOnProgressUpdateListener(new CustomSeekBar.OnProgressUpdateListener() {
            @Override
            public void onStartTrackingTouch(CustomSeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "按下进度条", Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressUpdate(CustomSeekBar seekBar, int progress, boolean fromUser) {
                vIndicator.setText(progress + "分钟");
                moveIndicator();
            }

            @Override
            public void onStopTrackingTouch(CustomSeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "松开进度条", Toast.LENGTH_SHORT).show();
            }
        });
        vVivoSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        vVivoSeekBarGray.setOnSeekBarChangeListener(seekBarChangeListener);
        vDownloadProgressView.setOnProgressUpdateListener(new DownloadProgressView.OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(int progress) {
                vVivoSeekBar.setProgress(progress);
                vVivoSeekBarGray.setProgress(progress);
                vCustomSeekBar.setProgress(progress);
            }
        });
        vHookCheckBox.setOnCheckChangeListener(new HookCheckBox.OnCheckChangeListener() {
            @Override
            public void onCheckChange(boolean isCheck) {
                String msg;
                if (isCheck) {
                    msg = "开";
                } else {
                    msg = "关";
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        AnimationDrawable animationDrawable = (AnimationDrawable) vVivoLoadingProgress.getBackground();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
        Glide.with(this).load(R.drawable.vivo_polygon_loading).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    //无限循环
                    ((GifDrawable) resource).setLoopCount(GifDrawable.LOOP_FOREVER);
                }
                return false;
            }
        }).into(vVivoPolygonLoading);
        //设置导航栏图标
        setupNavigationIcon();
    }

    /**
     * 垂直进度条
     */
    private void setupVerticalSeekBar() {
        //垂直音量控制条
        vVolumeSeekBar.setOnProgressUpdateListener(new VerticalSeekBar.SimpleProgressUpdateListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressUpdate(VerticalSeekBar seekBar, float progress, boolean fromUser) {
                //设置音量
                Log.d(TAG, "VolumeSeekBar onProgressUpdate => progress = " + progress);
            }
        });
        vVolumeSeekBar.setMin(0f);
        vVolumeSeekBar.setMax(1f);
        vVolumeSeekBar.setProgress(0f);
        //倍数
        vSpeedSeekBar.setOnProgressChangeListener(new VerticalControlWrapper.OnProgressChangeListener() {
            @Override
            public void onProgress(float progress, boolean fromUser) {
                //设置倍数
                float result = floatValueRetain2Location(progress);
                Log.d(TAG, "SpeedSeekBar onProgressUpdate => progress = " + progress + "，result = " + result);
            }
        });
        vSpeedSeekBar.setSuffixText(" X");
        vSpeedSeekBar.setZero(1f);
        vSpeedSeekBar.setMax(1.5f);
        //音调
        vPitchSeekBar.setOnProgressChangeListener(new VerticalControlWrapper.OnProgressChangeListener() {
            @Override
            public void onProgress(float progress, boolean fromUser) {
                //设置音调
                float result = floatValueRetain2Location(progress);
                Log.d(TAG, "PitchSeekBar onProgressUpdate => progress = " + progress + "，result = " + result);
            }
        });
        vPitchSeekBar.setBgColor(Color.parseColor("#EDF0FA"));
        vPitchSeekBar.setProgressBgColor(Color.parseColor("#FFCA72"));
        vPitchSeekBar.setSuffixText(" K");
        vPitchSeekBar.setMax(3f);
        vPitchSeekBar.setZero(0f);
    }

    private void setupNavigationIcon() {
        vNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppBroadcastManager
                        .sendBroadcast(getApplication(), Constant.Action.ACTION_DO_BACK);
            }
        });
        vNavHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppBroadcastManager
                        .sendBroadcast(getApplication(), Constant.Action.ACTION_DO_GO_HOME);
            }
        });
        vNavTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppBroadcastManager
                        .sendBroadcast(getApplication(), Constant.Action.ACTION_DO_GO_TASK);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 移动当前时间的提示
     */
    private void moveIndicator() {
        //进度比值
        float progressRatio = vCustomSeekBar.getProgressRatio();
        //指示器的宽度
        float indicatorWidth = getTextWidth(vIndicator);
        //滑块的宽度
        int thumbWidth = vCustomSeekBar.getThumbRadius();
        //计算公式：总宽度 * 进度百分比 -（指示器宽度 - 滑块宽度）/ 2 - 滑块宽度 * 进度百分比
        float indicatorOffset = vCustomSeekBar.getWidth() * progressRatio
                - (indicatorWidth - thumbWidth) / 2f
                - thumbWidth * progressRatio;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) vIndicator.getLayoutParams();
        layoutParams.leftMargin = Math.round(indicatorOffset) + dip2px(getApplicationContext(), 15f);
        vIndicator.setLayoutParams(layoutParams);
    }

    /**
     * 获取TextView的文本宽度
     */
    public float getTextWidth(TextView textView) {
        TextPaint paint = textView.getPaint();
        return paint.measureText(textView.getText().toString());
    }

    /**
     * Float值保留2位小数
     */
    private float floatValueRetain2Location(float value) {
        DecimalFormat format = new DecimalFormat("0.##");
        String resultValue = format.format(value);
        return Float.parseFloat(resultValue);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}