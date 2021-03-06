package me.liaoheng.wallpaper.util;

import android.content.Context;

import net.grandcentrix.tray.TrayPreferences;
import net.grandcentrix.tray.core.ItemNotFoundException;
import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.core.TrayRuntimeException;
import net.grandcentrix.tray.core.WrongTypeException;

import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author liaoheng
 * @version 2018-01-24 12:05
 */
public class SettingTrayPreferences implements ISettingTrayPreferences {

    private static ISettingTrayPreferences mPreferences;

    public static synchronized ISettingTrayPreferences get(Context context) {
        if (mPreferences == null) {
            try {
                mPreferences = new SettingTrayPreferences(new TrayPreferences(context, context.getPackageName(), 1));
            } catch (TrayRuntimeException e) {
                mPreferences = new TestPreferenceAccessor();
            }
        }
        return mPreferences;
    }

    private TrayPreferences mAccessor;

    private SettingTrayPreferences(TrayPreferences accessor) {
        mAccessor = accessor;
    }

    @Override
    public boolean clear() {
        return mAccessor.clear();
    }

    @Override
    public boolean contains(String key) {
        return mAccessor.contains(key);
    }

    @Override
    public Collection<TrayItem> getAll() {
        return mAccessor.getAll();
    }

    @Override
    public boolean getBoolean(@NonNull String key) throws ItemNotFoundException {
        return mAccessor.getBoolean(key);
    }

    @Override
    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        return mAccessor.getBoolean(key, defaultValue);
    }

    @Override
    public float getFloat(@NonNull String key) throws ItemNotFoundException, WrongTypeException {
        return mAccessor.getFloat(key);
    }

    @Override
    public float getFloat(@NonNull String key, float defaultValue) throws WrongTypeException {
        return mAccessor.getFloat(key, defaultValue);
    }

    @Override
    public int getInt(@NonNull String key) throws ItemNotFoundException, WrongTypeException {
        return mAccessor.getInt(key);
    }

    @Override
    public int getInt(@NonNull String key, int defaultValue) throws WrongTypeException {
        return mAccessor.getInt(key, defaultValue);
    }

    @Override
    public long getLong(@NonNull String key) throws ItemNotFoundException, WrongTypeException {
        return mAccessor.getLong(key);
    }

    @Override
    public long getLong(@NonNull String key, long defaultValue) throws WrongTypeException {
        return mAccessor.getLong(key, defaultValue);
    }

    @Nullable
    @Override
    public TrayItem getPref(@NonNull String key) {
        return mAccessor.getPref(key);
    }

    @Nullable
    @Override
    public String getString(@NonNull String key) throws ItemNotFoundException {
        return mAccessor.getString(key);
    }

    @Nullable
    @Override
    public String getString(@NonNull String key, @Nullable String defaultValue) {
        return mAccessor.getString(key, defaultValue);
    }

    @Override
    public boolean put(@NonNull String key, @Nullable String value) {
        return mAccessor.put(key, value);
    }

    @Override
    public boolean put(@NonNull String key, int value) {
        return mAccessor.put(key, value);
    }

    @Override
    public boolean put(@NonNull String key, float value) {
        return mAccessor.put(key, value);
    }

    @Override
    public boolean put(@NonNull String key, long value) {
        return mAccessor.put(key, value);
    }

    @Override
    public boolean put(@NonNull String key, boolean value) {
        return mAccessor.put(key, value);
    }

    @Override
    public boolean remove(@NonNull String key) {
        return mAccessor.remove(key);
    }

    @Override
    public boolean wipe() {
        return mAccessor.wipe();
    }

    public static class TestPreferenceAccessor implements ISettingTrayPreferences {

        @Override
        public boolean clear() {
            return false;
        }

        @Override
        public boolean contains(String key) {
            return false;
        }

        @Override
        public Collection<TrayItem> getAll() {
            return null;
        }

        @Override
        public boolean getBoolean(@NonNull String key) throws ItemNotFoundException {
            return false;
        }

        @Override
        public boolean getBoolean(@NonNull String key, boolean defaultValue) {
            return false;
        }

        @Override
        public float getFloat(@NonNull String key) throws ItemNotFoundException, WrongTypeException {
            return 0;
        }

        @Override
        public float getFloat(@NonNull String key, float defaultValue) throws WrongTypeException {
            return 0;
        }

        @Override
        public int getInt(@NonNull String key) throws ItemNotFoundException, WrongTypeException {
            return 0;
        }

        @Override
        public int getInt(@NonNull String key, int defaultValue) throws WrongTypeException {
            return 0;
        }

        @Override
        public long getLong(@NonNull String key) throws ItemNotFoundException, WrongTypeException {
            return 0;
        }

        @Override
        public long getLong(@NonNull String key, long defaultValue) throws WrongTypeException {
            return 0;
        }

        @Nullable
        @Override
        public TrayItem getPref(@NonNull String key) {
            return null;
        }

        @Nullable
        @Override
        public String getString(@NonNull String key) throws ItemNotFoundException {
            return null;
        }

        @Nullable
        @Override
        public String getString(@NonNull String key, @Nullable String defaultValue) {
            return null;
        }

        @Override
        public boolean put(@NonNull String key, @Nullable String value) {
            return false;
        }

        @Override
        public boolean put(@NonNull String key, int value) {
            return false;
        }

        @Override
        public boolean put(@NonNull String key, float value) {
            return false;
        }

        @Override
        public boolean put(@NonNull String key, long value) {
            return false;
        }

        @Override
        public boolean put(@NonNull String key, boolean value) {
            return false;
        }

        @Override
        public boolean remove(@NonNull String key) {
            return false;
        }

        @Override
        public boolean wipe() {
            return false;
        }
    }
}
