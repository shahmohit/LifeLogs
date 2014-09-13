package com.android.lifelogs;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GetLocationReceiver extends BroadcastReceiver {

    String RECEIVER_NAME = "GetLocationReceiver";
    public GetLocationReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogReaderdBHelper mDbHelper = new LogReaderdBHelper(context);
        Log.d(RECEIVER_NAME,intent.getAction().toString());
        String lat = intent.getExtras().getString(Constants.PLAT_DATA);
        String lon = intent.getExtras().getString(Constants.PLON_DATA);
        String date = intent.getExtras().getString(Constants.PDATE_DATA);
        String time = intent.getExtras().getString(Constants.PTIME_DATA);
        Log.d(RECEIVER_NAME,lat);
        Log.d(RECEIVER_NAME,lon);
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LogReaderContract.LogEntry.COLUMN_NAME_ENTRY_ID, date+":"+time);
        values.put(LogReaderContract.LogEntry.COLUMN_NAME_DATE, date);
        values.put(LogReaderContract.LogEntry.COLUMN_NAME_TIME, time);
        values.put(LogReaderContract.LogEntry.COLUMN_NAME_LAT, lat);
        values.put(LogReaderContract.LogEntry.COLUMN_NAME_LON, lon);
        long newRowId;
        newRowId = db.insert(
                LogReaderContract.LogEntry.TABLE_NAME,
                null,
                values);
    }
}
