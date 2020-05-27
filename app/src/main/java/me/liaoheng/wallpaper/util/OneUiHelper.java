package me.liaoheng.wallpaper.util;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;

/**
 * @author liaoheng
 * @version 2020-05-27 11:53
 */
public class OneUiHelper {

    public static void setWallpaper(Context context, @Constants.setWallpaperMode int mode, File homeWallpaper,
            File lockWallpaper)
            throws IOException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            BingWallpaperUtils.setWallpaper(context, homeWallpaper);
        } else {
            if (mode == Constants.EXTRA_SET_WALLPAPER_MODE_HOME) {
                homeSetWallpaper(context, homeWallpaper);
            } else if (mode == Constants.EXTRA_SET_WALLPAPER_MODE_LOCK) {
                BingWallpaperUtils.setLockScreenWallpaper(context, lockWallpaper);
            } else {
                homeSetWallpaper(context, homeWallpaper);
                BingWallpaperUtils.setLockScreenWallpaper(context, lockWallpaper);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void homeSetWallpaper(Context context, File wallpaper) throws IOException {
        wallpaper = UIHelper.cropWallpaper(context, wallpaper,false);
        BingWallpaperUtils.setHomeScreenWallpaper(context, wallpaper);
    }
}
