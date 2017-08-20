package com.droid.ray.droidturnoff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Robson on 20/08/2017.
 */

public class DroidCommon {
    public static boolean AtivarBotaoFlutuante(final Context context) {
        boolean spf = false;
        try {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            spf = sp.getBoolean("spf_botaoFlutuante", true);
        } catch (Exception ex) {
            Log.d("DroidTurnOff", ex.getMessage());
        }
        return spf;
    }

    public static void turnOffScreen(final Context context) {
        // turn off screen
        try {
            if (!DroidShowDeviceAdmin.EnabledAdminAndLock(context)) {
                Intent intent = new Intent(context, DroidConfigurationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        } catch (Exception ex) {
            Toast.makeText(context, R.string.device_admin_not_enabled,
                    Toast.LENGTH_LONG).show();
            Log.d("DroidTurnOff", "turnOffScreen: " + ex.getMessage());

        }
    }

    public static void stopStartService(Context context, boolean start)
    {
        Intent intentService = new Intent(context, DroidHeadService.class);

        try {
            if (!start) {
                context.stopService(intentService);
            }

        } catch (Exception ex) {
            Log.d("DroidTurnOff", "DroidRestartApp - onReceive - stopService - Erro: " + ex.getMessage());
        }
        try {
            if (start) {
                context.startService(intentService);
            }
            Log.d("DroidTurnOff", "DroidRestartApp - onReceive ");

        } catch (Exception ex) {
            Log.d("DroidTurnOff", "DroidRestartApp - onReceive - startService - Erro: " + ex.getMessage());
        }
    }

}
