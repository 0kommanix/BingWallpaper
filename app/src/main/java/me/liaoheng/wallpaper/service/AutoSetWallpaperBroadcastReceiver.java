package me.liaoheng.wallpaper.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.NetworkUtils;

import org.joda.time.LocalTime;

import me.liaoheng.wallpaper.util.BingWallpaperAlarmManager;
import me.liaoheng.wallpaper.util.LogDebugFileUtils;
import me.liaoheng.wallpaper.util.BingWallpaperUtils;
import me.liaoheng.wallpaper.widget.AppWidget_5x1;
import me.liaoheng.wallpaper.widget.AppWidget_5x2;

/**
 * 接收定时闹钟与开机自启事件
 * @author liaoheng
 * @version 2016-09-19 15:49
 */
public class AutoSetWallpaperBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        L.Log.d("AutoSetWallpaperBroadcastReceiver", "action : %s", intent.getAction());
        if (BingWallpaperUtils.isEnableLog(context)) {
            LogDebugFileUtils.get()
                    .i("AutoSetWallpaperBroadcastReceiver", "action  : %s", intent.getAction());
        }
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AppWidget_5x1.start(context,null);
            AppWidget_5x2.start(context,null);
            LocalTime dayUpdateTime = BingWallpaperUtils.getDayUpdateTime(context);
            if (dayUpdateTime == null) {
                return;
            }
            BingWallpaperAlarmManager.enabled(context, dayUpdateTime);
            return;
        }

        if (BingWallpaperUtils.isConnectedOrConnecting(context)) {
            if (BingWallpaperUtils.getOnlyWifi(context)) {
                if (!NetworkUtils.isWifiConnected(context)) {
                    return;
                }
            }
            BingWallpaperIntentService.start(context,BingWallpaperUtils.getAutoModeValue(context));
        }
    }
}
