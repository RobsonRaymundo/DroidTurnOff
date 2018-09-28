package com.droid.ray.droidturnoff;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

public class DroidConfigurationActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context context;
    public Object getThis() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean enabledAdmin = DroidShowDeviceAdmin.EnabledAdmin(this);

        context = getBaseContext();
        super.onCreate(savedInstanceState);

        if (!enabledAdmin) {
            ShowDialog();
        }
        else
        {
            addPreferencesFromResource(R.xml.preferences);
            PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        }

        Log.d(DroidCommon.TAG, "onCreate ");


    }

    private void CreateRemoveShortCut(Boolean remove) {
        Intent shortcutIntent = new Intent(getApplicationContext(),
                DroidTurnOffActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        Intent intent = new Intent();

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Desligar");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(
                        getApplicationContext(), R.mipmap.button));
        if (remove) {
            intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            Log.d(DroidCommon.TAG, "RemoveShortCut ");
        }
        else {
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            Log.d(DroidCommon.TAG, "CreatehortCut");
        }
        getApplicationContext().sendBroadcast(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        DroidCommon.stopStartService(context, DroidCommon.AtivarBotaoFlutuante(context));
        Log.d(DroidCommon.TAG, "onDestroy");
    }

    private void ShowDeviceAdmin() {
        try {
            DroidShowDeviceAdmin.Show(this);

        } catch (Exception ex) {
            Log.d(DroidCommon.TAG, " - Erro: " + ex.getMessage());
        }
    }

    private void ShowDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ShowDeviceAdmin();
                    finish();

                }

            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setMessage(getString(R.string.messageAdmin));
            dialog.show();

        } catch (
                Exception ex)

        {
            Log.d(DroidCommon.TAG, " - Erro: " + ex.getMessage());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        boolean ativarBotaoFlutuante = DroidCommon.AtivarBotaoFlutuante(context);
        CreateRemoveShortCut(ativarBotaoFlutuante);
        DroidCommon.stopStartService(context, ativarBotaoFlutuante);
        finish();
        Log.d(DroidCommon.TAG, " onSharedPreferenceChanged " );
    }
}

