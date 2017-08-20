package com.droid.ray.droidturnoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Robson on 13/08/2017.
 */

public class DroidRestartService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DroidCommon.stopStartService(context, DroidCommon.AtivarBotaoFlutuante(context));

    }
}
