package com.example.brian.halos;

import android.os.AsyncTask;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;


// Google Maps API Key AIzaSyCGlh3TOI8yioBEDhR9Scr6RlZMokqF6js

public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    Button createAccountButton;
    Button forgotPwButton;

    EditText username;
    EditText password;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.input_username);
        password = (EditText) findViewById(R.id.input_password);

        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AUTHENTICATE HERE
                Intent i = new Intent(getApplicationContext(), HalosMapActivity.class);
                startActivity(i);

//                Account account = new Account(username.getText().toString(), password.getText().toString());
//                account.execute();
                //Toast.makeText(LoginActivity.this, "FIRED LOGIN", Toast.LENGTH_SHORT).show();

                // if login is succesful, get user info
//                TODO: parse user info to User object
            }
        });

        createAccountButton = (Button) findViewById(R.id.btn_create_account);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This takes user to create account activity
                Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(i);
            }
        });

        forgotPwButton = (Button) findViewById(R.id.btn_forgot_password);
        forgotPwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to retrieve password activity
                Intent i = new Intent(getApplicationContext(), RetrievePasswordActivity.class);
                startActivity(i);
            }
        });

    }

    private class Account extends AsyncTask<Void, Void, String> {
        String username;
        String password;

        protected Account(String u, String p) {
            username = u;
            password = p;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: need to have an id associated and maybe other things (cookies, ip, etc)
            // TODO: need to encrypt data going over the wire
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:12344/login/" + username)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Server Failure Response", call.request().body().toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("response", response.body().string());
                    if (response.toString() == "Attempt login for " + username) {
                        Log.e("User authenticated:", username);
                    }
                    // parse response
                }

            });
            return "ok";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO: Must check that the location was processed to the database before making announcement
            Toast.makeText(LoginActivity.this, "Account Created", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
