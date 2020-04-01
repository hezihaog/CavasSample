package com.zh.cavas.sample.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


/**
 * <b>Package:</b> com.zh.cavas.sample.util <br>
 * <b>Create Date:</b> 2020/3/5  9:35 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class AppBroadcastManager {
    private AppBroadcastManager() {
    }

    public static void sendBroadcast(Context context, String action) {
        sendBroadcast(context, action, null);
    }

    /**
     * 发送广播
     */
    public static void sendBroadcast(Context context, String action, Bundle args) {
        Intent intent = new Intent(action);
        if (args != null) {
            intent.putExtras(args);
        }
        context.sendBroadcast(intent);
    }

    /**
     * 注册
     */
    public static void registerReceiver(Context context, BroadcastReceiver receiver, IntentFilter filter) {
        context.registerReceiver(receiver, filter);
    }

    public static void registerReceiver(Context context, BroadcastReceiver receiver, String... actions) {
        IntentFilter intentFilter = new IntentFilter();
        for (String action : actions) {
            intentFilter.addAction(action);
        }
        registerReceiver(context, receiver, intentFilter);
    }

    public static void registerReceiver(Context context, BroadcastReceiver receiver, String action) {
        registerReceiver(context, receiver, new IntentFilter(action));
    }

    /**
     * 注销
     */
    public static void unregisterReceiver(Context context, BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }
}