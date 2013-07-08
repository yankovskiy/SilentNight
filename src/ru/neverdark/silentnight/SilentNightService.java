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
            Log.message("run");
            /* get current time */
            Calendar calendar = new GregorianCalendar();
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            /* IF statement for turn off device sound */
            if (calendar.get(Calendar.HOUR_OF_DAY) == mSilentModeStartAt
                    .get(Calendar.HOUR_OF_DAY)) {
                if (calendar.get(Calendar.MINUTE) == mSilentModeStartAt
                        .get(Calendar.MINUTE)) {
                    Log.message("mute");
                    audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                    audioManager.setStreamMute(
                            AudioManager.STREAM_NOTIFICATION, true);
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
                    Log.message("unmute");
                    audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                    audioManager.setStreamMute(
                            AudioManager.STREAM_NOTIFICATION, false);
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

    /* sleeping time for active job */
    private final byte MINUTE = 60;
    private boolean mIsServiceEnabled;
    private Calendar mSilentModeEndAt;
    private Calendar mSilentModeStartAt;
    private Timer mTimer;

    /* amount of time in milliseconds between subsequent executions */
    private final int PERIOD = 10000;

    /**
     * Constructor
     */
    public SilentNightService() {
        Log.message("SilentNightService.Constructor");
        mTimer = null;
        mSilentModeStartAt = new GregorianCalendar();
        mSilentModeEndAt = new GregorianCalendar();
    }

    /**
     * Cancels the timer and all the scheduled tasks. Function also removes all
     * canceled tasks from the task queue.
     */
    private void freeTimer() {
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

        Log.variable("mSilentModeStartAt",
                mSilentModeStartAt.get(Calendar.HOUR_OF_DAY) + ":"
                        + mSilentModeStartAt.get(Calendar.MINUTE));
        Log.variable("mSilentModeEndAt",
                mSilentModeEndAt.get(Calendar.HOUR_OF_DAY) + ":"
                        + mSilentModeEndAt.get(Calendar.MINUTE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent arg0) {
        Log.message("SilentNightService.onBind");
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        Log.message("SilentNightService.onCreate");
        loadPreferences();

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
        Log.message("SilentNightService.onDestroy");
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
        Log.message("SilentNightService.onStartCommand");
        /* Scheduling restart of crashed service */
        return START_STICKY;
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
}
