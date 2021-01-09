package com.example.home_android_interface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Login extends Activity implements MqttCallback {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        TextView username = findViewById(R.id.username);
        TextView passWord = findViewById(R.id.passWord);
        TextView signin = findViewById(R.id.signin);
        Button login = findViewById(R.id.login);
        Gson gson = new Gson();
        MqttClient client = null;


        //Connection
        try {
            client = new MqttClient("tcp://smart-mqtthive.duckdns.org:1883", "android_app", new MemoryPersistence());
            client.setCallback(this); // listener for messages
            client.connect();
            String topic = "web/statistics/user"; //listening this topic
            client.subscribe(topic); // listen to that topic
        } catch (MqttException e) {
            e.printStackTrace();
        }


        final MqttClient finalClient = client; //onchecked method can only use final values

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] userLogin = {String.valueOf(username.getText()), String.valueOf(passWord.getText())};

                try {
                    publish(finalClient, "web/request/user",
                            String.valueOf(new MqttMessage(gson.toJson(userLogin).getBytes())));
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                switchActivities();

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSignIn(v);
            }
        });

    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        Gson gson = new Gson();
        String[] payload = gson.fromJson(message.toString(), String[].class);
        String name = payload[0];
        System.out.println("##########################");
        System.out.println(topic + " " + name); // received from broker(server)

        if (name.equals("null")) {
            System.out.println("invalid user credentials.");

        } else if (!name.equals("null")){
            System.out.println("valid user");
            switchActivities();
        }

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

    private void switchActivities() {
        Intent switchactivities = new Intent(this, MainActivity.class);
        startActivity(switchactivities);
    }


    public void switchToSignIn(View view) {
        Intent switchactivities = new Intent(this, UserRegistration.class);
        startActivity(switchactivities);
    }
}