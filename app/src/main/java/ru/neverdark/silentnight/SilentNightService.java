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

import java.util.Calendar;
import ru.neverdark.log.Log;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

/**
 * Service automatically turn on and off the device sound
 */
public class SilentNightService extends Service {

    private AlarmManager mAlarmManager;
    private boolean mIsServiceEnabled;
    private PendingIntent mPendingIntentDisabler;
    private PendingIntent mPendingIntentEnabler;
    private Calendar mSilentModeEndAt;
    private Calendar mSilentModeStartAt;

    /**
     * Constructor
     */
    public SilentNightService() {
        Log.message("Enter");
        mSilentModeStartAt = Calendar.getInstance();
        mSilentModeEndAt = Calendar.getInstance();
    }

    /**
     * Loads the preferences of the stored service control activity
     */
    private void loadPreferences() {
        Log.message("Enter");
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        int startTime = sp.getInt(Constant.PREF_SILENT_MODE_START_AT, 0);
        int entTime = sp.getInt(Constant.PREF_SILENT_MODE_END_AT, 0);
        
        if (startTime == 0) {
            mSilentModeStartAt.setTimeInMillis(0);
        } else {
            mSilentModeStartAt.set(Calendar.HOUR_OF_DAY, TimePreference.MASK & startTime);
            mSilentModeStartAt.set(Calendar.MINUTE, TimePreference.MASK & startTime >>> 8);
        }
        
        if (entTime == 0) {
            mSilentModeEndAt.setTimeInMillis(0);
        } else {
            mSilentModeEndAt.set(Calendar.HOUR_OF_DAY, TimePreference.MASK & entTime);
            mSilentModeEndAt.set(Calendar.MINUTE, TimePreference.MASK & entTime >>> 8);
        }

        mIsServiceEnabled = sp.getBoolean(Constant.PREF_IS_SERVICE_ENABLED,
                false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        Log.message("Enter");
        loadPreferences();
        prepareAlarms();
        /* Starts scheduler if service enabled or stopping service in other case */
        if (mIsServiceEnabled) {
            startScheduler();
        } else {
            stopScheduler();
        }
        
        stopSelf();
    }

    /**
     * Plans alarm for run in future
     * @param pengingIntent PendingIntent for run special service
     * @param calendar Calendar contains timee for run
     */
    private void planAlarm(PendingIntent pengingIntent, Calendar calendar) {
        Log.message("Enter");
        Calendar calendarNow = Calendar.getInstance();
        Calendar calendarPlan = Calendar.getInstance();
        
        calendarPlan.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        calendarPlan.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        calendarPlan.set(Calendar.SECOND, 0);
        
        if (calendarPlan.before(calendarNow)) {
            calendarPlan.add(Calendar.DATE, 1);
        }
        
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendarPlan.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pengingIntent);
    }
    
    /**
     * Prepares alarm manager for use
     */
    private void prepareAlarms() {
        Log.message("Enter");
        mPendingIntentEnabler = PendingIntent.getService(this,
                0, new Intent(this, EnableSoundService.class),
                0);

        mPendingIntentDisabler = PendingIntent.getService(this,
                0, new Intent(this, DisableSoundService.class),
                0);

        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }
    
    /**
     * Starts scheduler for automatically turn on and off device sound
     */
    private void startScheduler() {
        Log.message("Enter");
        /* Schedule task for automatically turn off sound */
        planAlarm(mPendingIntentDisabler, mSilentModeStartAt);
        
        /* Schedule task for automatically turn on sound */
        planAlarm(mPendingIntentEnabler, mSilentModeEndAt);
    }
    
    /**
     * Stops any schedule
     */
    private void stopScheduler() {
        Log.message("Enter");
        mAlarmManager.cancel(mPendingIntentEnabler);
        mAlarmManager.cancel(mPendingIntentDisabler);
    }
}
