package ru.neverdark.silentnight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class for receive android.intent.action.BOOT_COMPLETED
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /* we receive BOOT_COMPLETED message, start our service */
        context.startService(new Intent(context, SilentNightService.class));
    }

}
