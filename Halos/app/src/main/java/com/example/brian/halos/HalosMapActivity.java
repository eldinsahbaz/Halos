package com.example.brian.halos;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// PLACES API KEY FOR ANDROID: AIzaSyDCMZlOV0A3k7FnhD1OKX1zsUHH8kJ2VH0
//       WEB SERVICES API KEY: AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ


public class HalosMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halos_map);
        //Setup Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.menu);
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        OkHttpClient client = new OkHttpClient();

        int radius = 1000;
        String key = "AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ";
        String location = "43.0481,-76.1474";
        String type = "restaurant";
        String keyword = "italian";
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location
                + "&radius=" + radius
                + "&type=" + type
                + "&keyword=" + keyword
                + "&key=" + key;
        //String url = "http://10.0.2.2:5000/places";
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                       Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("TAG", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                       Toast.makeText(LoginActivity.this, response.body().string(), Toast.LENGTH_SHORT).show();
                // create an array of Locations from the json response
                // hash each location ID to see if it is in our database, if it is not then add it

                Log.i("TAG", response.body().string());
            }
        });

        // TODO: Make an array or list of location objects for all places with given parameters
    }

    public String putJson(String url, String json) throws IOException{
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String postJson(String url, String json) throws IOException{
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Syracuse, NY.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng syracuse = new LatLng(43.0481, -76.1474);
        mMap.addMarker(new MarkerOptions().position(syracuse).title("Marker in Syracuse, NY"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(syracuse));

        LatLng another_marker = new LatLng(43.0481, -77.1474);
        mMap.addMarker(new MarkerOptions().position(another_marker).title("Marker somewhere around Syracuse, NY"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(another_marker));

        //  TODO: for each location in the prviously created array or list of locations
        //      new LatLng = location from JSON
        //      title = name from JSON
        //      addMarker(name)
        //      moveCamera(name)
    }

    private class RunOffMainThread extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO: Toast saying task was executed
            // Must check that the location was processed to the database before making announcement
            Toast.makeText(HalosMapActivity.this, "Executed", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public boolean onCreateOptionsMenu ( Menu menu ) {
        getMenuInflater().inflate(R.menu.toolbar,menu );
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch( item.getItemId() ) {
            case R.id.Home:
                Intent intent1 = new Intent(this, HalosMapActivity.class);
                startActivity(intent1);
                return true ;
            case R.id.store:
                Intent intent2 = new Intent(this, StoreActivity.class);
                startActivity(intent2);
                return true ;
            case R.id.profile:
                Intent intent3 = new Intent(this, UserProfileActivity.class);
                startActivity(intent3);
                return true ;
            case R.id.Logout:
                Intent intent4 = new Intent(this, LoginActivity.class);
                startActivity(intent4);
                return true ;
            default :
// If we got here , the user ’s action was not recognized .
// Invoke the superclass to handle it .
                return super.onOptionsItemSelected(item);
        }
    }
}