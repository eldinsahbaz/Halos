package com.example.brian.halos;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.tls.OkHostnameVerifier;

public class TourMapActivity extends FragmentActivity implements OnMapReadyCallback {

    protected String TAG = "tour-map-activity";
    protected String mDirectionsResponse;

    protected static Tour mTour;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_map);

        Bundle b = this.getIntent().getExtras();
        if (b != null)
            mTour = b.getParcelable("Tour");

        // ELDIN AND RAY -- THE TOUR OBJECT IS PASSED CORRECTLY AND IT IS STORED IN mTour, I believe that is all you need for the waypoints, but let me know if you need more
        if (mTour == null) {
            Log.e(TAG, "Tour object not passed by intent");
        } else if (mTour.landmarks.size() == 0) {
            Log.e(TAG, "Tour has no landmarks associated");
        } else {
            for (int i = 0; i < mTour.landmarks.size(); i++) {
                Log.v(TAG, String.valueOf(mTour.landmarks.get(i).getName()));
            }
        }

        Log.v(TAG, "Arrived");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        styleMap();

        // Add a marker in Sydney and move the camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        for (int i = 0; i < mTour.landmarks.size(); i++) {
            LatLng landmarkLoc = new LatLng(mTour.landmarks.get(i).getLatitude(), mTour.landmarks.get(i).getLongitude());

            Log.v(mTour.landmarks.get(i).getName(), String.valueOf(mTour.landmarks.get(i).getLatitude()) +"," + String.valueOf(mTour.landmarks.get(i).getLongitude()));

            if (i == 0) {
                mMap.addMarker(new MarkerOptions().position(landmarkLoc).title(mTour.landmarks.get(i).getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(landmarkLoc));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
            }
            else {
                mMap.addMarker(new MarkerOptions().position(landmarkLoc).title(mTour.landmarks.get(i).getName()));
            }
        }

        DirectionsRequest directionsRequest = new DirectionsRequest(mTour);
        directionsRequest.execute();
    }

    /**
     * Style the map in a cool way
     */
    private boolean styleMap() {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
                return true;
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        return false;
    }

    private class DirectionsRequest extends AsyncTask<Void, Void, String> {
        String origin;
        String waypoints = "none";
        String destination;
        String mode = "walking";        // TODO: add in settings to select walking or driving

        OkHttpClient client = new OkHttpClient();

        protected DirectionsRequest(Tour t) {
            try {
                if (t.landmarks.size() == 2) {
                    origin = t.landmarks.get(0).getLatitude() + "," + t.landmarks.get(0).getLongitude();
                    destination = t.landmarks.get(1).getLatitude() + "," + t.landmarks.get(1).getLongitude();
                }
                else {
                    for (int i = 0; i < t.landmarks.size(); i++) {
                        if (i == 0) {
                            origin = t.landmarks.get(i).getLongitude() + "," + t.landmarks.get(i).getLatitude();
                        }
                        else if (i == t.landmarks.size()-1) {
                            destination = t.landmarks.get(i).getLongitude() + "," + t.landmarks.get(i).getLatitude();
                        }
                        else {
//                            TODO: all all landmarks in list as waypoints
                            waypoints = waypoints + "|" + t.landmarks.get(i).getLongitude() + "," + t.landmarks.get(i).getLatitude();
                        }
                    }
                    waypoints = waypoints.substring(1);                 // remove leading '|'
                }

                Log.v("Directions Request", origin);
                Log.v("Directions Request", waypoints);
                Log.v("Directions Request", destination);
            } catch (Exception e) {
                Log.e("Directions Request", e.getMessage());
            }
        }


        @Override
        protected String doInBackground(Void... params) {
            // TODO: need to have an id associated and maybe other things (cookies, ip, etc)
            // TODO: need to encrypt data going over the wire
            Request request = new Request.Builder()
                    // if you want to run on local use http://10.0.2.2:12344
                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
                    .url("http://lcs-vc-esahbaz.syr.edu:12344/get_directions?origin=" + origin
                            + "&destination=" + destination
                            + "&waypoints=" + waypoints
                            + "&mode=" + mode)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Server Failure Response", call.request().body().toString());
                    mDirectionsResponse = "cannot connect to server";
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // get the response data from the server
                    String responseData = response.body().string();

                    Log.e("Geocode API", "onResponse:" + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);

//                        mDirectionsResponse = result;

                    } catch (Exception e){
                        Log.e(TAG, "Exception Thrown: " + e);
                        mDirectionsResponse = e.toString();
                    }
                }

            });

            return mDirectionsResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            // Hold for time to update result
            try {
                Log.i("Directions API", "Waiting for process to catch up");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
