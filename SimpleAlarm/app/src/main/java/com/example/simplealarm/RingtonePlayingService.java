package com.example.simplealarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class RingtonePlayingService extends Service {

    MediaPlayer media_song;
    boolean isRunning;
    int startId;
    int resId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        //fetch the extra string values
        String state = intent.getStringExtra("extra");
        String song = intent.getStringExtra("song");

        Log.e("Ringtone state is : " , state );

        //put the notification here

        //notification
        //set up the notification service
        NotificationManager notify_manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        //set up an intent that goes to the MainActivity
        Intent intent_main = new Intent(this.getApplicationContext(), MainActivity.class);

        //set up a pending intent
        PendingIntent pending_main = PendingIntent.getActivity(this,
                0, intent_main, 0);

        //make the notification parameters
        NotificationCompat.Builder notif_popup = new NotificationCompat.Builder(
                this.getApplicationContext(),
                "channelTest");

        //check the state values from the intent
        assert state != null;
        switch(state) {
            case "alarm on" :
                startId = 1;
//                isRunning = true;
                break;
            case "alarm off" :
                startId = 0;
                break;
            case "alarm shake off" :
                startId = 0;
                break;
            default :
                startId = 0;
                break;
        }

        //check the song values from the intent
        switch (song) {
            case "your name" :
                resId = R.raw.ringtone;
                break;
            case "blue water" :
                resId = R.raw.blue_water;
                break;
            case "hikari" :
                resId = R.raw.hikari;
                break;
            default:
                resId = R.raw.ringtone;
                break;
        }

        //if else statements

        //if there is no music playing, and the user pressed "alarm on" button
        //music should start playing
        if(!this.isRunning && startId == 1) {
            Log.e("there is no music", "and you want start");
//
//            create an instance of the media player
            media_song = MediaPlayer.create(this, resId);

            //start the ringtone
            media_song.start();

            this.isRunning = true;
            this.startId = 0;


            notif_popup.setContentIntent(pending_main);
            notif_popup.setSmallIcon(R.drawable.ic_alarm);
            notif_popup.setContentTitle("An alarm is going off");
            notif_popup.setContentText("Click Me !");
            notif_popup.setPriority(NotificationCompat.PRIORITY_MAX);
//
//            Notification notification = notif_popup.build();
//            notification.sound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + resId);

            //set up the notification to appear
            notify_manager.notify(1,notif_popup.build());

        }

        //if there is music playing, and the user pressed "alarm off" button
        //music should stop playing
        else if(this.isRunning && startId == 0) {
            Log.e("there is music", "and you want end");

            //stop the ringtone
            media_song.stop();
            media_song.reset();

            this.isRunning = false;
            this.startId = 0;
        }

        //these are if the user presses random buttons
        //just to bug-proof the app
        //if there is no music playing, and the user pressed "alarm off" button
        //do nothing
        else if(!this.isRunning && startId == 0) {
            Log.e("there is no music", "and you want end");

            this.isRunning = false;
            this.startId = 0;
        }

        //if there is music playing, and the user pressed "alarm on" button
        //do nothing
        else if(this.isRunning && startId == 1) {
            Log.e("there is music", "and you want start");

            this.isRunning = true;
            this.startId = 1;
        }

        //can't think of anything else, just to catch the odd event
        else {
            Log.e("else", "somehow you reached this");
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("onDestroy called", "Ta da");

        super.onDestroy();
        this.isRunning = false;

    }


}
