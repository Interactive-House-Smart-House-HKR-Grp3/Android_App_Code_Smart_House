package com.example.home_android_interface;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Notification extends Activity implements MqttCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.noti);
        MqttClient client = null;


        try {
            client = new MqttClient("tcp://smart-mqtthive.duckdns.org:1883", "android_app5", new MemoryPersistence());
            client.setCallback(this); // listener for messages
            client.connect();
            client.subscribe("smart_house/gui/hbreak_alarm");
            client.subscribe("smart_house/gui/leakage");
            client.subscribe("smart_house/gui/fire_alarm"); // listen to that topic

        } catch (
                MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }
    private void setTextThread( final String alarm) {
        Thread th = new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        addNotification(alarm);


                    }
                });
            }
        });
        th.start();
    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        String payload = new String(message.getPayload());
        System.out.println("##########################");
        System.out.println(topic + " " + payload); // received from broker(server)

        switch (topic){
            case "smart_house/gui/fire_alarm":
                setTextThread("Fire ");
                break;
            case "smart_house/gui/hbreak_alarm":
                setTextThread("House Break-In ");
                break;
            case "smart_house/gui/leakage":
                setTextThread("Water Leakage ");
                break;
        }


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public void addNotification(String alarmtype) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID , name , importance);
        channel.setDescription(Description);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        channel.setShowBadge(false);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.heateron) //set icon for notification
                        .setContentTitle(alarmtype) //set title of notification
                        .setContentText(alarmtype+"Alarm is activated in your house!")//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        Intent notificationIntent = new Intent(this, NotificationView.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", alarmtype+"Alarm is activated in your house please check it immediately!");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification

        manager.notify(0, builder.build());
    }
}
