package com.example.home_android_interface;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class OutdoorDevices extends Fragment implements MqttCallback {

    private ImageView outdoorlight;
    private Switch outdoorswitch;
    private TextView ourdoorstate;
    private LivingRoom livingRoom;

    public OutdoorDevices() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_outdoor_devices, container, false);

        outdoorlight = view.findViewById(R.id.outdoorlamp);
        outdoorswitch = view.findViewById(R.id.outdoorswitch);
        ourdoorstate = view.findViewById(R.id.outdoorstate);

        MqttClient client = null;


        try {
            client = new MqttClient("tcp://smart-mqtthive.duckdns.org:1883", "android_app4", new MemoryPersistence());
            client.setCallback(this); // listener for messages
            client.connect();
            String topic = "smart_house/gui/outdoor_light"; //listening this topic
            client.subscribe(topic); // listen to that topic

        } catch (MqttException e) {
            e.printStackTrace();
        }


        final MqttClient finalClient = client; //onchecked method can only use final values

        outdoorswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    try {
                        publish(finalClient, "smart_house/cmd/outdoor_light", "true");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        publish(finalClient, "smart_house/cmd/outdoor_light", "false");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        return view;
    }
    private void setTextThread(final TextView textView, final String text, final boolean switchCheck) {
        Thread th = new Thread(new Runnable() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textView.setText(text);
                        outdoorswitch.setChecked(switchCheck);

                    }
                });
            }
        });
        th.start();
    }

    @Override
    public void connectionLost(Throwable cause) {

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
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        System.out.println("##########################");
        System.out.println(topic + " " + payload); // received from broker(server)


        switch (topic) {
            case "smart_house/gui/outdoor_light":
                if (Boolean.parseBoolean(message.toString())) {
                    outdoorlight.setImageResource(R.drawable.outdooron);
                    setTextThread(ourdoorstate, "ON", true);
                } else {
                    outdoorlight.setImageResource(R.drawable.outdooroff);
                    setTextThread(ourdoorstate, "OFF", false);
                }
                break;
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}