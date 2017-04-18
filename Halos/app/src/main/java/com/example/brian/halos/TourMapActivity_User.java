package com.example.brian.halos;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class is to generate a map and tour route based on the Tour the User Clicked in the
 * UserProfile Activity. It retrieves the tour's landmarks from the server based on
 * the unique tour_id through a Json Get request and builds a url with the data retrieved
 * to send a http request to Google Directions Api which returns a Json Object that is parsed with
 * DirectionJSonParser class to get a route with all the landmarks fitted onto the Map.
 */


public class TourMapActivity_User extends AppCompatActivity implements OnMapReadyCallback {
    protected String mDirectionsResponse;
    private LatLng origin;
    private LatLng destination;
    protected Tour mTour;
    private static final String DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String API_KEY = "AIzaSyD3KjHahrdF7B-e2C0h-IbRae1o7DSbYXI";
    protected String TAG = "USer-tour-map-activity";
    private GoogleMap mMap;
    ArrayList<LatLng> markerpoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TourMapActivityUSer","HERE");
        setContentView(R.layout.activity_tour_map__user);
        Button finish;
        //Button to finish the tour and return home.
        finish = (Button)findViewById(R.id.finish_tour);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(getApplicationContext(),HalosMapActivity.class);
                String username2 = getIntent().getStringExtra("username");
                profile.putExtra("username",username2);
                startActivity(profile);
            }
        });

        //Retrieve Tour from previous activity.
        markerpoints = new ArrayList<LatLng>();
        Bundle b = this.getIntent().getExtras();
        if (b != null)
            mTour = b.getParcelable("Tour");

       Log.v("Tour Length",""+mTour.landmarks.size() );
        for (int i = 0; i< mTour.landmarks.size() ; i++) {
            Log.v("Tour", "Lat:"+ mTour.landmarks.get(i).latitude);
        }
        String url = getDirectionsUrl();
        Log.v("URL LENGTH",""+url.length());
        Log.v("URL",url);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        Log.v(TAG, "Arrived");
    }


    //Method for formating the Url.

    private String getDirectionsUrl() {

        // Origin of route
        String str_origin = "origin=" + mTour.landmarks.getFirst().latitude + "," + mTour.landmarks.getFirst().longitude;

        // Destination of route
        String str_dest = "destination=" + mTour.landmarks.getLast().latitude + "," + mTour.landmarks.getLast().longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        String waypoints = "waypoints=optimize:true|";

        for(int i = 1; i < mTour.landmarks.size()-1; i++)
            waypoints += String.valueOf(mTour.landmarks.get(i).latitude) + "," + String.valueOf(mTour.landmarks.get(i).longitude+"|");

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + waypoints + "&" + sensor + "&" + API_KEY;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    //Method that uses an Http connection to download the the directions after passing the Url.

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();
        } catch (Exception e) {
            Log.d("Exception ", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    //Class that parses the data given by the directions API.
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    //Class that parses the directions through the DirectionsJsonParser class
    //and fits the route onto the Google Map fragment.
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String As = "";

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    As += ""+lat+lng;
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);
            }
            // Drawing polyline in the Google Map for the i-th route
            Log.v(TAG, As);
            if (lineOptions != null)
            {
                mMap.addPolyline(lineOptions);
            }
        }

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
    //@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        styleMap();

        // Add a marker in Sydney and move the camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //Workarounds right now -remove when parser works
        origin = new LatLng(mTour.landmarks.get(0).getLatitude(), mTour.landmarks.get(0).getLongitude());
        destination = origin;

        for (int i = 0; i < mTour.landmarks.size(); i++) {
            LatLng landmarkLoc = new LatLng(mTour.landmarks.get(i).getLatitude(), mTour.landmarks.get(i).getLongitude());

            Log.v(mTour.landmarks.get(i).getName(), String.valueOf(mTour.landmarks.get(i).getLatitude()) + "," + String.valueOf(mTour.landmarks.get(i).getLongitude()));

            if (i == 0) {
                mMap.addMarker(new MarkerOptions().position(landmarkLoc).title(mTour.landmarks.get(i).getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(landmarkLoc));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                //Workarounds right now -remove when parser works

            } else {
                mMap.addMarker(new MarkerOptions().position(landmarkLoc).title(mTour.landmarks.get(i).getName()));
                //Workarounds right now -remove when parser works
            }
        }

        //DirectionsRequest directionsRequest = new DirectionsRequest(mTour);
        //directionsRequest.execute();

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
                            getApplicationContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
                return true;
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        return false;
    }


    //Server-side code that wasn't implemented to retrieve directions
    //from the server (which made the call) rather than making a front-end
    //making an http request to google for the directions.

    private class DirectionsRequest extends AsyncTask<Void, Void, String> {
        String origin;
        String waypoints = "none";
        String destination;
        String mode = "walking";        // TODO: add in settings to select walking or driving
        String sensor = "sensor=false";
        String output = "json";

        OkHttpClient client = new OkHttpClient();

        protected String DirectionsRequest(Tour t) {
            try {
                if (t.landmarks.size() == 2) {
                    origin = t.landmarks.get(0).getLatitude() + "," + t.landmarks.get(0).getLongitude();
                    destination = t.landmarks.get(1).getLatitude() + "," + t.landmarks.get(1).getLongitude();
                } else {
                    for (int i = 0; i < t.landmarks.size(); i++) {
                        if (i == 0) {
                            origin = t.landmarks.get(i).getLongitude() + "," + t.landmarks.get(i).getLatitude();
                        } else if (i == t.landmarks.size() - 1) {
                            destination = t.landmarks.get(i).getLongitude() + "," + t.landmarks.get(i).getLatitude();
                        } else {
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

            String parameters=origin+"&"+destination+"&"+waypoints+"&"+sensor;
            String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
            return url;
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

                    } catch (Exception e) {
                        Log.e(TAG, "Exception Thrown: " + e);
                        mDirectionsResponse = e.toString();
                    }
                }

            });

            return mDirectionsResponse;
        }


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
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    @Override
    public void onBackPressed() {
    }
}
