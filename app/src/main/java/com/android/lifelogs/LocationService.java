package com.android.lifelogs;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
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
 * helper methods.
 */
public class LocationService extends IntentService implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    String SERVICE_NAME = "LocationService";
    LocationClient mLocationClient;
    Location mLocation;
    String actionType;
    Intent localIntent;
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Log.d(SERVICE_NAME,"Location Service Connected");
        getCurrentLocation();
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        Log.d(SERVICE_NAME,"Location Service Disconnected");
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
        localIntent = new Intent(actionType);
        localIntent.putExtra(Constants.PLAT_DATA, Double.toString(lat));
        localIntent.putExtra(Constants.PLON_DATA, Double.toString(lon));
        localIntent.putExtra(Constants.PDATE_DATA, date);
        localIntent.putExtra(Constants.PTIME_DATA, time);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        Log.d(SERVICE_NAME, "Current Location is Being Broadcast");
    }

    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            actionType = intent.getAction();
            mLocationClient = new LocationClient(this,this,this);
            mLocationClient.connect();
        }
    }

}
