package com.cti.gogolalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.squareup.seismic.ShakeDetector;

/**
 * Created by CTI on 12/06/2016.
 */
public class ShakeService extends Service implements ShakeDetector.Listener {

    private final int NOTIFICATION_ID = 1;
    private NotificationManager mNotifyMgr;
    private static int resID;
    private MediaPlayer mediaPlayer;
    private ShakeDetector sd;
    private AudioManager audio;
    private int originalVolume;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ShakeService", "Service started");

        // init SensorManager
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sd = new ShakeDetector(this);
        sd.start(sensorManager);

        // init notification icon
        showNotificationIcon();

        // init media player and audio manager
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        resID = getResources().getIdentifier("gogolalert", "raw", getPackageName());

        mediaPlayer = MediaPlayer.create(this, resID);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //restore original media volume
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.d("ShakeService", "Service stoped");

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        if (sd != null) {
            sd.stop();
        }

        hideNotificationIcon();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void hearShake() {
        Log.d("ShakeService", "heard a Shake");

        if (!mediaPlayer.isPlaying()) {

            Log.d("ShakeService", "Start playing gogol mp3");
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();

            // save current media volume
            originalVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

            // set media volume to 50%
            fixMediaVolume();

            // playing mp3
            mediaPlayer.start();

        }
    }

    private void showNotificationIcon() {

        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon_notif)
                .setContentTitle(getString(R.string.enabled_service));
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, notification);
    }

    private void hideNotificationIcon() {
        mNotifyMgr.cancel(NOTIFICATION_ID);
    }

    private void fixMediaVolume() {
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float percent = 0.5f;
        int newVolume = (int) (maxVolume*percent);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
    }
}
