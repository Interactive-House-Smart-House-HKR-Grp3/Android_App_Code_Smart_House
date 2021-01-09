package com.example.home_android_interface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);


        Button login = findViewById(R.id.login);
        Button signup = findViewById(R.id.signup);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivities();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivities2();
            }
        });

    }

    private void switchActivities() {
        Intent switchactivities = new Intent(this, Login.class);
        startActivity(switchactivities);
    }

    private void switchActivities2() {
        Intent switchactivities = new Intent(this, UserRegistration.class);
        startActivity(switchactivities);
    }


}