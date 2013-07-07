package ru.neverdark.silentnight;

import ru.neverdark.log.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.message("BootBroadcastReceiver.onReceive");
        context.startService(new Intent(context, SilentNightService.class));
    }

}
