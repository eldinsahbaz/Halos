package com.example.brian.halos;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

//        TextView welcomeMsg = (TextView) findViewById(R.id.welcome_message);
//        String username = findViewById(R.id.input_email).toString();
//        welcomeMsg.setText("Welcome " + username);
    }

}
