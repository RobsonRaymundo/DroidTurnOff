package com.droid.ray.droidturnoff;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DroidMainActivity extends AppCompatActivity {


    private static final int RESULT_ENABLE = RESULT_CANCELED;
    private Context context;

    public Object getThis() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getBaseContext();

        boolean exibeTelaInicial = !DroidShowDeviceAdmin.EnabledAdmin(this);

        if (exibeTelaInicial) {
            setTheme(R.style.AppTheme);
            ShowDialog();
        }
        else {
            finish();
        }
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intentService = new Intent(getBaseContext(), DroidHeadService.class);
        startService(intentService);
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
}

