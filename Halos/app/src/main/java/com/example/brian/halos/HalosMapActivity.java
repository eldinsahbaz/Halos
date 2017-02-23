package com.example.brian.halos;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
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



public class HalosMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private OkHttpClient client = new OkHttpClient();

    private android.location.LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private android.location.Location location;

    private double lat;
    private double lng;

    private long minTime = 1000;
    private float minDistance = 0;

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

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new HalosLocationlistener();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.e("LOCATION: ", location.toString());

        String url = "http://10.0.2.2:12344/places";
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // create an array of Locations from the json response
                // hash each location ID to see if it is in our database, if it is not then add it

                Log.i("TAG", response.body().string());
            }
        });

        // TODO: Make an array or list of location objects for all places with given parameters
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


        //  TODO: for each location in the prviously created array or list of locations
        //      new LatLng = location from JSON
        //      title = name from JSON
        //      addMarker(name)
        //      moveCamera(name)
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
            case R.id.activity_settings:
                Intent intent4 = new Intent(this, SettingsActivity.class);
                startActivity(intent4);
                return true ;
            case R.id.checkout:
                Intent intent5 = new Intent(this, Checkout_Store.class);
                startActivity(intent5);
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

    private class HalosLocationlistener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(android.location.Location location) {
            if(location != null){
                lat = location.getLatitude();
                lng = location.getLongitude();

                Log.e("HalosMapActivity.java", "Latitude: " + lat);
                Log.e("HalosMapActivity.java", "Longitude: " + lng);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}

