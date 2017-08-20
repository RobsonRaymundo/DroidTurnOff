package com.droid.ray.droidturnoff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class DroidTurnOffActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            DroidCommon.turnOffScreen(getBaseContext());
        } catch (
                Exception ex)
        {
            Log.d("DroidTurnOff", "DroidTurnOffActivity - onCreate - Erro: " + ex.getMessage());
        }
        finish();
    }
}
