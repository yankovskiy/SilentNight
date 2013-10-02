package ru.neverdark.silentnight;

import ru.neverdark.log.Log;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;

/**
 * Enables sound on your device
 */
public class EnableSoundService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        turnOnSound();
        stopSelf();
    }
    
    /**
     * Turns on sound for ring and notification
     */
    private void turnOnSound() {
        Log.message("Enter");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

}
