package me.liaoheng.wallpaper.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.request.target.Target;
import com.github.liaoheng.common.util.Callback;
import com.github.liaoheng.common.util.L;
import com.github.liaoheng.common.util.NetException;
import com.github.liaoheng.common.util.SystemException;
import me.liaoheng.wallpaper.data.BingWallpaperNetworkClient;
import me.liaoheng.wallpaper.model.BingWallpaperImage;
import me.liaoheng.wallpaper.model.BingWallpaperState;
import me.liaoheng.wallpaper.util.*;
import me.liaoheng.wallpaper.widget.AppWidget_5x1;
import me.liaoheng.wallpaper.widget.AppWidget_5x2;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 设置壁纸操作IntentService
 *
 * @author liaoheng
 * @version 2016-9-19 12:48
 */
public class BingWallpaperIntentService extends IntentService {

    private final String TAG = BingWallpaperIntentService.class.getSimpleName();
    public final static String ACTION_GET_WALLPAPER_STATE = "me.liaoheng.wallpaper.BING_WALLPAPER_STATE";
    public final static String EXTRA_GET_WALLPAPER_STATE = "GET_WALLPAPER_STATE";
    public final static String FLAG_SET_WALLPAPER_STATE = "SET_WALLPAPER_STATE";
    /**
     * <p>0. both</p>
     * <p>1. home</p>
     * <p>2. lock</p>
     */
    public final static String EXTRA_SET_WALLPAPER_MODE = "set_wallpaper_mode";
    public final static String EXTRA_SET_WALLPAPER_BACKGROUND = "set_wallpaper_background";
    public final static String EXTRA_SET_WALLPAPER_IMAGE = "set_wallpaper_image";
    private IUIHelper mUiHelper;

    public BingWallpaperIntentService() {
        super("BingWallpaperIntentService");
    }

    /**
     * @param mode 0. both , 1. home , 2. lock
     */
    public static void start(Context context, int mode) {
        start(context, mode, true);
    }

    /**
     * @param mode 0. both , 1. home , 2. lock
     */
    public static void start(Context context, @Constants.setWallpaperMode int mode, boolean background) {
        start(context, null, mode, background);
    }

    /**
     * @param mode 0. both , 1. home , 2. lock
     */
    public static void start(Context context, @Nullable BingWallpaperImage image, @Constants.setWallpaperMode int mode,
            boolean background) {
        Intent intent = new Intent(context, BingWallpaperIntentService.class);
        intent.putExtra(EXTRA_SET_WALLPAPER_MODE, mode);
        intent.putExtra(EXTRA_SET_WALLPAPER_BACKGROUND, background);
        intent.putExtra(EXTRA_SET_WALLPAPER_IMAGE, image);
        ContextCompat.startForegroundService(context, intent);
    }

    @Override
    public void onCreate() {
        mUiHelper = new UIHelper();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mUiHelper = null;
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        NotificationUtils.showStartNotification(this);
        @Constants.setWallpaperMode int setWallpaperType = intent.getIntExtra(EXTRA_SET_WALLPAPER_MODE, 0);
        final boolean isBackground = intent.getBooleanExtra(EXTRA_SET_WALLPAPER_BACKGROUND, false);
        BingWallpaperImage bingWallpaperImage = intent.getParcelableExtra(EXTRA_SET_WALLPAPER_IMAGE);
        L.alog().d(TAG, " setWallpaperType : " + setWallpaperType);

        if (BingWallpaperUtils.isEnableLogProvider(getApplicationContext())) {
            LogDebugFileUtils.get().i(TAG, "Starting " + setWallpaperType);
        }

        sendSetWallpaperBroadcast(BingWallpaperState.BEGIN);

        Callback<BingWallpaperImage> callback = new Callback.EmptyCallback<BingWallpaperImage>() {
            @Override
            public void onSuccess(BingWallpaperImage bingWallpaperImage) {
                success(isBackground, bingWallpaperImage);
            }

            @Override
            public void onError(Throwable e) {
                failure(e);
            }
        };
        String imageUrl;
        if (bingWallpaperImage == null) {
            if (BingWallpaperUtils.isPixabaySupport(getApplicationContext())) {
                try {
                    bingWallpaperImage = BingWallpaperNetworkClient.getPixabaysExecute();
                    imageUrl = bingWallpaperImage.getUrl();
                } catch (NetException e) {
                    callback.onError(e);
                    return;
                }
            } else {
                try {
                    String locale = BingWallpaperUtils.getAutoLocale(getApplicationContext());
                    String url = BingWallpaperUtils.getUrl(getApplicationContext());
                    bingWallpaperImage = BingWallpaperNetworkClient.getBingWallpaperSingleCall(url, locale);
                    imageUrl = BingWallpaperUtils.getResolutionImageUrl(getApplicationContext(),
                            bingWallpaperImage);
                } catch (NetException e) {
                    callback.onError(e);
                    return;
                }
            }
        } else {
            imageUrl = bingWallpaperImage.getImageUrl();
        }

        if (BingWallpaperUtils.isEnableLogProvider(getApplicationContext())) {
            LogDebugFileUtils.get().i(TAG, "imageUrl : %s", imageUrl);
        }

        try {
            downloadAndSetWallpaper(imageUrl, setWallpaperType);
            callback.onSuccess(bingWallpaperImage);
            callback.onFinish();
        } catch (Exception e) {
            callback.onError(new SystemException(e));
        }
    }

    private void failure(Throwable throwable) {
        L.alog().e(TAG, throwable, "Failure");
        if (BingWallpaperUtils.isEnableLogProvider(getApplicationContext())) {
            LogDebugFileUtils.get().e(TAG, throwable, "Failure");
        }
        sendSetWallpaperBroadcast(BingWallpaperState.FAIL);
        CrashReportHandle.collectException(getApplicationContext(), TAG, throwable);

        NotificationUtils.showFailureNotification(getApplicationContext());
    }

    private void success(boolean isBackground, BingWallpaperImage bingWallpaperImage) {
        L.alog().i(TAG, "Complete");
        if (BingWallpaperUtils.isEnableLogProvider(getApplicationContext())) {
            LogDebugFileUtils.get().i(TAG, "Complete");
        }

        AppWidget_5x2.start(this, bingWallpaperImage);
        AppWidget_5x1.start(this, bingWallpaperImage);
        sendSetWallpaperBroadcast(BingWallpaperState.SUCCESS);
        if (isBackground) {

            if (BingWallpaperUtils.isAutomaticUpdateNotification(getApplicationContext())) {
                NotificationUtils.showSuccessNotification(getApplicationContext(), bingWallpaperImage.getCopyright());
            }

            //标记成功，每天只在后台执行一次
            if (TasksUtils.isToDaysDoProvider(getApplicationContext(), 1, FLAG_SET_WALLPAPER_STATE)) {
                L.alog().i(TAG, "Today markDone");
                if (BingWallpaperUtils.isEnableLogProvider(getApplicationContext())) {
                    LogDebugFileUtils.get().i(TAG, "Today markDone");
                }
                TasksUtils.markDoneProvider(getApplicationContext(), FLAG_SET_WALLPAPER_STATE);
            }
        }
    }

    private void downloadAndSetWallpaper(String url, @Constants.setWallpaperMode int setWallpaperType)
            throws Exception {
        L.alog().i(TAG, "wallpaper image url: " + url);
        File wallpaper = GlideApp.with(getApplicationContext())
                .asFile()
                .load(url)
                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get(2, TimeUnit.MINUTES);

        if (wallpaper == null || !wallpaper.exists()) {
            throw new IOException("download wallpaper failure");
        }

        mUiHelper.setWallpaper(getApplicationContext(), setWallpaperType, wallpaper);

        L.alog().i(TAG, "setBingWallpaper Success");
        if (BingWallpaperUtils.isEnableLogProvider(getApplicationContext())) {
            LogDebugFileUtils.get().i(TAG, "setBingWallpaper Success");
        }
    }

    private void sendSetWallpaperBroadcast(BingWallpaperState state) {
        Intent intent = new Intent(ACTION_GET_WALLPAPER_STATE);
        intent.putExtra(EXTRA_GET_WALLPAPER_STATE, state.getState());
        sendBroadcast(intent);
    }
}
