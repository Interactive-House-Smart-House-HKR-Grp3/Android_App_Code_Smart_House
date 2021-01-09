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

