package com.android.lifelogs;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class PrefsActivity extends PreferenceActivity {

    public String ACTIVITY_NAME = "PrefsActivity";
    SharedPreferences prefs;
    AlarmReceiver Alarm = new AlarmReceiver();
    boolean prefChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefChanged = false;
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onPause() {
        if (prefChanged) {
            Alarm.CancelAlarm(this);
            Log.d(ACTIVITY_NAME, "Location Capture Stopped");
            Alarm.SetAlarm(this);
        }
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
        super.onPause();
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            // Implementation
            int val = prefs.getInt(Constants.IntervalKey, 100);
            Log.d(ACTIVITY_NAME, Integer.toString(val));
            prefChanged = true;
        }
    };
}
