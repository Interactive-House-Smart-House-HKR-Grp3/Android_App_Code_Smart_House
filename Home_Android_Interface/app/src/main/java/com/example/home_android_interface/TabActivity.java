package com.example.home_android_interface;


import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TabActivity extends MainActivity {


    TabLayout tabLayout;
    ViewPager viewPager;
    private TextView eleconsumption ;
    MqttClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.test, frameLayout);

        eleconsumption = findViewById(R.id.eleconsumption);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Living Room"));
        tabLayout.addTab(tabLayout.newTab().setText("Hallway"));
        tabLayout.addTab(tabLayout.newTab().setText("Outdoor"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final MyAdapter adapter = new MyAdapter(this, getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        try {
            client = new MqttClient("tcp://smart-mqtthive.duckdns.org:1883", "android_app", new MemoryPersistence());
            client.setCallback(this); // listener for messages
            client.connect();
            client.subscribe("smart_house/gui/el_consumption");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void setTextThread(final TextView textView, final String text) {
        Thread th = new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textView.setText(text+" kW");


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


        switch (topic) {
            case "smart_house/gui/el_consumption":
                setTextThread(eleconsumption , payload);
                break;

        }
    }

}
