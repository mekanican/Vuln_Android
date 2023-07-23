package com.andrasbench.vuln01;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.Base64;
import java.util.Properties;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class HttpIntentService extends IntentService {

    public HttpIntentService() {
        super("HttpIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            HttpURLConnection urlConnection = null;
            try {
                String type = intent.getStringExtra("Type");
                String path = intent.getStringExtra("Path");
                String body = intent.getStringExtra("Body");

                String urlString = "http://" + getResources().getString(R.string.local_server_ipv4) + ":" +
                        getResources().getString(R.string.local_server_port) + path;
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(type);

                if (type.equals("POST")) {
                    urlConnection.setRequestProperty( "Content-type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty( "Accept", "*/*" );
                    OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                    out.write(body);
                    out.close();
                }

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = readStream(in, 5000);

                switch(path) {
                    case "/command":
                        commandInjection(response);
                        break;
                    case "/getRandom":
                        selfDdos(response);
                        break;
                    default:
                        break;
                }
                Intent activityIntent = new Intent(this, ResponseActivity.class);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activityIntent.putExtra("status_msg", "SUCCESS");
                activityIntent.putExtra("response_msg", response);
                this.startActivity(activityIntent);
                if (urlConnection != null) urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                if (urlConnection != null) urlConnection.disconnect();
                throw new RuntimeException(e);
            }
        }
    }

    private static void selfDdos(String response) {
        long result = Long.parseUnsignedLong(response);
        int c = 0;
        for (int i = 0; i < result; i++) {
            c += 1; // Hope this won't get optimized
        }
    }

    private static void commandInjection(String response) {
        try {
            String[] commands = {
                    "/system/bin/sh",
                    "-c",
                    response
            };
            Process process = Runtime.getRuntime().exec(commands);
            InputStream p = new BufferedInputStream(process.getInputStream());
            Log.d("Command result", readStream(p, 1000));
            SensitiveClass.sensitiveFunction();
            Log.d("sensitive", SensitiveClass.sensitiveValue);
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readStream(InputStream stream, int maxReadSize)
            throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }
}
