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

    private boolean mIsServiceEnabled;
    private Calendar mSilentModeEndAt;
    private Calendar mSilentModeStartAt;
    private PendingIntent pendingIntentEnabler;
    private AlarmManager alarmManagerEnabler;
    private PendingIntent pendingIntentDisabler;
    private AlarmManager alarmManagerDisabler;

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
     * Prepares alarm manager for use
     */
    private void prepareAlarms() {
        Log.message("prepareAlarms");
        //Context context = getApplicationContext();
        pendingIntentEnabler = PendingIntent.getService(this,
                0, new Intent(this, EnableSoundService.class),
                0);
        alarmManagerEnabler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        pendingIntentDisabler = PendingIntent.getService(this,
                0, new Intent(this, DisableSoundService.class),
                0);
        //        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManagerDisabler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }
    
    /**
     * Starts scheduler for automatically turn on and off device sound
     */
    private void startScheduler() {
        Log.message("startScheduler");
        
        Calendar cal1 = Calendar.getInstance();
        /* Schedule task for automatically turn off sound */
        cal1.set(Calendar.HOUR_OF_DAY, mSilentModeStartAt.get(Calendar.HOUR_OF_DAY));
        cal1.set(Calendar.MINUTE, mSilentModeStartAt.get(Calendar.MINUTE));
        cal1.set(Calendar.SECOND, 0);
        Log.variable("HOUR", String.valueOf(cal1.get(Calendar.HOUR_OF_DAY)));
        Log.variable("MINUTE", String.valueOf(cal1.get(Calendar.MINUTE)));
        Log.variable("SECOND", String.valueOf(cal1.get(Calendar.SECOND)));
        
        alarmManagerDisabler.setRepeating(AlarmManager.RTC_WAKEUP,
                cal1.getTimeInMillis(), 
                AlarmManager.INTERVAL_DAY, pendingIntentDisabler);
        
        /* Schedule task for automatically turn on sound */
        cal1.set(Calendar.HOUR_OF_DAY, mSilentModeEndAt.get(Calendar.HOUR_OF_DAY));
        cal1.set(Calendar.MINUTE, mSilentModeEndAt.get(Calendar.MINUTE));
        Log.variable("HOUR", String.valueOf(cal1.get(Calendar.HOUR_OF_DAY)));
        Log.variable("MINUTE", String.valueOf(cal1.get(Calendar.MINUTE)));
        Log.variable("SECOND", String.valueOf(cal1.get(Calendar.SECOND)));
        alarmManagerEnabler.setRepeating(AlarmManager.RTC_WAKEUP,
                cal1.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntentEnabler); 
    }
    
    /**
     * Stops any schedule
     */
    private void stopScheduler() {
        Log.message("stopScheduler");
        alarmManagerEnabler.cancel(pendingIntentEnabler);
        alarmManagerDisabler.cancel(pendingIntentDisabler);
    }
}
