package com.example.wi_fi_scanning;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;

public class IMEI {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String get_device_id(Context context){
        TelephonyManager Tel = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return IMEI;
    }
}
