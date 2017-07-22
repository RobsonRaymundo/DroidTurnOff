package com.droid.ray.droidturnoff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DroidMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intentService = new Intent(getBaseContext(), DroidHeadService.class);
        startService(intentService);
        finish();
    }
}
