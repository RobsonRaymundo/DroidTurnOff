package com.droid.ray.droidturnoff;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

public class DroidConfigurationActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    private static final int RESULT_ENABLE = RESULT_CANCELED;
    private Context context;

    SharedPreferences appPreferences;
    boolean isAppInstalled = false;

    public Object getThis() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean enabledAdmin = DroidShowDeviceAdmin.EnabledAdmin(this);

        if (enabledAdmin) {
            setTheme(R.style.TranslucentTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
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

//        Log.d("DroidTurnOff", "onCreate ");

    }

    private void CreateRemoveShortCut(Boolean remove) {
        Intent shortcutIntent = new Intent(getApplicationContext(),
                DroidTurnOffActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Turn Off");
        intent.putExtra(Intent.EXTRA_TITLE, "Turn Off");

        intent.putExtra("ShortCut", "sim");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(
                        getApplicationContext(), R.mipmap.button));
        if (remove) {
            intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        }
        else intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DroidCommon.AtivarBotaoFlutuante(context)) {
            Intent intentService = new Intent(getBaseContext(), DroidHeadService.class);
            startService(intentService);
        }
    }

    private void ShowDeviceAdmin() {
        try {
            DroidShowDeviceAdmin.Show(this);

        } catch (Exception ex) {
            Log.d("DroidTurnOff", "ShowDeviceAdmin - Erro: " + ex.getMessage());
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
            Log.d("DroidTurnOff", "ShowDialog - Erro: " + ex.getMessage());
        }
    }




    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        boolean ativarBotaoFlutuante = DroidCommon.AtivarBotaoFlutuante(context);
        CreateRemoveShortCut(ativarBotaoFlutuante);
        DroidCommon.stopStartService(context, ativarBotaoFlutuante);
        finish();
    }
}

