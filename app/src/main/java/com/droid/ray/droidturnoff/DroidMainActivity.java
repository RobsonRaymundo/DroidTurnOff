package com.droid.ray.droidturnoff;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DroidMainActivity extends AppCompatActivity {


    private static final int RESULT_ENABLE = RESULT_CANCELED;
    private Context context;

    public Object getThis()
    {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getBaseContext();

        try {
            if (!DroidShowDeviceAdmin.EnabledAdmin(this)) {
              DroidShowDeviceAdmin.Show(this);
            }

        } catch (Exception ex) {
            Log.d("DroidTurnOff", "Erro: " + ex.getMessage());
        }

        Intent intentService = new Intent(getBaseContext(), DroidHeadService.class);
        startService(intentService);

        finish();


    }
}

