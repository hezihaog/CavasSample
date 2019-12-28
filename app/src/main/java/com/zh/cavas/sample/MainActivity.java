package com.zh.cavas.sample;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zh.cavas.sample.widget.BackArrowView;
import com.zh.cavas.sample.widget.RingLoadingView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ImageView loadingImage = findViewById(R.id.loading);
        final RingLoadingView loadingShape = findViewById(R.id.loading_shape);
        BackArrowView backArrowView = findViewById(R.id.back_arrow);
        backArrowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.app_loading_anim);
//        loadingImage.startAnimation(rotateAnimation);
        ValueAnimator rotateAnimator = ValueAnimator.ofFloat(0, 360);
        rotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float cValue = (Float) animation.getAnimatedValue();
                loadingImage.setRotation(cValue);
            }
        });
        rotateAnimator.setDuration(800);
        rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        rotateAnimator.start();
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}