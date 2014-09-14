package com.android.lifelogs;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.IntentSender;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
 * <p/>
 * helper methods.
 */
public class PeriodicLocationService extends Service implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    String SERVICE_NAME = "PeriodicLocationService";
    LocationClient mLocationClient;
    Location mLocation;
    LogReaderdBHelper mDbHelper;

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationClient = new LocationClient(this, this, this);
        mDbHelper = new LogReaderdBHelper(PeriodicLocationService.this);
        mLocationClient.connect();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Log.d(SERVICE_NAME, "Location Service Connected");
        saveCurrentLocation();
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        Log.d(SERVICE_NAME, "Location Service Disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            Log.d(SERVICE_NAME, "Connection Failed");

        } else {
            Log.d(SERVICE_NAME, "Connection Failed");
        }
    }

    private void saveCurrentLocation() {
        mLocation = mLocationClient.getLastLocation();
        Double lat = mLocation.getLatitude();
        Double lon = mLocation.getLongitude();
        String date = new SimpleDateFormat("yy-MM-dd").format(new Date());
        String time = new SimpleDateFormat("HH-mm-ss").format(new Date());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LogReaderContract.LogEntry.COLUMN_NAME_ENTRY_ID, date + ":" + time);
        values.put(LogReaderContract.LogEntry.COLUMN_NAME_DATE, date);
        values.put(LogReaderContract.LogEntry.COLUMN_NAME_TIME, time);
        values.put(LogReaderContract.LogEntry.COLUMN_NAME_LAT, lat);
        values.put(LogReaderContract.LogEntry.COLUMN_NAME_LON, lon);
        long newRowId;
        newRowId = db.insert(
                LogReaderContract.LogEntry.TABLE_NAME,
                null,
                values);
        Long numRows = DatabaseUtils.queryNumEntries(db, LogReaderContract.LogEntry.TABLE_NAME);
        Log.d(SERVICE_NAME, "Database rows = " + Long.toString(numRows));
        mLocationClient.disconnect();
    }

}