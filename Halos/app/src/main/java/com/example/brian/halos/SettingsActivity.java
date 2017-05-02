package com.example.brian.halos;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/*
This class stores User's preferences including the types of places they want from the Google
Places Api and tracks the radius they want to get locations from in miles which the User can change.
 */
public class SettingsActivity extends AppCompatActivity {
    Button submitButton;
    String usernameSave;
    TextView radiusText;
    SeekBar radiusBar;
    int radius;

    Spinner category;
    Spinner rankBy;
    Spinner openNow;
    Spinner travelMode;

    EditText keyword;
    EditText minPrice;
    EditText maxPrice;

    String retVal;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        usernameSave= getIntent().getStringExtra("username");
        //Setup Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.menu);
        setSupportActionBar(toolbar);

        radiusBar = (SeekBar) findViewById(R.id.settings_radius_bar);
        radiusBar.setProgress(User.getRadius()*10);
        radiusText = (TextView) findViewById(R.id.setting_radius_text);
        radiusText.setText(User.getRadius() + " Miles");
        radius = radiusBar.getProgress();

        category = (Spinner) findViewById(R.id.setting_type_drop_menu);
        rankBy = (Spinner) findViewById(R.id.setting_rank_drop_menu);
        openNow = (Spinner) findViewById(R.id.setting_opennow_drop_menu);
        travelMode = (Spinner) findViewById(R.id.setting_mode_drop_menu);

        keyword = (EditText) findViewById(R.id.setting_keyword_enter_text);
        keyword.setText(User.getKeyword());
        minPrice = (EditText) findViewById(R.id.setting_minprice_enter_text);
        minPrice.setText(String.valueOf(User.getMinPrice()));
        maxPrice = (EditText) findViewById(R.id.setting_maxprice_enter_text);
        maxPrice.setText(String.valueOf(User.getMaxPrice()));

        submitButton = (Button) findViewById(R.id.settings_submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsRequest settingsRequest = new SettingsRequest(  User.getName(),
                                                                        User.getRadius(),
                                                                        User.getCategory(),
                                                                        User.getRankBy(),
                                                                        Boolean.valueOf(User.getOpenNow()),
                                                                        User.getKeyword(),
                                                                        User.getMinPrice(),
                                                                        User.getMaxPrice());
                settingsRequest.execute();

                Intent i = new Intent(getApplicationContext(), HalosMapActivity.class);
                i.putExtra("username", usernameSave);
                startActivity(i);
            }
        });

        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = User.getRadius()*10;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                radiusText.setText(progress/10 + " Miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v("SeekBar", "Selected: " + progress);
                try {
                    User.setRadius(progress/10);
                }catch (Exception e) {
                    Log.e("SeekBar radius changed", e.getMessage());
                }
            }
        });

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                User.setCategory(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rankBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                User.setRankBy(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        openNow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                User.setOpenNow(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        travelMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                User.setMode(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        keyword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
//                // you can call or do what you want with your EditText here
//                User.setKeyword(s.toString());
//                Toast.makeText(SettingsActivity.this, s.toString() + " is your new keyword", Toast.LENGTH_SHORT).show();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // you can call or do what you want with your EditText here
                User.setKeyword(s.toString());
            }
        });

        minPrice.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
//                // you can call or do what you want with your EditText here
//                try {
//                    User.setMinPrice(Double.valueOf(s.toString()));
//                    Toast.makeText(SettingsActivity.this, s.toString() + " is your new keyword", Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//
//                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // you can call or do what you want with your EditText here
                try {
                    User.setMinPrice(Double.valueOf(s.toString()));
                } catch (Exception e) {

                }
            }
        });

        maxPrice.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // you can call or do what you want with your EditText here
//                try {
//                    User.setMaxPrice(Double.valueOf(s.toString()));
//                    Toast.makeText(SettingsActivity.this, s.toString() + " is your new keyword", Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//
//                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // you can call or do what you want with your EditText here
                try {
                    User.setMaxPrice(Double.valueOf(s.toString()));
                } catch (Exception e) {

                }
            }
        });
    }

    public boolean onCreateOptionsMenu ( Menu menu ) {
        getMenuInflater().inflate(R.menu.toolbar,menu );
        return true ;
    }

    private class SettingsRequest extends AsyncTask<Void, Void, String> {
        String username;
        int radius;
        String category;
        String rankBy;
        boolean openNow;
        String keyword;
        double minPrice;
        double maxPrice;

        protected SettingsRequest(String u,
                                  int r,
                                  String c,
                                  String rb,
                                  boolean o,
                                  String k,
                                  double minP,
                                  double maxP) {

            username = u;
            radius = r;
            category = c;
            rankBy = rb;
            openNow = o;
            keyword = k;
            minPrice = minP;
            maxPrice = maxP;

        }


        @Override
        protected String doInBackground(Void... params) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Map<String, String> json_params = new HashMap<String, String>();
            json_params.put("username", username);
            json_params.put("radius", String.valueOf(radius));
            json_params.put("category", String.valueOf(category));
            json_params.put("rankBy", String.valueOf(rankBy));
            json_params.put("openNow", String.valueOf(openNow));
            json_params.put("keyword", String.valueOf(keyword));
            json_params.put("minprice", String.valueOf(minPrice));
            json_params.put("maxprice", String.valueOf(maxPrice));
            Log.e("RADIUS", User.getRadius() + "\t" + radius);
            // TODO: need to have an id associated and maybe other things (travelled, guided, etc + cookies, ip, etc)
            // TODO: need to encrypt data going over the wire

            JSONObject json_parameter = new JSONObject(json_params);
            RequestBody json_body = RequestBody.create(JSON, json_parameter.toString());
//            Request request = new Request.Builder()
//                    // if you want to run on local use http://10.0.2.2:12344
//                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
//                    .url("http://lcs-vc-esahbaz.syr.edu:12344/get_places")
//                    .post(json_body)
//                    .addHeader("content-type", "application/json; charset=utf-8")
//                    .build();

            // TODO: need to have an id associated and maybe other things (cookies, ip, etc)
            // TODO: need to encrypt data going over the wire
            Request request = new Request.Builder()
                    // if you want to run on local use http://10.0.2.2:12344
                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
                    .url("http://lcs-vc-esahbaz.syr.edu:12344/set_settings")
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
                    // get the response data from the server
                    String responseData = response.body().string();

                    Log.e("SettingsActivity.java", "onResponse:" + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject respObject = jsonObject.getJSONObject("response");
                        String result = respObject.getString("result");

                        retVal = result;

                    } catch (Exception e){
                        Log.e("SettingsActivity.java", "Exception Thrown: " + e);
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
            Toast.makeText(getApplicationContext(), retVal, Toast.LENGTH_LONG).show();
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
    @Override
    public void onBackPressed() {
    }

}
