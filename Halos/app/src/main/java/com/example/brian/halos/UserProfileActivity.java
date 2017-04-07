package com.example.brian.halos;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserProfileActivity extends AppCompatActivity {
    String usernameSave;
    String retVal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar)findViewById(R.id.menu);
        setSupportActionBar(toolbar);
        usernameSave = getIntent().getStringExtra("username");

        UserProfileRequest userRequest = new UserProfileRequest(User.getName());
        userRequest.execute();
    }

    public boolean onCreateOptionsMenu ( Menu menu ) {
        getMenuInflater().inflate(R.menu.toolbar,menu );
        return true ;
    }

    private class UserProfileRequest extends AsyncTask<Void, Void, String> {
        String username;

        protected UserProfileRequest(String u) {
            username = u;
        }


        @Override
        protected String doInBackground(Void... params) {

            Log.e("Username: ", User.getName());

            // TODO: need to encrypt data going over the wire
            Request request = new Request.Builder()
                    // if you want to run on local use http://10.0.2.2:12344
                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
                    .url("http://lcs-vc-esahbaz.syr.edu:12344/get_tour_by_user?username=" + username)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();

            OkHttpClient client = new OkHttpClient();
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

                    Log.e("UserProfileActivity", "onResponse:" + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray respArray = jsonObject.getJSONArray("response");
                        JSONObject respObject = respArray.getJSONObject(0);
                        String result = respObject.getString("result");

                        Log.e("SERVER RESULT", result);

                        retVal = result;

                    } catch (Exception e){
                        retVal = e.toString();
                        Log.e("UserProfile Exception", retVal);
                    }
                }

            });

            return retVal;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("UserProfileActivity", " retVal: " + retVal);
            Toast.makeText(UserProfileActivity.this, retVal, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch( item.getItemId() ) {
            case R.id.Home:
                Intent intent1 = new Intent(this, HalosMapActivity.class);
                intent1.putExtra("username", usernameSave);
                startActivity(intent1);
                return true ;
            case R.id.store:
                Intent intent2 = new Intent(this, StoreActivity.class);
                intent2.putExtra("username", usernameSave);
                startActivity(intent2);
                return true ;
            case R.id.profile:
                Intent intent3 = new Intent(this, UserProfileActivity.class);
                intent3.putExtra("username", usernameSave);
                startActivity(intent3);
                return true ;
            case R.id.activity_settings:
                Intent intent4 = new Intent(this, SettingsActivity.class);
                intent4.putExtra("username", usernameSave);
                startActivity(intent4);
                return true ;
            case R.id.Logout:
                Intent intent6 = new Intent(this, LoginActivity.class);
                startActivity(intent6);
                return true ;

            default :
                // If we got here , the user ’s action was not recognized .
                // Invoke the superclass to handle it .
                return super.onOptionsItemSelected(item);
        }
    }
    // TODO: Add functionality to search for users; Ray said something about using fragments instead of TabView for the tabs?
    // Tabs wont show because they don't have any activities within them. Need to program in either placeholder "dummy" activities for the tabs?

}
