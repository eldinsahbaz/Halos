package com.example.brian.halos;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateAccountActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();

    Button createAccountButton;

    EditText mEmail;
    EditText mUsername;
    EditText mPassword1;
    EditText mPassword2;

    String retVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mEmail = (EditText) findViewById(R.id.create_email);
        mUsername = (EditText) findViewById(R.id.create_username);
        mPassword1 = (EditText) findViewById(R.id.create_password);
        mPassword2 = (EditText) findViewById(R.id.verify_password);

        createAccountButton = (Button) findViewById(R.id.btn_add_account);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);

                String username = mUsername.getText().toString();
                String password = mPassword1.getText().toString();
                String email = mEmail.getText().toString();

                User user = new User(username, password, email);

                if (mPassword1.getText().toString().equals(mPassword2.getText().toString())) {
                    // CreateAccount cannot be run on main thread -> see code below, it extends AsyncTask
                    Account account = new Account(user);
                    account.execute();
                }
                else {
                    Toast.makeText(CreateAccountActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class Account extends AsyncTask<Void, Void, String> {
        User user;

        protected Account(User u) {
            user = u;
        }

        @Override
        protected String doInBackground(Void... params) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Map<String, String> json_params = new HashMap<String, String>();
            json_params.put("username", user.getName());
            json_params.put("password", user.getPassword());
            json_params.put("email", user.getEmail());
            json_params.put("radius", Integer.toString(user.getRadius()));
            // TODO: need to have an id associated and maybe other things (travelled, guided, etc + cookies, ip, etc)
            // TODO: need to encrypt data going over the wire

            JSONObject json_parameter = new JSONObject(json_params);
            RequestBody json_body = RequestBody.create(JSON, json_parameter.toString());
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:12344/login/new")
                    .post(json_body)
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
                    String usernameTaken = "username is taken";
                    String emailTaken = "email is already linked to an account";
                    String accountCreated = "account created successfully";

                    // get the response data from the server
                    String responseData = response.body().string();

                    Log.e("LoginActivity.java", "onResponse:" + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject respObject = jsonObject.getJSONObject("response");
                        String result = respObject.getString("result");

                        retVal = result;

                        if (result.equals(accountCreated)) {
                            Log.e("LoginActivity.java", "result: " + result);

                            Intent i = new Intent(getApplicationContext(), HalosMapActivity.class);
                            startActivity(i);
                        } else if (result.equals(emailTaken)){
                            Log.e("LoginActivity.java: " + result, emailTaken);
                            retVal = result;
                        } else if (result.equals(usernameTaken)) {
                            Log.e("LoginActivity.java: " + result, usernameTaken);
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
            Toast.makeText(CreateAccountActivity.this, result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}