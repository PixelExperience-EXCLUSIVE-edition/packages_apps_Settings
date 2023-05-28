/*
 * Copyright (C) 2023 the risingOS android project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.sound;

import android.os.AsyncTask;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;

import com.android.settingslib.core.AbstractPreferenceController;

import com.android.settings.custom.preference.CustomSeekBarPreference;

public class HapticsPreferenceFragmentController extends AbstractPreferenceController
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY = "haptics_settings";
    private static final String KEY_BRIGHTNESS_SLIDER_HAPTICS_INTENSITY = "brightness_slider_haptics_intensity";
    private static final String KEY_EDGE_SCROLLING_HAPTICS_INTENSITY = "edge_scrolling_haptics_intensity";
    private static final String KEY_QS_TILE_HAPTICS_INTENSITY = "qs_panel_tile_haptic";
    private static final String KEY_VOLUME_SLIDER_HAPTICS_INTENSITY = "volume_slider_haptics_intensity";

    private CustomSeekBarPreference mBrightnessIntensity;
    private CustomSeekBarPreference mEdgeScrollingIntensity;
    private CustomSeekBarPreference mQsTileIntensity;
    private CustomSeekBarPreference mVolumeSliderIntensity;

    public HapticsPreferenceFragmentController(Context context) {
        super(context);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY;
    }
    
    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        mBrightnessIntensity = (CustomSeekBarPreference) screen.findPreference(KEY_BRIGHTNESS_SLIDER_HAPTICS_INTENSITY);
        mEdgeScrollingIntensity = (CustomSeekBarPreference) screen.findPreference(KEY_EDGE_SCROLLING_HAPTICS_INTENSITY);
        mQsTileIntensity = (CustomSeekBarPreference) screen.findPreference(KEY_QS_TILE_HAPTICS_INTENSITY);
        mVolumeSliderIntensity = (CustomSeekBarPreference) screen.findPreference(KEY_VOLUME_SLIDER_HAPTICS_INTENSITY);
        updateSettings();
    }

   private void updateSettings() {

        int brightnessIntensity = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.BRIGHTNESS_SLIDER_HAPTICS_INTENSITY, 1);
        mBrightnessIntensity.setValue(brightnessIntensity);

        int edgeScrollingIntensity = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.EDGE_SCROLLING_HAPTICS_INTENSITY, 3);
        mEdgeScrollingIntensity.setValue(edgeScrollingIntensity);

        int volumeSliderIntensity = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.VOLUME_SLIDER_HAPTICS_INTENSITY, 1);
        mVolumeSliderIntensity.setValue(volumeSliderIntensity);
        
        int qsTileHapticsIntensity = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.QS_PANEL_TILE_HAPTIC, 0);
        mQsTileIntensity.setValue(qsTileHapticsIntensity);

        mBrightnessIntensity.setOnPreferenceChangeListener(this);
        mEdgeScrollingIntensity.setOnPreferenceChangeListener(this);
        mQsTileIntensity.setOnPreferenceChangeListener(this);
        mVolumeSliderIntensity.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mBrightnessIntensity) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.BRIGHTNESS_SLIDER_HAPTICS_INTENSITY, (Integer) newValue);
            mBrightnessIntensity.setValue((Integer) newValue);
            vibrate((Integer) newValue);
            return true;
        } else if (preference == mEdgeScrollingIntensity) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.EDGE_SCROLLING_HAPTICS_INTENSITY, (Integer) newValue);
            mEdgeScrollingIntensity.setValue((Integer) newValue);
            vibrate((Integer) newValue);
            return true;
        } else if (preference == mQsTileIntensity) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.QS_PANEL_TILE_HAPTIC, (Integer) newValue);
            mQsTileIntensity.setValue((Integer) newValue);
            vibrate((Integer) newValue);
            return true;
        } else if (preference == mVolumeSliderIntensity) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.VOLUME_SLIDER_HAPTICS_INTENSITY, (Integer) newValue);
            mVolumeSliderIntensity.setValue((Integer) newValue);
            vibrate((Integer) newValue);
            return true;
        }
        return false;
    }

    private void vibrate(int intensityLevel) {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || intensityLevel == 0) {
            return;
        }
            VibrationEffect effect;
            switch (intensityLevel) {
                case 1:
                    effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TEXTURE_TICK);
                    break;
                case 2:
                    effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK);
                    break;
                case 3:
                    effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                    break;
                case 4:
                    effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK);
                    break;
                case 5:
                    effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK);
                    break;
                default:
                    effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                    break;
            }
            AsyncTask.execute(() -> vibrator.vibrate(effect));
    }

}
