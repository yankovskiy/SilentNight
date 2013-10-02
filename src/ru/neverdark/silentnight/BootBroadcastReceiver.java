package ru.neverdark.silentnight;

import ru.neverdark.log.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Class for receive android.intent.action.BOOT_COMPLETED
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /* we receive BOOT_COMPLETED message, start our service */
        Log.message("Enter");
        context.startService(new Intent(context, SilentNightService.class));
    }

}
