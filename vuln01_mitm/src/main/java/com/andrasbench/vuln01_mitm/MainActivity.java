package com.andrasbench.vuln01_mitm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            String value = b.getString(Intent.EXTRA_TEXT);
            Log.d("Captured", value);
            Intent i = getPackageManager().getLaunchIntentForPackage("com.andrasbench.vuln01");
            if (i != null) {
                i.putExtra(Intent.EXTRA_TEXT, value);
                startActivity(i);
            } else {
                Log.d("Intent", "MITM fail");
            }
        }
    }
}