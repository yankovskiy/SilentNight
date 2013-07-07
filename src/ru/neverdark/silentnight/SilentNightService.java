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

public class SilentNightService extends Service {

    private Calendar mSilentModeStartAt;
    private Calendar mSilentModeEndAt;
    private boolean mIsEnabled;
    private Timer mTimer;
    /* amount of time in milliseconds between subsequent executions */
    private final int PERIOD = 10000;
    private final byte MINUTE = 60;

    public SilentNightService() {
        Log.message("SilentNightService.Constructor");
        mTimer = null;
        mSilentModeStartAt = new GregorianCalendar();
        mSilentModeEndAt = new GregorianCalendar();
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

    @Override
    public void onCreate() {
        Log.message("SilentNightService.onCreate");
        loadPrefs();
        
        if (mIsEnabled) { 
            startScheduler();
        } else {
            stopSelf();
        }
    }

    private void startScheduler() {
        Log.message("startScheduler");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        } else {
            mTimer = new Timer();
        }
        /* schedule task periodic 10 seconds */
        mTimer.scheduleAtFixedRate(new Job(), 0, PERIOD);
    }

    private void loadPrefs() {
        Log.message("loadPrefs");
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        mSilentModeStartAt.setTimeInMillis(sp.getLong(
                Constant.PREF_SILENT_MODE_START_AT, 0));
        mSilentModeEndAt.setTimeInMillis(sp.getLong(
                Constant.PREF_SILENT_MODE_END_AT, 0));
        mIsEnabled = sp.getBoolean(Constant.PREF_IS_SERVICE_ENABLED, false);

        Log.variable("mSilentModeStartAt",
                mSilentModeStartAt.get(Calendar.HOUR_OF_DAY) + ":"
                        + mSilentModeStartAt.get(Calendar.MINUTE));
        Log.variable("mSilentModeEndAt",
                mSilentModeEndAt.get(Calendar.HOUR_OF_DAY) + ":"
                        + mSilentModeEndAt.get(Calendar.MINUTE));
    }

    @Override
    public void onDestroy() {
        Log.message("SilentNightService.onDestroy");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.message("SilentNightService.onStartCommand");
        /* Scheduling restart of crashed service */
        return START_STICKY;
    }

    private class Job extends TimerTask {
        @Override
        public void run() {
            Log.message("run");
            Calendar calendar = new GregorianCalendar();
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            
            if (calendar.get(Calendar.HOUR_OF_DAY) == mSilentModeStartAt.get(Calendar.HOUR_OF_DAY)) {
                if (calendar.get(Calendar.MINUTE) == mSilentModeStartAt.get(Calendar.MINUTE)) {
                    Log.message("mute");
                    audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                    audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                    try {
                        TimeUnit.SECONDS.sleep(MINUTE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            if (calendar.get(Calendar.HOUR_OF_DAY) == mSilentModeEndAt.get(Calendar.HOUR_OF_DAY)) {
                if (calendar.get(Calendar.MINUTE) == mSilentModeEndAt.get(Calendar.MINUTE)) {
                    Log.message("unmute");
                    audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                    audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                    try {
                        TimeUnit.SECONDS.sleep(MINUTE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
