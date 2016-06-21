package com.cti.gogolalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by CTI on 12/06/2016.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("BootReceiver", "Received intent : " + intent.getAction().toString());

        if(shouldStartService(context)) {
            Log.d("BootReceiver", "Resuming gogol service from device boot");
            Intent serviceIntent = new Intent(context, ShakeService.class);
            context.startService(serviceIntent);
        }
    }

    private boolean shouldStartService(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.gogol_pref), Context.MODE_PRIVATE);
        return sharedPref.getBoolean("enableService", true);
    }
}
