package com.android.lifelogs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class RequestLocationReceiver extends BroadcastReceiver {

    GetLocationReceiver getLocR;
    String RECEIVER_NAME = "RequestLocationReceiver";
    public RequestLocationReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(RECEIVER_NAME,intent.getAction().toString());
        Log.d(RECEIVER_NAME, "Intent Registered");
        IntentFilter mStatusIntentFilter = new IntentFilter(Constants.PERIODIC_BROADCAST_ACTION);
        GetLocationReceiver getLocR = new GetLocationReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(getLocR,mStatusIntentFilter);
        Intent getLocation = new Intent(context,PeriodicLocationService.class);
        context.startService(getLocation);
    }
}
