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
        Log.message("SilentNightService");
        mSilentModeStartAt = Calendar.getInstance();
        mSilentModeEndAt = Calendar.getInstance();
    }

    /**
     * Loads the preferences of the stored service control activity
     */
    private void loadPreferences() {
        Log.message("loadPreferences");
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        mSilentModeStartAt.setTimeInMillis(sp.getLong(
                Constant.PREF_SILENT_MODE_START_AT, 0));
        mSilentModeEndAt.setTimeInMillis(sp.getLong(
                Constant.PREF_SILENT_MODE_END_AT, 0));
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
        Log.message("onCreate");
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
        Log.message("planAlarm");
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
        Log.message("prepareAlarms");
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
        Log.message("startScheduler");
        /* Schedule task for automatically turn off sound */
        planAlarm(mPendingIntentDisabler, mSilentModeStartAt);
        
        /* Schedule task for automatically turn on sound */
        planAlarm(mPendingIntentEnabler, mSilentModeEndAt);
    }
    
    /**
     * Stops any schedule
     */
    private void stopScheduler() {
        Log.message("stopScheduler");
        mAlarmManager.cancel(mPendingIntentEnabler);
        mAlarmManager.cancel(mPendingIntentDisabler);
    }
}
