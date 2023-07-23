package com.andrasbench.vuln01;


import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResponseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        if (getIntent().hasExtra("status_msg")) {
            String status = getIntent().getStringExtra("status_msg");
            if (status.equals("SUCCESS")) {
                if (getIntent().hasExtra("response_msg")) {
                    TextView header = (TextView) findViewById(R.id.headingText);
                    header.setText("Response From Server:");

                    String responseMsg = getIntent().getStringExtra("response_msg");
                    TextView responseText = (TextView) findViewById(R.id.serverResponseText);
                    responseText.setText(responseMsg);
                }
            }
        }
    }
}