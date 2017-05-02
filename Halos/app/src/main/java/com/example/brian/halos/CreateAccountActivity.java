package com.example.brian.halos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import org.json.JSONObject;
import android.support.v7.app.AlertDialog;
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

/*
    This class allows Users to create an account which will be registered in the server
    and checks if username already exists or not within the server.
 */
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

                String username = mUsername.getText().toString();
                String password = mPassword1.getText().toString();
                String email = mEmail.getText().toString();

                User user = new User(username, password, email);

                if (username.equals("") || password.equals("") || email.equals("")) {
                    Toast.makeText(CreateAccountActivity.this, "Please fill out all fields", Toast.LENGTH_LONG).show();
                }
                else if (mPassword1.getText().toString().equals(mPassword2.getText().toString()) && !password.equals("")) {


                if (username.equals("") || password.equals("") || email.equals("")) {
//                    AlertDialog alertDialog = new AlertDialog.Builder(CreateAccountActivity.this).create();
//                    alertDialog.setTitle("Missing Field");
//                    alertDialog.setMessage("Please fill out all fields before creating account");
//                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    alertDialog.show();
                    Toast.makeText(CreateAccountActivity.this, "Please fill out all fields", Toast.LENGTH_LONG).show();
                }

                else if (mPassword1.getText().toString().equals(mPassword2.getText().toString()) && !password.equals("")) {
                    // CreateAccount cannot be run on main thread -> see code below, it extends AsyncTask

                    Log.e("account created", username);
                    Account account = new Account(user);
                    account.execute();
                }
            }
        }
    });
    }

    private class Account extends AsyncTask<Void, Void, String> {
        User user;
        String username;
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
            username = user.getName();
            JSONObject json_parameter = new JSONObject(json_params);
            RequestBody json_body = RequestBody.create(JSON, json_parameter.toString());
            Request request = new Request.Builder()
                    // if you want to run on local use http://10.0.2.2:12344
                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
                    .url("http://lcs-vc-esahbaz.syr.edu:12344/login/new")
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

                    Log.e("CreateAccountActivity", "onResponse:" + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject respObject = jsonObject.getJSONObject("response");
                        String result = respObject.getString("result");

                        retVal = result;

                        if (result.equals(accountCreated)) {
                            Log.e("CreateAccountActivity", "result: " + result);

                            Intent i = new Intent(getApplicationContext(), HalosMapActivity.class);
                            i.putExtra("username",username);
                            startActivity(i);
                        } else if (result.equals(emailTaken)){
                            Log.e("CreateAccountActivity" + result, emailTaken);
                            retVal = result;
                        } else if (result.equals(usernameTaken)) {
                            Log.e("CreateAccountActivity" + result, usernameTaken);
                            retVal = result;
                        }

                    } catch (Exception e){
                        Log.e("CreateAccountActivity", "Exception Thrown: " + e);
                        retVal = e.toString();
                    }

                }


            });
            return retVal;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO: Must check that the location was processed to the database before making announcement
           // Toast.makeText(CreateAccountActivity.this, result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}