package com.zh.cavas.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.zh.cavas.sample.widget.BackArrowView;
import com.zh.cavas.sample.widget.DownloadProgressView;
import com.zh.cavas.sample.widget.MoreActionView;

public class MainActivity extends BaseActivity {
    private Toolbar vToolbar;
    private MoreActionView vMoreActionView;
    private DownloadProgressView vDownloadProgressView;
    private BackArrowView vBackArrowView;
    private SeekBar vVivoSeekBar;
    private SeekBar vViveoSeekBarGray;
    private TextView vProgressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View layout = findViewById(android.R.id.content);
        findView(layout);
        bindView();
    }

    private void findView(View view) {
        vToolbar = view.findViewById(R.id.toolbar);
        vMoreActionView = view.findViewById(R.id.more_action);
        vBackArrowView = view.findViewById(R.id.back_arrow);
        vDownloadProgressView = view.findViewById(R.id.download_progress);
        vVivoSeekBar = view.findViewById(R.id.vivo_seek_bar);
        vViveoSeekBarGray = view.findViewById(R.id.vivo_seek_bar_gray);
        vProgressIndicator = view.findViewById(R.id.progress_indicator);
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
        vVivoSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        vViveoSeekBarGray.setOnSeekBarChangeListener(seekBarChangeListener);
        vDownloadProgressView.setOnProgressUpdateListener(new DownloadProgressView.OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(int progress) {
                vVivoSeekBar.setProgress(progress);
                vViveoSeekBarGray.setProgress(progress);
                vProgressIndicator.setText("当前进度：" + progress);
            }
        });
        vDownloadProgressView.setProgress(0);
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
}