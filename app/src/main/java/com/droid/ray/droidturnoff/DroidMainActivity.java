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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        context = getBaseContext();

        try {
            DevicePolicyManager policyManager = (DevicePolicyManager) context
                    .getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminReceiver = new ComponentName(context,
                    ScreenOffAdminReceiver.class);
            boolean admin = policyManager.isAdminActive(adminReceiver);

            if (!admin) {

                ComponentName mDeviceAdminSample = new ComponentName(this, ScreenOffAdminReceiver.class);
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                        mDeviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "You need to activate Device Administrator to perform phonelost tasks!");
                startActivity(intent);
            }

        } catch (Exception ex) {
            Log.d("DroidTurnOff", "Erro: " + ex.getMessage());
        }

        Intent intentService = new Intent(getBaseContext(), DroidHeadService.class);
        startService(intentService);

        finish();


    }
}

