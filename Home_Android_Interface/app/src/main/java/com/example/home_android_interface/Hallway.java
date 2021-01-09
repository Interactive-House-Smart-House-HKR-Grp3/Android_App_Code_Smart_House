package com.example.home_android_interface;



import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

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

public class Hallway extends Fragment implements MqttCallback {

    private ImageView fan;
    private ImageView window;
    private ImageView door;
    private Switch fanswitch;
    private TextView fanstate;
    private TextView windowstate;
    private TextView doorstate;
    private LivingRoom livingRoom;


    public Hallway() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hallway, container, false);

        fan = view.findViewById(R.id.fan);
        window = view.findViewById(R.id.window);
        door = view.findViewById(R.id.door);
        fanstate = view.findViewById(R.id.fanstate);
        windowstate = view.findViewById(R.id.windowstate);
        doorstate = view.findViewById(R.id.doorstate);
        fanswitch = view.findViewById(R.id.fanswicth);

        MqttClient client = null;


        try {
            client = new MqttClient("tcp://smart-mqtthive.duckdns.org:1883", "android_app11", new MemoryPersistence());
            client.setCallback(this); // listener for messages
            client.connect();
            String topic = "smart_house/gui/fan"; //listening this topic
            client.subscribe("smart_house/gui/window");
            client.subscribe("smart_house/gui/door");
            client.subscribe(topic); // listen to that topic
        } catch (MqttException e) {
            e.printStackTrace();
        }


        final MqttClient finalClient = client; //onchecked method can only use final values

        fanswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    try {
                        publish(finalClient, "smart_house/cmd/fan", "true");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        publish(finalClient, "smart_house/cmd/fan", "false");
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
                        fanswitch.setChecked(switchCheck);

                    }
                });
            }
        });
        th.start();
    }

    private void setTextThread2(final TextView textView, final String text) {
        Thread th = new Thread(new Runnable() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textView.setText(text);

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
            case "smart_house/gui/fan":
                if (Boolean.parseBoolean(message.toString())) {
                    setTextThread(fanstate, "ON", true);
                } else {
                    setTextThread(fanstate, "OFF", false);
                }
                break;
            case "smart_house/gui/window":
                if (Boolean.parseBoolean(message.toString())) {
                    window.setImageResource(R.drawable.windowopen);
                    setTextThread2(windowstate,payload.toUpperCase());
                } else {
                    window.setImageResource(R.drawable.windowclose);
                    setTextThread2(windowstate, payload.toUpperCase());
                }
                break;

            case "smart_house/gui/door":
                if (Boolean.parseBoolean(message.toString())) {
                    door.setImageResource(R.drawable.dooropen);
                    setTextThread2(doorstate, payload.toUpperCase());
                } else {
                    door.setImageResource(R.drawable.doorclosed);
                    setTextThread2(doorstate , payload.toUpperCase());
                }
                break;

        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

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

}

