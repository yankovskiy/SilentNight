/*******************************************************************************
 * Copyright (C) 2013 Artem Yankovskiy (artemyankovskiy@gmail.com).
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
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;

/**
 * Disables sound on your device
 */
public class DisableSoundService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.message("Enter");
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        if (sp.getBoolean(Constant.PREF_AIRPLANE_MODE, false)) {
            enableAirplaneMode();
        }

        turnOffSound();
        stopSelf();
    }

    /**
     * Turns off sound for ring and notification
     */
    private void turnOffSound() {
        Log.message("Enter");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    /**
     * Enables "Airplane mode"
     */
    private void enableAirplaneMode() {
        Log.message("Enter");
        AirplaneMode airplaneMode = new AirplaneMode(getApplicationContext());
        if (airplaneMode.isEnabled() == false) {
            airplaneMode.enable();
        }
    }

}
