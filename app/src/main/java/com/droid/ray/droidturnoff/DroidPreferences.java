package com.droid.ray.droidturnoff;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Robson on 31/07/2017.
 */

public class DroidPreferences {

    public static final String PREF_ID = "DroidTurnOff";

    public static void SetInteger(Context context, String chave, int valor) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(chave, valor);
        editor.commit();
    }

    public static int GetInteger(Context context, String chave) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_ID, 0);
        int i = sharedPreferences.getInt(chave, 0);
        return i;

    }
}
