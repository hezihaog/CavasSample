package com.zh.cavas.sample.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.zh.cavas.sample.Constant;
import com.zh.cavas.sample.util.AccessibilityHelper;
import com.zh.cavas.sample.util.AppBroadcastManager;

/**
 * <b>Package:</b> com.zh.cavas.sample.service <br>
 * <b>Create Date:</b> 2020/3/5  9:29 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 核心辅助服务 <br>
 */
public class CoreAccessibilityService extends AccessibilityService {
    private BroadcastReceiver mActionReceiver;

    /**
     * 开启服务
     */
    public static void startSelf(Context context) {
        Intent intent = new Intent(context, CoreAccessibilityService.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final AccessibilityHelper accessibilityHelper = new AccessibilityHelper(this);
        mActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == null) {
                    return;
                }
                //检查辅助服务是否开启，如果未开启，则跳转到设置页面
                if (!accessibilityHelper.checkAccessibilityIsOpen(getApplicationContext())) {
                    return;
                }
                switch (intent.getAction()) {
                    case Constant.Action.ACTION_DO_BACK:
                        accessibilityHelper.doBack();
                        break;
                    case Constant.Action.ACTION_PULL_DOWN_NOTIFICATION_BAR:
                        accessibilityHelper.doPullDownNotificationBar();
                        break;
                    case Constant.Action.ACTION_DO_GO_HOME:
                        accessibilityHelper.doGoHome();
                        break;
                    case Constant.Action.ACTION_DO_GO_TASK:
                        accessibilityHelper.doGoTask();
                        break;
                    default:
                        break;
                }
            }
        };
        //注册广播
        AppBroadcastManager.registerReceiver(this,
                mActionReceiver,
                Constant.Action.ACTION_DO_BACK,
                Constant.Action.ACTION_PULL_DOWN_NOTIFICATION_BAR,
                Constant.Action.ACTION_DO_GO_HOME,
                Constant.Action.ACTION_DO_GO_TASK);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mActionReceiver != null) {
            AppBroadcastManager.unregisterReceiver(this.getApplicationContext(), mActionReceiver);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }
}