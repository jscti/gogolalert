package com.cti.gogolalert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private Switch serviceSwitch;
    private Intent intent;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        intent = new Intent(MainActivity.this, ShakeService.class);
        sharedPref = getSharedPreferences(getString(R.string.gogol_pref), Context.MODE_PRIVATE);

        // by default (just after app install, or after a force close), service is started
        // all other time, service will be resumed at boot or when app start
        if (getEnableFromPref()) {
            toggleService(true);
        }

        initViews();
        initListeners();
    }

    private void initViews() {
        serviceSwitch = (Switch) findViewById(R.id.serviceSwitch);
        serviceSwitch.setChecked(getEnableFromPref());
    }

    private void initListeners() {
        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("MainActivity", "switch new value : " + isChecked);
                toggleService(isChecked);
            }
        });
    }

    private void toggleService(final boolean enable) {

        SharedPreferences.Editor editor = sharedPref.edit();

        if (enable) {
            Log.d("MainActivity", "enabling service");
            startService(intent);
            editor.putBoolean("enableService", true);

        } else {
            Log.d("MainActivity", "disabling service");
            stopService(intent);
            editor.putBoolean("enableService", false);
        }

        editor.apply();
    }

    private boolean getEnableFromPref() {
        return sharedPref.getBoolean("enableService", true);
    }
}
