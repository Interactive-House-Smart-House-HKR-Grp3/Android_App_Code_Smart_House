package com.example.home_android_interface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class UserRegistration extends Activity implements MqttCallback {

    private String ACCOUNT_NAME; //login credential
    private String PASSWORD; //login credential
    private String NAME;
    private String EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        TextView accountName = findViewById(R.id.accountName);
        TextView password = findViewById(R.id.password);
        TextView userName = findViewById(R.id.name);
        TextView emailAddress = findViewById(R.id.email);
        Button register = findViewById(R.id.register);
        Gson gson = new Gson();
        MqttClient client = null;
        //Connection
        try {
            client = new MqttClient("tcp://smart-mqtthive.duckdns.org:1883", "android_app", new MemoryPersistence());
            client.setCallback(this); // listener for messages
            client.connect();
            String topic = "web/statistics/registration"; //listening this topic
            client.subscribe(topic); // listen to that topic
        } catch (MqttException e) {
            e.printStackTrace();
        }


        final MqttClient finalClient = client; //onchecked method can only use final values


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] userCredentials = {String.valueOf(accountName.getText()), String.valueOf(password.getText()), String.valueOf(userName.getText()),
                        String.valueOf(emailAddress.getText())};
                if (accountName.getText().equals("") || password.getText().equals("") || userName.getText().equals("") || emailAddress.getText().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill in the user credentials", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        publish(finalClient, "web/request/register_user", String.valueOf(new MqttMessage(gson.toJson(userCredentials).getBytes())));
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }


            }
        });


    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        System.out.println("##########################");
        System.out.println(topic + " " + payload); // received from broker(server)


        switch (topic) {
            case "web/statistics/registration":
                if (Boolean.parseBoolean(message.toString())) {
                    System.out.println("registration successful");
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                } else {
                    System.out.println("user already exits.");
                }
                break;
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

}