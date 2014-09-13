package com.android.lifelogs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchActivity extends FragmentActivity {

    String ACTIVITY_NAME = "SearchActivity";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    String[] projection = {
            LogReaderContract.LogEntry.COLUMN_NAME_TIME,
            LogReaderContract.LogEntry.COLUMN_NAME_LAT,
            LogReaderContract.LogEntry.COLUMN_NAME_LON,
    };
    String sortOrder = LogReaderContract.LogEntry.COLUMN_NAME_TIME + " DESC";
    LogReaderdBHelper mDbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mDbHelper = new LogReaderdBHelper(SearchActivity.this);
        db = mDbHelper.getReadableDatabase();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.search_map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        Cursor c = db.query(
                LogReaderContract.LogEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        int count = 1;
        while (c.moveToNext()) {
            String itemTime = c.getString(c.getColumnIndexOrThrow(LogReaderContract.LogEntry.COLUMN_NAME_TIME));
            String lat = c.getString(c.getColumnIndexOrThrow(LogReaderContract.LogEntry.COLUMN_NAME_LAT));
            String lon = c.getString(c.getColumnIndexOrThrow(LogReaderContract.LogEntry.COLUMN_NAME_LON));
            Log.d(ACTIVITY_NAME, itemTime);
            LatLng latlon = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            mMap.addCircle(new CircleOptions()
                    .center(latlon)
                    .radius(36f)
                    .fillColor(getResources().getColor(R.color.wave))
                    .strokeWidth(2f));
            /*
            mMap.addMarker(new MarkerOptions()
                    .position(latlon)
                    .title(Integer.toString(count)));
            */
            if (count == 1) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlon,15));
            }
            count = count + 1;
        }
    }
}
