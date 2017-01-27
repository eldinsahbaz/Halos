package com.example.brian.halos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


// Google Maps API Key AIzaSyCGlh3TOI8yioBEDhR9Scr6RlZMokqF6js


public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    Button createAccountButton;

    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);

        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AUTHENTICATE HERE
                Intent i = new Intent(getApplicationContext(), HalosMapActivity.class);
                startActivity(i);
            }
        });

        createAccountButton = (Button) findViewById(R.id.btn_create_account);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CREATE ACCOUNT HERE
                Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(i);
            }
        });


    }
}
