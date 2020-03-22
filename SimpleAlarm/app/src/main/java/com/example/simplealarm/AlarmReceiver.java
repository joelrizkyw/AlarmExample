package com.example.simplealarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("We are in the receiver", "Yay !");

        //fetch extra strings from the intent
        String get_your_string = intent.getStringExtra("extra");
        String song_string = intent.getStringExtra("song");


        Log.e("What is the key ?", get_your_string);
        Log.e("What is the song ?", song_string);

        //create an intent to the ringtone service
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        //pass the extra string from MainActivity to the Ringtone Playing Service
        service_intent.putExtra("extra", get_your_string);
        service_intent.putExtra("song", song_string);

        //start the ringtone playing service
        context.startService(service_intent);
    }
}
