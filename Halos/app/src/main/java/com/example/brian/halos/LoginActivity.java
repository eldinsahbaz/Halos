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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
//
//import com.google.gson.JsonDeserializationContext;
//import com.google.gson.JsonDeserializer;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParseException;
/*
    This class allows users to login with their credentials or create an account on our server.
     We check the username and password if they choose to login with the server.
 */
public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    Button createAccountButton;
    Button forgotPwButton;

    EditText username;
    EditText password;

    OkHttpClient client = new OkHttpClient();

    String retVal = "Welcome to Halos";

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = new User();

        username = (EditText) findViewById(R.id.input_username);
        password = (EditText) findViewById(R.id.input_password);

        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Account account = new Account(username.getText().toString(), password.getText().toString());
                account.execute();
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

        //REMOVE BYPASSER BUTTON
//        Button bypasser =(Button) findViewById(R.id.bypasser);
//        bypasser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent map = new Intent(getApplicationContext(),HalosMapActivity.class);
//                startActivity(map);
//            }
//        });
    }

    // this is so the user cannot just go back to the app once they log out
    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
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
                    Log.e("Server Failure Response", e.getMessage());
                    retVal = "cannot connect to server";
                    Log.e("LoginActivity", retVal);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // get the response data from the server
                    String responseData = response.body().string();
                    String correctResponse =  "login successful";

                    Log.e("LoginActivity.java", "onResponse:" + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray respArray = jsonObject.getJSONArray("response");
                        JSONObject respObject = respArray.getJSONObject(0);
                        String result = respObject.getString("result");

                        Log.e("SERVER RESULT", result);

                        retVal = result;

                        if (result.equals(correctResponse)) {
                            user.setName(username);
                            user.setEmail(respObject.getString("email"));
                            user.setPassword(password);
                            user.setRadius(Integer.valueOf(respObject.getString("radius")));
                            // TODO: get other user info
                            Log.v("Login", "Loggin in " + user.getName());

                            Intent i = new Intent(getApplicationContext(), HalosMapActivity.class);
                            i.putExtra("username",username);
                            startActivity(i);
                        } else {
                            Log.e("LoginActivity.java: " + result, correctResponse);
                            retVal = result;
                            Log.e("LoginActivity", retVal);
                        }

                    } catch (Exception e){
                        retVal = e.toString();
                        Log.e("LoginActivity Exception", retVal);
                    }
                }

            });

            return retVal;
        }

        @Override
        protected void onPostExecute(String result) {
            // Hold for time to update result
            try {
                Log.i("LoginActivity", "Waiting for process to catch up");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("LoginActivity retVal", retVal);
            Toast.makeText(LoginActivity.this, retVal, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


}
