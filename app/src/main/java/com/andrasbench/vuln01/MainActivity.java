package com.andrasbench.vuln01;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle b = getIntent().getExtras();
        if (b != null) { // Calling from other activity
            Log.d("Calling", "from another activity");
            // Type 3.1: Denial of service attack (Component -> Component)
            String value = b.getString("value");
            if (value != null) {
                long r = Long.parseUnsignedLong(value);
                long c = 0;
                for (long i = 0; i < r; i++) {
                    c += 1;
                }
                Log.d("Value", Long.toString(c));
            } else {
                Log.d("Calling", "from request data");
                if (b.getBoolean("requestData")) {
                    Intent i = getIntent();
                    // execute command
                    Process process = null;
                    String r = "data from client";
                    String[] commands = {
                            "/system/bin/sh",
                            "-c",
                            b.getString("command")
                    };
                    try {
                        process = Runtime.getRuntime().exec(commands);
                        InputStream p = new BufferedInputStream(process.getInputStream());
                        r = HttpIntentService.readStream(p, 1000);
                    } catch (IOException e) {
                        Log.d("Error", "Cannot execute command");
                    }
                    i.putExtra("result", r);
                    SensitiveClass.sensitiveFunction();
                    i.putExtra("sensitive", SensitiveClass.sensitiveValue);
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
        }
    }
    // Support url encoding for POST request
    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "NotValid";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button button =  findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button button5 = findViewById(R.id.button5);
        Button button6 = findViewById(R.id.button6);

        // Type 1: SQL injection (Client -> Server)
        // POST request to localhost:7999/login with password="1234 OR 1=1", which always return 1 (first row)
        Intent sqlInjection = new Intent(getApplicationContext(), HttpIntentService.class);
        sqlInjection.putExtra("Type", "POST"); // POST request
        sqlInjection.putExtra("Path", "/login"); // /login endpoint
        sqlInjection.putExtra("Body", "password=" + encodeValue("1234 OR 1=1"));

        // Type 2: IDOR - Unauthorized access to sensitive data (Client -> Server)
        // GET request to localhost:7999/content/4, ignoring the needs of login!
        Intent idor = new Intent(getApplicationContext(), HttpIntentService.class);
        idor.putExtra("Type", "GET"); // GET request
        idor.putExtra("Path", "/content/4"); // /content endpoint with id=4
        idor.putExtra("Body", "");

        // Type 3: Denial of service attack
        // attack server (client -> server)
        Intent ddos = new Intent(getApplicationContext(), HttpIntentService.class);
        ddos.putExtra("Type", "POST"); // POST request
        ddos.putExtra("Path", "/checkPrime"); // /checkPrime endpoint
        ddos.putExtra("Body", "num=598957906193"); // Careful for this attack

        // Type 3.2: attack client (server -> client)
        Intent reversed_ddos = new Intent(getApplicationContext(), HttpIntentService.class);
        reversed_ddos.putExtra("Type", "GET"); // POST request
        reversed_ddos.putExtra("Path", "/getRandom"); // /getRandom endpoint
        reversed_ddos.putExtra("Body", ""); // Careful for this attack


        // Type 4: Eavesdropping on both side (client <-> server)
        Intent secret = new Intent(getApplicationContext(), HttpIntentService.class);
        secret.putExtra("Type", "POST"); // POST request
        secret.putExtra("Path", "/secret"); // /secret endpoint
        secret.putExtra("Body", "secret=secretvalue"); // Careful for this attack

        // Type 5: Unauthorized access to functionality
        // & Type 6: Command Injection
        Intent command = new Intent(getApplicationContext(), HttpIntentService.class);
        command.putExtra("Type", "GET"); // GET request
        command.putExtra("Path", "/command"); // /command
        command.putExtra("Body", "");

//        // Type 6: Command injection (server -> client)
//        Intent command = new Intent(getApplicationContext(), HttpIntentService.class);
//        command.putExtra("Type", "GET"); // GET request
//        command.putExtra("Path", "/command"); // /command
//        command.putExtra("Body", "");


        button.setOnClickListener(v -> startService(sqlInjection));
        button2.setOnClickListener(v -> startService(idor));
        button3.setOnClickListener(v -> startService(ddos));
        button4.setOnClickListener(v -> startService(reversed_ddos));
        button5.setOnClickListener(v -> startService(command));
        button6.setOnClickListener(v -> startService(secret));
    }
}