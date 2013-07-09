package ru.neverdark.silentnight;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ru.neverdark.log.Log;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;

/**
 * Service automatically turn on and off the device sound
 */
public class SilentNightService extends Service {

    /**
     * Job for automatically turn on and off the device sound
     */
    private class Job extends TimerTask {
        @Override
        public void run() {
            /* get current time */
            Calendar calendar = new GregorianCalendar();
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            Log.variable("HOUR_OF_DAY", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
            Log.variable("MINUTE", String.valueOf(calendar.get(Calendar.MINUTE)));
            
            /* IF statement for turn off device sound */
            if (calendar.get(Calendar.HOUR_OF_DAY) == mSilentModeStartAt
                    .get(Calendar.HOUR_OF_DAY)) {
                if (calendar.get(Calendar.MINUTE) == mSilentModeStartAt
                        .get(Calendar.MINUTE)) {
                    saveCurrentVolumeValues();
                    turnOffSound();
                    /* Minute sleep to avoid re-mute sound */
                    try {
                        TimeUnit.SECONDS.sleep(MINUTE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            /* IF statement for turn on device sound */
            if (calendar.get(Calendar.HOUR_OF_DAY) == mSilentModeEndAt
                    .get(Calendar.HOUR_OF_DAY)) {
                if (calendar.get(Calendar.MINUTE) == mSilentModeEndAt
                        .get(Calendar.MINUTE)) {

                    /* restore volume only if user not change volume manually */
                    if ((0 == audioManager
                            .getStreamVolume(AudioManager.STREAM_RING))
                            && (0 == audioManager
                                    .getStreamVolume(AudioManager.STREAM_NOTIFICATION))) {
                        turnOnSound();
                    }
                    /* Minute sleep to avoid re-enable sound */
                    try {
                        TimeUnit.SECONDS.sleep(MINUTE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private AudioManager audioManager;
    /* sleeping time for active job */
    private final byte MINUTE = 60;
    private boolean mIsServiceEnabled;
    private int mPreviousNotificationVolume;
    private int mPreviousRingVolume;
    private Calendar mSilentModeEndAt;
    private Calendar mSilentModeStartAt;
    private Timer mTimer;

    /* amount of time in milliseconds between subsequent executions */
    private final int PERIOD = 10000;

    /**
     * Constructor
     */
    public SilentNightService() {
        Log.message("SilentNightService");
        mTimer = null;
        mSilentModeStartAt = new GregorianCalendar();
        mSilentModeEndAt = new GregorianCalendar();
    }

    /**
     * Cancels the timer and all the scheduled tasks. Function also removes all
     * canceled tasks from the task queue.
     */
    private void freeTimer() {
        Log.message("freeTimer");
        mTimer.cancel();
        mTimer.purge();
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
        mPreviousRingVolume = sp.getInt(Constant.PREF_PREVIOUS_RING_VOLUME, 0);
        mPreviousNotificationVolume = sp.getInt(
                Constant.PREF_PREVIOUS_NOTIFICATION_VOLUME, 0);
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
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        /* Starts scheduler if service enabled or stopping service in other case */
        if (mIsServiceEnabled) {
            startScheduler();
        } else {
            stopSelf();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        Log.message("onDestroy");
        /* If time exists cancel and erase all active jobs */
        if (mTimer != null) {
            freeTimer();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /* Scheduling restart of crashed service */
        return START_STICKY;
    }

    /**
     * Sets current volume values for ring and notification
     */
    private void saveCurrentVolumeValues() {
        Log.message("saveCurrentVolumeValues");
        mPreviousRingVolume = audioManager
                .getStreamVolume(AudioManager.STREAM_RING);
        mPreviousNotificationVolume = audioManager
                .getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constant.PREF_PREVIOUS_RING_VOLUME, mPreviousRingVolume);
        editor.putInt(Constant.PREF_PREVIOUS_NOTIFICATION_VOLUME,
                mPreviousNotificationVolume);
        editor.commit();
    }

    /**
     * Starts scheduler for automatically turn on and off device sound
     */
    private void startScheduler() {
        Log.message("startScheduler");
        if (mTimer != null) {
            freeTimer();
        } else {
            mTimer = new Timer();
        }
        /* schedule task periodic 10 seconds */
        mTimer.scheduleAtFixedRate(new Job(), 0, PERIOD);
    }

    /**
     * Turns off sound for ring and notification
     */
    private void turnOffSound() {
        Log.message("Mute");
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    /**
     * Turns on sound for ring and notification
     */
    private void turnOnSound() {
        Log.message("Unmute");
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }
}
