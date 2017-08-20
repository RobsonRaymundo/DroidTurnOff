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
        DroidCommon.stopStartService(context, DroidCommon.AtivarBotaoFlutuante(context));
    }
}
