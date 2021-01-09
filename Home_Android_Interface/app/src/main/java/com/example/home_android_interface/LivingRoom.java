package com.example.home_android_interface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class LivingRoom extends Fragment implements MqttCallback {


    public LivingRoom() {
        // Required empty public constructor
    }

    private ImageView bulbon;
    private ImageView heateron;
    private ImageView stoveon;
    private Switch bulbswitch;
    private Switch heaterswitch;
    private TextView bulbstate;
    private TextView heaterstate;
    private TextView stovestate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_living_room, container, false);
        bulbon = view.findViewById(R.id.light);
        heateron = view.findViewById(R.id.heater);
        stoveon = view.findViewById(R.id.stove);
        bulbswitch = view.findViewById(R.id.bulbswicth);
        heaterswitch = view.findViewById(R.id.heaterswsitch);
        bulbstate = view.findViewById(R.id.lightstate);
        heaterstate = view.findViewById(R.id.heaterstate);
        stovestate = view.findViewById(R.id.stovestate);


        MqttClient client = null;


        try {
            client = new MqttClient("tcp://smart-mqtthive.duckdns.org:1883", "android_app1", new MemoryPersistence());
            client.setCallback(this); // listener for messages
            client.connect();
            String topic = "smart_house/gui/indoor_light"; //listening this topic
            client.subscribe("smart_house/gui/heating_in");
            client.subscribe(topic); // listen to that topic

        } catch (MqttException e) {
            e.printStackTrace();
        }


        final MqttClient finalClient = client; //onchecked method can only use final values
        bulbswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    try {
                        publish(finalClient, "smart_house/cmd/indoor_light", "true");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        publish(finalClient, "smart_house/cmd/indoor_light", "false");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        heaterswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    try {
                        publish(finalClient, "smart_house/cmd/heating_in", "true");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        publish(finalClient, "smart_house/cmd/heating_in", "false");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        return view;
    }

    public void publish(MqttClient client, String topic, String message) throws MqttException {
        if (!client.isConnected()) {
            client.connect();
        }
        try {
            client.publish(topic,
                    new MqttMessage(message.getBytes())); //string  to byte array
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }


    private void setTextThread(final TextView textView, final String text, final boolean switchCheck) {
        Thread th = new Thread(new Runnable() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textView.setText(text);
                        bulbswitch.setChecked(switchCheck);
                    }
                });
            }
        });
        th.start();
    }


    @Override
    public void messageArrived(String topic, final MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        System.out.println("##########################");
        System.out.println(topic + " " + payload); // received from broker(server)


        switch (topic) {
            case "smart_house/gui/heating_in":
                if (Boolean.parseBoolean(message.toString())) {
                    heateron.setImageResource(R.drawable.heateron);
                    setTextThread(heaterstate, "ON", false);

                } else {
                    heateron.setImageResource(R.drawable.heateroff);
                    setTextThread(heaterstate, "OFF", false);
                }
                break;
            case "smart_house/gui/indoor_light":
                if (Boolean.parseBoolean(message.toString())) {
                    bulbon.setImageResource(R.drawable.lighton);
                    setTextThread(bulbstate, "ON", true);
                } else {
                    bulbon.setImageResource(R.drawable.lightoff);
                    setTextThread(bulbstate, "OFF", false);
                }
                break;


            case "smart_house/gui/stove":
                stovestate.setText(payload.toUpperCase());
                break;

        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }


}