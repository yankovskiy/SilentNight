package ru.neverdark.silentnight;

import ru.neverdark.log.Log;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;

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
        turnOffSound();
        stopSelf();
    }
    
    /**
     * Turns off sound for ring and notification
     */
    private void turnOffSound() {
        Log.message("Mute sound");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

}
