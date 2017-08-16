package com.droid.ray.droidturnoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Robson on 03/05/2017.
 */

public class DroidRestartApp extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context, DroidHeadService.class);
        try {
            context.stopService(intentService);

        } catch (Exception ex) {
            Log.d("DroidBattery", "DroidRestartApp - onReceive - stopService - Erro: " + ex.getMessage());
        }
        try {
            context.startService(intentService);
            Log.d("DroidBattery", "DroidRestartApp - onReceive ");

        } catch (Exception ex) {
            Log.d("DroidBattery", "DroidRestartApp - onReceive - startService - Erro: " + ex.getMessage());
        }
    }
}
