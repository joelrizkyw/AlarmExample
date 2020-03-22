package com.example.simplealarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.seismic.ShakeDetector;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ShakeDetector.Listener {

    //Make our alarm manager
    AlarmManager alarm_manager;

    TimePicker alarm_timepicker;
    TextView update_text;
    Context context;
    Intent my_intent;

    PendingIntent pending_intent;
    String song;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        this.context = this;

        //Initialize our sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector shakeDetector = new ShakeDetector(this);
        shakeDetector.start(sensorManager);

        //Initialize our alarm manager
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Initialize our timepicker
        alarm_timepicker = (TimePicker) findViewById(R.id.timePicker);

        //Initialize our text update box
        update_text = (TextView) findViewById(R.id.update_text);

        //create a spinner in the main UI
        final Spinner spinner = (Spinner) findViewById(R.id.songSpinner);

        //create an array adapter
        ArrayAdapter<CharSequence> array_adapter = ArrayAdapter.createFromResource(this,
                R.array.songs_array, android.R.layout.simple_spinner_item);

        array_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(array_adapter);

        //Create an instance of a calendar
        final Calendar calendar = Calendar.getInstance();

        //create an intent to the AlarmReceiver class
         my_intent = new Intent(this.context, AlarmReceiver.class);

        //Initialize our start button
        final Button alarm_on = (Button) findViewById(R.id.alarm_on);

        //create an onClick method to start the alarm
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String am_or_pm;
                long alarm_milisecond;

                //setting calendar instance with the hour and minute that we picked
                //on the time picker
                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.AM_PM, setAM_PM(alarm_timepicker.getHour()));

                //check if alarm is set before the current hour or not
                if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    alarm_milisecond = calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY;
                } else {
                    alarm_milisecond = calendar.getTimeInMillis();
                }

                //get the int values of hour and minute
                int hour = alarm_timepicker.getHour();
                int minute = alarm_timepicker.getMinute();

                //log the hour and minute
                Log.e("hour:minute ", hour + ":" + minute);

                //convert the int values to strings
                String hour_string = String.valueOf(hour);
                String minute_string = String.valueOf(minute);

                //convert 24-hour time to 12-hour time
                if(hour > 12) {
                    hour_string = String.valueOf(hour - 12);
                    am_or_pm = "PM";
                } else {
                    am_or_pm = "AM";
                }

                //change 10:7 or 7:10 to 10:07 or 07:10
                //what this means is that we add 0 (zero)
                if( minute < 10) {
                    minute_string = "0" + String.valueOf(minute);
                }

                //get the spinner value
                song = spinner.getSelectedItem().toString();

                //determine the song and put it in the intent as extras
                switch(song) {
                    case "Your Name" :
                        my_intent.putExtra("song", "your name");
                        break;
                    case "Blue Water" :
                        my_intent.putExtra("song", "blue water");
                        break;
                    case "Hikari" :
                        my_intent.putExtra("song", "hikari");
                        break;
                    default:
//                        my_intent.putExtra("song", "your name");
                        break;
                }

                //method that changes the update text Textbox
                set_alarm_text("Alarm set to : " + hour_string + ":" + minute_string + " " + am_or_pm);

                //put in extra string in my_intent
                //tells the clock that you pressed the "alarm on" button
                my_intent.putExtra("extra", "alarm on");

                //create a pending intent that delays the intent
                //until the specified calendar time
                pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0,
                        my_intent, PendingIntent.FLAG_UPDATE_CURRENT);


//                set the alarm manager
//                alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                        pending_intent);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, alarm_milisecond, pending_intent);
                } else {
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, alarm_milisecond, pending_intent);
                }

            }


        });

        //Initialize our stop button
        Button alarm_off = (Button) findViewById(R.id.alarm_off);

        //create an onClick method to stop the alarm
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pending_intent == null) {
                    //do nothing
                } else {
                    //method that changes the update text Textbox
                    set_alarm_text("Alarm Off !");

                    //cancel the alarm
                    alarm_manager.cancel(pending_intent);

                    //put in extra string in my_intent
                    //tells the clock that you pressed the "alarm off" button
                    my_intent.putExtra("extra", "alarm off");

                    //put in extra long in my_intent
                    //to prevent crashes in a Null pointer exception
                    my_intent.putExtra("song", song);

                    //stop the ringtone
                    sendBroadcast(my_intent);
                }
            }
        });

    }

    private void set_alarm_text(String output) {
        update_text.setText(output);
    }

    private int setAM_PM(int hour) {
      int AM_PM;

      if(hour == 0) {
          AM_PM = Calendar.AM;
      } else if(hour < 12) {
          AM_PM = Calendar.AM;
      } else if(hour == 12) {
          AM_PM = Calendar.PM;
      } else {
          AM_PM = Calendar.PM;
      }

      return AM_PM;
    }

    @Override
    public void hearShake() {
        //shake to turn off alarm
        if(pending_intent == null) {
            //do nothing
        } else {
            Log.e("shake", "you just shake your phone");

            set_alarm_text("Alarm off by shaking");

            alarm_manager.cancel(pending_intent);

            my_intent.putExtra("extra", "alarm shake off");
            my_intent.putExtra("song", song);

            sendBroadcast(my_intent);
        }
    }

//    private String addZero(int hour) {
//        String str;
//
//        if(hour < 10) {
//            str = "0" + String.valueOf(hour);
//        } else {
//            hour = hour - 12;
//            str = "0" + String.valueOf(hour);
//        }
//
//        return str;
//    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

            NotificationChannel channelTest = new NotificationChannel("channelTest",
                    "Channel Test",
                    NotificationManager.IMPORTANCE_HIGH);

            channelTest.setDescription("This is channel test");

            try {
                NotificationManager manager = getSystemService(NotificationManager.class);

                manager.createNotificationChannel(channelTest);
            } catch(Exception e) {
                Log.e("NotificationManager value", null);
            }
        }
    }
}
