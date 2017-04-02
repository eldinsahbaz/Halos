package com.example.brian.halos;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateTourActivity extends AppCompatActivity {
    EditText mTourname;
    TextView mTourCreator;
    EditText mTourDescription;
    EditText mPrice;
    OkHttpClient client = new OkHttpClient();
    String retVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);

        Button cancel = (Button)findViewById(R.id.create_tour_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createtour = new Intent(getApplicationContext(),HalosMapActivity.class);
                String username2 = getIntent().getStringExtra("username");
                createtour.putExtra("username",username2);
                startActivity(createtour);
            }
        });
        final String username3 = getIntent().getStringExtra("username");
        mTourname = (EditText)findViewById(R.id.tour_id);
        mTourDescription= (EditText)findViewById(R.id.tour_description);
        mTourCreator = (TextView)findViewById(R.id.tour_creator);
        mPrice = (EditText)findViewById(R.id.tour_price);
        mTourCreator.setText(username3);

        Button create = (Button)findViewById(R.id.create_tour);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tourname = mTourname.getText().toString();
                String tourcreator = username3;
                String tour_description = mTourDescription.getText().toString();
                String tour_price = mPrice.getText().toString();

                Tour passtour = new Tour();
                Bundle b = getIntent().getExtras();
                if (b != null) {
                    passtour = b.getParcelable("TourObject");
                } else {
                    Log.d("Bug","bundle is null");
                }

                if( (tourname.length() == 0) || (tourcreator.length() == 0) || (tour_description.length() == 0) || (tour_price.length() == 0) ){
                    Toast.makeText(getApplicationContext(), "Missing Inputs", Toast.LENGTH_LONG).show();
                }else {
                    passtour.setName(tourname);
                    passtour.setCreator(tourcreator);
                    try {
                        passtour.setPrice(Double.valueOf(tour_price));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    passtour.setDescription(tour_description);
                    TourCreation creation = new TourCreation(passtour);
                    creation.execute();
                    Intent i = new Intent(getApplicationContext(), HalosMapActivity.class);
                    String username2 = getIntent().getStringExtra("username");
                    i.putExtra("username",username2);
                    startActivity(i);
                }
            }
        });



    }


    private class TourCreation extends AsyncTask<Void,Void,String> {
        Tour tour;

        protected TourCreation(Tour create){
            tour=create;
        }

        @Override
        protected String doInBackground(Void... voids) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Map json_params = new HashMap<String, String>();
            json_params.put("tourid", tour.getName());
            json_params.put("price", tour.getPrice());
            json_params.put("description",tour.getDescription());
            json_params.put("created-by",tour.getCreator());
            String Lat = "";
            String Long= "";

            for(int i=0; i < tour.getLandmarks().size() ; i++){
                Landmark point = tour.getLandmarks().get(i);
                Lat = Lat + Double.toString(point.getLatitude())+ "|";
                Long = Long+ Double.toString(point.getLongitude())+"|";
            }
            String Lat2 = Lat.substring(0, Lat.length() - 1);
            String Long2 = Long.substring(0, Long.length() -1);

            json_params.put("Lat", Lat2 );
            json_params.put("Long",Long2);

            // TODO: need to have an id associated and maybe other things (travelled, guided, etc + cookies, ip, etc)
            // TODO: need to encrypt data going over the wire

            JSONObject json_parameter = new JSONObject(json_params);
            RequestBody json_body = RequestBody.create(JSON, json_parameter.toString());
            Request request = new Request.Builder()
                    // if you want to run on local use http://10.0.2.2:12344
                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
                    .url("http://lcs-vc-esahbaz.syr.edu:12344/addtour")
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

                    String tourcreated = "tour successfully created";
                    String tour_name_taken = "tour by this name already exists";

                    // get the response data from the server
                    String responseData = response.body().string();

                    Log.e("TourCreationActivity", "onResponse:" + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject respObject = jsonObject.getJSONObject("response");
                        String result = respObject.getString("result");

                        retVal = result;

                        if (result.equals(tourcreated)) {
                            Log.e("TourCreationActivity", "result: " + result);
                        } else if (result.equals(tour_name_taken)){
                            Log.e("TourCreationActivity: " + result, tour_name_taken);
                            retVal = result;
                        }

                    } catch (Exception e){
                        Log.e("TourCreationActivity", "Exception Thrown: " + e);
                        retVal = e.toString();
                    }
                }


            });
            return retVal;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO: Must check that the location was processed to the database before making announcement
            Toast.makeText(CreateTourActivity.this, result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}


    }


    @Override
    public void onBackPressed() {
    }
}
