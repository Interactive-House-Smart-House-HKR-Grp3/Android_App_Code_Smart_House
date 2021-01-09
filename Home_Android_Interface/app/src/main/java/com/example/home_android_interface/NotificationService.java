package com.example.home_android_interface;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    public static final String NOTIFICATION_CHANEL_ID = "10001" ;
    private final static String default_chanel_id = "default" ;

    Timer timer ;
    TimerTask timerTask;
    String TAG = "Timers" ;
    int X_SECS = 5 ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //will implement this part so it starts when we get message from the broker.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG , "onStartCommand");
        super.onStartCommand(intent, flags , startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG , "onCreate") ;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy") ;
        stopTimerTask();
        super.onDestroy();
    }
    final Handler handler = new Handler();

    public  void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask , 5000, X_SECS * 1000);

    }

    public void stopTimerTask(){
        if (timer != null){
            timer.cancel();
            timer = null ;
        }

    }

    public  void  initializeTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        createNotification();
                    }
                });
            }
        } ;
    }

    // Will also further implement createNotifcation

    private void createNotification(){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE ) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext() , default_chanel_id ) ;
        mBuilder.setContentTitle( "My Notification" ) ;
        mBuilder.setContentText( "Notification Listener Service Example" ) ;
        mBuilder.setTicker( "Notification Listener Service Example" ) ;
        mBuilder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        mBuilder.setAutoCancel( true ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
    }
    }



