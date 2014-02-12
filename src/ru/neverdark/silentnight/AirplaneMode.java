/*******************************************************************************
 * Copyright (C) 2014 Grégory Soutadé.
 * Copyright (C) 2013-2014 Artem Yankovskiy (artemyankovskiy@gmail.com).
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package ru.neverdark.silentnight;

import ru.neverdark.log.Log;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import eu.chainfire.libsuperuser.Shell;

/**
 * Turns on/off airplane mode
 */
public class AirplaneMode {

    private static Context mContext;
    private boolean mSuEnabled;

    public AirplaneMode(Context context) {
        Log.message("Enter");
        mContext = context;
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        mSuEnabled = sp.getBoolean(Constant.PREF_SU_MODE, false);
    }

    /**
     * Enables "Airplane mode"
     */
    public void enable() {
        Log.message("Enter");
        updateSystemSettings(true);
    }

    /**
     * Disables "Airplane mode"
     */
    public void disable() {
        Log.message("Enter");
        updateSystemSettings(false);
    }

    /**
     * Changes airplane mode to new state
     * 
     * @return
     */
    private boolean updateSystemSettings(boolean isEnabled) {
        Log.message("Enter");
        // Toggle airplane mode.
        setSettings(isEnabled ? 1 : 0);
        if (!mSuEnabled) {
            // Post an intent to reload.
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", isEnabled);
            mContext.sendBroadcast(intent);
        } else {
            // http://stackoverflow.com/questions/15861046/how-to-toggle-airplane-mode-on-android-4-2-using-root
            if (isEnabled)
                Shell.SU.run("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true");
            else
                Shell.SU.run("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false");
        }
        return true;
    }

    /**
     * Checks whether the "airplane mode"
     * 
     * @return true for "Airplane mode" enabled, false for disabled
     */
    @SuppressLint("NewApi")
    public boolean isEnabled() {
        Log.message("Enter");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(mContext.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(mContext.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    /**
     * Sets new "airplane mode"
     * 
     * @param value
     *            new "airplane mode". 0 - disable, 1 - enable
     */
    @SuppressLint("NewApi")
    private void setSettings(int value) {
        Log.message("Enter");
        if (!mSuEnabled) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, value);
            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Settings.Global.putInt(mContext.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, value);
            }
        } else {
            // http://stackoverflow.com/questions/15861046/how-to-toggle-airplane-mode-on-android-4-2-using-root
            if (value != 0)
                Shell.SU.run("settings put global airplane_mode_on 1");
            else
                Shell.SU.run("settings put global airplane_mode_on 0");
        }
    }
}
