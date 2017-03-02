package com.example.brian.halos;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {
    Button submitButton;
    EditText radius;
    String retVal;
    OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        radius = (EditText) findViewById(R.id.radius_input);

        submitButton = (Button) findViewById(R.id.settings_submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), HalosMapActivity.class);
                startActivity(i);
            }
        });
    }

    private class Setting extends AsyncTask<Void, Void, String> {
        String username;
        String password;

        protected Setting(String u, String p) {
            username = u;
            password = p;
        }


        @Override
        protected String doInBackground(Void... params) {
            // TODO: need to have an id associated and maybe other things (cookies, ip, etc)
            // TODO: need to encrypt data going over the wire
            Request request = new Request.Builder()
                    // if you want to run on local use http://10.0.2.2:12344
                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
                    .url("http://lcs-vc-esahbaz.syr.edu:12344/login/auth?user=" + username + "&pw=" + password)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Server Failure Response", call.request().body().toString());
                    retVal = "cannot connect to server";
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // get the response data from the server
                    String responseData = response.body().string();
                    String correctResponse =  "login successful";

                    Log.e("LoginActivity.java", "onResponse:" + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject respObject = jsonObject.getJSONObject("response");
                        String result = respObject.getString("result");

                        retVal = result;

                        if (result.equals(correctResponse)) {
                            Log.e("LoginActivity.java", "result: " + result);

                            Intent i = new Intent(getApplicationContext(), HalosMapActivity.class);
                            startActivity(i);
                        } else {
                            Log.e("LoginActivity.java: " + result, correctResponse);
                            retVal = result;
                        }

                    } catch (Exception e){
                        Log.e("LoginActivity.java", "Exception Thrown: " + e);
                        retVal = e.toString();
                    }
                }

            });

            return retVal;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO: Must check that the location was processed to the database before making announcement
            // TODO: toast is always one action behind? maybe try on real phone
            Toast.makeText(SettingsActivity.this, result.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
