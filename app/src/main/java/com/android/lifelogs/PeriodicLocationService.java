package com.android.lifelogs;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PeriodicLocationService extends IntentService implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    String SERVICE_NAME = "PeriodicLocationService";
    LocationClient mLocationClient;
    Location mLocation;
    int runCount = 1;

    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Log.d(SERVICE_NAME,"Location Service Connected");
        //mLocationClient.setMockMode(true);
        periodicLocation();
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        Log.d(SERVICE_NAME, "Location Service Disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            Log.d(SERVICE_NAME,"Connection Failed");

        } else {
            Log.d(SERVICE_NAME, "Connection Failed");
        }
    }

    private void getCurrentLocation() {
        mLocation = mLocationClient.getLastLocation();
        Double lat = mLocation.getLatitude();
        Double lon = mLocation.getLongitude();
        String date = new SimpleDateFormat("yy-MM-dd").format(new Date());
        String time = new SimpleDateFormat("HH-mm-ss").format(new Date());
        Intent localIntent = new Intent(Constants.PERIODIC_BROADCAST_ACTION);
        localIntent.putExtra(Constants.PLAT_DATA, Double.toString(lat));
        localIntent.putExtra(Constants.PLON_DATA, Double.toString(lon));
        localIntent.putExtra(Constants.PDATE_DATA, date);
        localIntent.putExtra(Constants.PTIME_DATA, time);
        Log.d(SERVICE_NAME, localIntent.getAction().toString());
        Log.d(SERVICE_NAME, "Periodic Current Location Broadcast");
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void periodicLocation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (runCount < Constants.maxCount) {
                    getCurrentLocation();
                    runCount = runCount+1;
                    Log.d(SERVICE_NAME, Integer.toString(runCount));
                    periodicLocation();
                }
                else {
                    mLocationClient.disconnect();
                    Log.d(SERVICE_NAME,"Periodic Location Complete");
                }
            }
        }, 10000);

    }

    public PeriodicLocationService() {
        super("PeriodicLocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //final String action = intent.getAction();
            mLocationClient = new LocationClient(this,this,this);
            mLocationClient.connect();
        }
    }

}
