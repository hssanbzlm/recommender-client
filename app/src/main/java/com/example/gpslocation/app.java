package com.example.gpslocation;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class app extends Application {
    public static final String CHANNEL_1_ID ="channel1";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();

    }

    public void createNotificationChannels()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1= new NotificationChannel(
                    CHANNEL_1_ID,"channel 1", NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("this is channel 1");
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

    }
}
