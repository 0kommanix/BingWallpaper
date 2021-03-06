package me.liaoheng.wallpaper.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.liaoheng.common.util.UIUtils;
import com.github.paolorotolo.appintro.AppIntro;
import me.liaoheng.wallpaper.R;
import me.liaoheng.wallpaper.util.*;
import org.joda.time.LocalTime;

/**
 * @author liaoheng
 * @version 2018-03-05 17:29
 */
public class IntroActivity extends AppIntro {
    private ISettingTrayPreferences mPreferences;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = SettingTrayPreferences.get(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        addSlide(new IntroHintFragment());
        addSlide(new IntroUpdateFragment());
    }

    public static class IntroHintFragment extends Fragment {
        @BindView(R.id.intro_hint_ignore_battery_optimization)
        View ignore;

        @OnClick(R.id.intro_hint_ignore_battery_optimization)
        void ignoreBatteryOptimization() {
            BingWallpaperUtils.showIgnoreBatteryOptimizationSetting(getActivity());
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_intro_hint, container, false);
            ButterKnife.bind(this, view);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                UIUtils.viewVisible(ignore);
            }
            return view;
        }
    }

    public static class IntroUpdateFragment extends Fragment {

        @BindView(R.id.intro_update_select_group)
        RadioGroup mSelectGroup;
        @BindView(R.id.intro_update_select_group_item_timing_time)
        TextView mTimingTime;

        @OnClick(R.id.intro_update_select_group_item_timing)
        void onTiming() {
            mAlertDialog.show();
        }

        int updateFlag;
        LocalTime localTime;
        private AlertDialog mAlertDialog;

        @SuppressLint("SetTextI18n")
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final TimePicker picker = new TimePicker(getContext());
            picker.setIs24HourView(DateFormat.is24HourFormat(getContext()));
            mAlertDialog = new AlertDialog.Builder(getContext())
                    .setCustomTitle(picker)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        localTime = new LocalTime(picker.getCurrentHour(), picker.getCurrentMinute());
                        UIUtils.viewVisible(mTimingTime);
                        mTimingTime.setText(
                                getString(R.string.intro_update_set_timing_time) + localTime.toString("HH:mm"));
                    })
                    .create();
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                @Nullable Bundle savedInstanceState) {
            View contentView = inflater.inflate(R.layout.fragment_intro_update, container, false);
            ButterKnife.bind(this, contentView);
            mSelectGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.intro_update_select_group_item_auto:
                        updateFlag = 1;
                        localTime = null;
                        UIUtils.viewGone(mTimingTime);
                        break;
                    case R.id.intro_update_select_group_item_timing:
                        updateFlag = 2;
                        break;
                    case R.id.intro_update_select_group_item_skip:
                        updateFlag = 0;
                        localTime = null;
                        UIUtils.viewGone(mTimingTime);
                        break;
                }
            });
            return contentView;
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        if (currentFragment instanceof IntroUpdateFragment) {
            IntroUpdateFragment fragment = (IntroUpdateFragment) currentFragment;
            switch (fragment.updateFlag) {
                case 1:
                    if (!BingWallpaperJobManager.enabled(this)) {
                        return;
                    }
                    mSharedPreferences.edit()
                            .putBoolean(SettingsActivity.PREF_SET_WALLPAPER_DAY_FULLY_AUTOMATIC_UPDATE, true)
                            .apply();
                    mPreferences.put(SettingsActivity.PREF_SET_WALLPAPER_DAY_FULLY_AUTOMATIC_UPDATE, true);
                    break;
                case 2:
                    if (fragment.localTime != null) {
                        BingWallpaperAlarmManager
                                .enabled(this, fragment.localTime);
                        mSharedPreferences.edit()
                                .putBoolean(SettingsActivity.PREF_SET_WALLPAPER_DAY_AUTO_UPDATE, true)
                                .apply();
                        mSharedPreferences.edit()
                                .putString(SettingsActivity.PREF_SET_WALLPAPER_DAY_AUTO_UPDATE_TIME,
                                        fragment.localTime.toString())
                                .apply();
                        mPreferences.put(SettingsActivity.PREF_SET_WALLPAPER_DAY_AUTO_UPDATE_TIME,
                                fragment.localTime.toString());
                    }
                    break;
            }
        } else {
            UIUtils.showToast(getApplicationContext(), "Setting invalid");
        }
        onSkipPressed(null);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        TasksUtils.markOne();
        UIUtils.startActivity(this, MainActivity.class);
        finish();
    }
}
