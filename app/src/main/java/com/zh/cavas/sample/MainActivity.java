package com.zh.cavas.sample;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
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

public class MainActivity extends BaseActivity {
    private Toolbar vToolbar;
    private MoreActionView vMoreActionView;
    private DownloadProgressView vDownloadProgressView;
    private BackArrowView vBackArrowView;
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
        //自定义SeekBar
        vCustomSeekBar.setOnProgressUpdateListener(new CustomSeekBar.OnProgressUpdateListener() {
            @Override public void onProgressUpdate(int progress) {
                vDownloadProgressView.setProgress(progress);
            }
        });
        vVivoSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        vVivoSeekBarGray.setOnSeekBarChangeListener(seekBarChangeListener);
        vDownloadProgressView.setOnProgressUpdateListener(new DownloadProgressView.OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(int progress) {
                vVivoSeekBar.setProgress(progress);
                vVivoSeekBarGray.setProgress(progress);
            }
        });
        vDownloadProgressView.setProgress(0);
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
     * 隐藏虚拟键
     */
    private void hideNavigationBar() {
        //隐藏虚拟按键
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}