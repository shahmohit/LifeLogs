package com.android.lifelogs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class AlarmBootReceiver extends BroadcastReceiver {
    String RECEIVER_NAME = "AlarmBootReceiver";

    public AlarmBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (Constants.PERIODIC_LOCATIONS) {
                Log.d(RECEIVER_NAME, "Boot Signal Received");
                Intent mServiceIntent = new Intent(context, AlarmService.class);
                context.startService(mServiceIntent);
            }
        }
    }
}
