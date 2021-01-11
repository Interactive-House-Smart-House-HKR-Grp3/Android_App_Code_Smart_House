package com.example.home_android_interface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MainActivity extends AppCompatActivity implements MqttCallback {

    private ActionBarDrawerToggle toggle;
    private TextView indoortemp ;
    private TextView outdoortemp ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        indoortemp = findViewById(R.id.indoortemp);
        outdoortemp = findViewById(R.id.outdoortemp);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navView);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MqttClient client = null;



        try {
            client = new MqttClient("tcp://smart-mqtthive.duckdns.org:1883", "android_app10", new MemoryPersistence());
            client.setCallback(this); // listener for messages
            client.connect();
            client.subscribe("smart_house/gui/temp_outdoor");
            client.subscribe("smart_house/gui/temp_indoor");
            client.subscribe("smart_house/gui/hbreak_alarm");
            client.subscribe("smart_house/gui/leakage");
            client.subscribe("smart_house/gui/fire_alarm");
            client.subscribe("smart_house/gui/power_cut");

        } catch (MqttException e) {
            e.printStackTrace();
        }




        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuitem1:
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuitem2:
                    Intent anIntent = new Intent(getApplicationContext(), TabActivity.class);
                    startActivity(anIntent);
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuitem3:
                    Intent intent1 = new Intent(getApplicationContext(), DirectWebpage.class);
                    startActivity(intent1);
                    drawerLayout.closeDrawers();
                    return true;


            }
            return true;
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    private void setTextThread(final TextView textView, final String text) {
        Thread th = new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textView.setText(text);


                    }
                });
            }
        });
        th.start();
    }

    private void setAlarm( final String alarm) {
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

        payload = payload.substring(0,2) + "°C";
        switch (topic){
            case "smart_house/gui/temp_indoor":
                setTextThread(indoortemp,payload);
                break;

            case "smart_house/gui/temp_outdoor":
                setTextThread(outdoortemp,payload);
                break;
            case "smart_house/gui/fire_alarm":
                setAlarm("Fire ");
                break;
            case "smart_house/gui/hbreak_alarm":
                setAlarm("House Break-In ");
                break;
            case "smart_house/gui/leakage":
                setAlarm("Water Leakage ");
                break;
            case "smart_house/gui/power_cut":
                setAlarm("Power Cut ");
                break;
        }

    }



    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }




}