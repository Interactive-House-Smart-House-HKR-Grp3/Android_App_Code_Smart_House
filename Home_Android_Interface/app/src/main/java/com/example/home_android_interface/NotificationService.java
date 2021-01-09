package com.example.home_android_interface;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.e(TAG , "onCreate") ;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy") ;
        super.onDestroy();
    }
    final Handler handler = new Handler();

    public  void startTimer() {
        timer = new Timer();

    }

    public void stopTimerTask(){

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

    private void createNotification(){

    }
}


