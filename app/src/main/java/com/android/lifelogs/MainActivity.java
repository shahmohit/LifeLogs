package com.android.lifelogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    public String ACTIVITY_NAME = "MainActivity";
    EditText fname = null;
    EditText lname = null;
    EditText email = null;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context cxt = getApplicationContext();
        sharedPref = cxt.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        boolean account_check = sharedPref.contains("account_check");
        Log.d(ACTIVITY_NAME,Boolean.toString(account_check));
        if (account_check == true) {
            String fnameTxt = sharedPref.getString("firstname","null");
            String lnameTxt = sharedPref.getString("lastname","null");
            String emailTxt = sharedPref.getString("emailaddress","null");
            setContentView(R.layout.activity_main);
            TextView txt = (TextView) findViewById(R.id.credentials);
            txt.setText("Welcome, " + fnameTxt + " " + lnameTxt);
            Toast.makeText(this, "Alarm Service Started", Toast.LENGTH_SHORT);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startActivity = new Intent(MainActivity.this, LifelogActivity.class);
                    startActivity(startActivity);
                    finish();
                }
            }, 2000);
        }
        else {
            setContentView(R.layout.welcome_signin);
            Button btn = (Button) findViewById(R.id.signin);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fname = (EditText) findViewById(R.id.fname);
                    lname = (EditText) findViewById(R.id.lname);
                    email = (EditText) findViewById(R.id.email);
                    String fnameTxt = fname.getText().toString();
                    String lnameTxt = lname.getText().toString();
                    String emailTxt = email.getText().toString();
                    if ((fnameTxt.matches("")) || (lnameTxt.matches("")) || (emailTxt.matches("")))  {
                        Toast.makeText(MainActivity.this,"Enter All Details",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("account_check",true);
                        editor.putString("firstname",fnameTxt);
                        editor.putString("lastname",lnameTxt);
                        editor.putString("emailaddress",emailTxt);
                        editor.commit();
                        Intent intentAlarm = new Intent(MainActivity.this, AlarmService.class);
                        startService(intentAlarm);
                        PreferenceManager.setDefaultValues(MainActivity.this, R.xml.preferences, false);
                        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
