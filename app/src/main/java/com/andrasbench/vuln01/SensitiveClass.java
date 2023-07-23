package com.andrasbench.vuln01;

import android.util.Log;

public class SensitiveClass {
    public static String sensitiveValue = "secretValue";
    public static void sensitiveFunction() {
        Log.d("Access", "Sensitive function");
    }
}
