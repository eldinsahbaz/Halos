package com.example.brian.halos;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.location.Location;
import android.location.LocationManager;
import java.text.DateFormat;
import java.util.Date;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.widget.Toast;
import org.json.JSONObject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
 import com.google.android.gms.location.LocationListener;
 import com.google.android.gms.location.LocationRequest;
 import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.tls.OkHostnameVerifier;


/**
 * This class is to generate a map and tour route based on the Tour the User created in the
 * HalosMap Activity. It retrieves the tour's landmarks from the tour object passed
 * from the previous activity and builds a url with it to send a http request to Google
 * Directions Api which returns a Json Object that is parsed with
 * DirectionJSonParser class to get a route with all the landmarks fitted onto the Map.
 */


public class TourMapActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected String TAG = "tour-map-activity";
    protected String mDirectionsResponse;
    private LatLng origin;
    private LatLng destination;
    protected static Tour mTour;
    private static final String DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String API_KEY = "AIzaSyD3KjHahrdF7B-e2C0h-IbRae1o7DSbYXI";

    // map used for this activity
    protected GoogleMap mMap;

    // maps marker for users current location
    protected Marker mCurrentLocationMarker;

    // marker currently being clicked
    protected Marker mClickedLocationMarker;

    // need a location manager to handle location requests, and provider to get the location
    protected LocationManager locationManager;
    protected String provider;

    // create tour object for when user adds locations to tour
    protected User user;
    protected LinkedList<Landmark> mTourList = new LinkedList<Landmark>();

    private OkHttpClient client = new OkHttpClient();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    String usernameSave;
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates = true;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    // return value used for server call
    protected String mRetVal;
    protected String mGeoAddr;


    /**
     * set of locations currently on map
     */
    protected HashMap<String, Landmark> mLocsOnMapSet;


    ArrayList<LatLng> markerpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
        //Set up the toolbar and map support.
        setContentView(R.layout.activity_tour_map);
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
        Button endtour;

        //End the Tour and return to home activity.
        endtour = (Button)findViewById(R.id.tour_map_canceltour);
        endtour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeactivity = new Intent(getApplicationContext(),HalosMapActivity.class);
                String username2 = getIntent().getStringExtra("username");
                homeactivity.putExtra("username",username2);
                startActivity(homeactivity);
            }
        });
        //Save the tour by passing it to CreateTourActivity
        Button createtour = (Button)findViewById(R.id.tour_map_save);
        createtour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createtour = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("TourObject", mTour);
                createtour.putExtras(bundle);
                createtour.setClass(getApplicationContext(),CreateTourActivity.class);
                String username3 = getIntent().getStringExtra("username");
                createtour.putExtra("username",username3);
                startActivity(createtour);
            }
        });
        //Obtain the Tour passed from HalosMapActivity
        markerpoints = new ArrayList<LatLng>();
        Bundle b = this.getIntent().getExtras();
        if (b != null)
            mTour = b.getParcelable("Tour");


        if (mTour == null) {
            Log.e(TAG, "Tour object not passed by intent");
        } else if (mTour.landmarks.size() == 0) {
            Log.e(TAG, "Tour has no landmarks associated");
        } else {
            for (int i = 0; i < mTour.landmarks.size(); i++) {
                Log.v(TAG, String.valueOf(mTour.landmarks.get(i).getName()));
            }
        }

        LatLng final_stop = new LatLng(mTour.landmarks.getLast().getLatitude(),mTour.landmarks.getLast().getLongitude());
        LatLng beginning = new LatLng(mTour.landmarks.getFirst().getLatitude(),mTour.landmarks.getFirst().getLongitude());

        //Formatting the directions request by building the url
        //using all the landmarks and current location.
        String url = getDirectionsUrl();
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);


                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }

            Log.e("UpdateValueFromBundle", mCurrentLocation.toString());
            updateUI();
            Log.e("UpdateValueFromBundle X", mCurrentLocation.toString());
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();

        Log.i(TAG, "GoogleApiClient Finished Building");
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
//        Log.e("mGoogleApiClient", mGoogleApiClient.toString());
//        Log.e("mLocationRequest", mLocationRequest.toString());
//        Log.e("this", this.toString());
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.e("TourMapActivity", e.getMessage());
        }
    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {
        Log.e(TAG, mCurrentLocation.toString());
        Log.e(TAG, "LNG: " + String.valueOf(mCurrentLocation.getLongitude()));
        Log.e(TAG, "LAT: " + String.valueOf(mCurrentLocation.getLatitude()));

        // get lat and long for current location
        // TODO: harcoding values below works, getting values from mCurrentLocation doesn't
        // makes no sense because they are the exact same values and types
        // but blank map activity shows up when using mCurrentLocation
        double currentLatitude = (double) mCurrentLocation.getLatitude();        //43.0392;
        double currentLongitude = (double) mCurrentLocation.getLongitude();      //-76.1351;
        LatLng currentLocation = new LatLng(
                currentLatitude,
                currentLongitude);

        // remove the old location marker
        mCurrentLocationMarker.remove();

        // update the marker location on the map
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        markerOptions.title("Current Location");
        markerOptions.snippet("You are here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mCurrentLocationMarker = mMap.addMarker(markerOptions);

        Log.e(TAG, "Maps Markers Updated");
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // need to request initial locations
        startLocationUpdates();

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            try {

                if (LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) == null) {
                    // will likely fail to this the first time the emulator starts because there is no location in the emulator
                    // thus we give it a default location (Syracuse University)
                    mCurrentLocation = new Location("");
                    mCurrentLocation.setLatitude(43.0392);
                    mCurrentLocation.setLongitude(-76.3351);
                }
                else {
                    // Gets the last known location on the device
                    mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    Log.e(TAG, LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).toString());
                }

                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                Log.e(TAG, DateFormat.getTimeInstance().format(new Date()).toString());

                Log.e("OnConnected", mCurrentLocation.toString());
                updateUI();
            } catch (SecurityException e) {
                Log.e("LocationActivity", e.getMessage());
            }
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
//        if (mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.e("OnLocationChanged Start", mCurrentLocation.toString());
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        Log.e("OnLocationChanged Done", mCurrentLocation.toString());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    //    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    //    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    //    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
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

            double currentLatitude = 43.0392;
            double currentLongitude = -76.130772;
            LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);

            if (mCurrentLocation != null) {
                currentLatitude = (double) mCurrentLocation.getLatitude();        //43.0392;
                currentLongitude = (double) mCurrentLocation.getLongitude();      //-76.130772;
                currentLocation = new LatLng(
                        currentLatitude,
                        currentLongitude);
            }

            if (mCurrentLocationMarker != null) {
                mCurrentLocationMarker.remove();
            }

            // update the marker location on the map
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title("Current Location");
            markerOptions.snippet("You are here");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            mCurrentLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

            // Add a marker in Sydney and move the camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            //Workarounds right now -remove when parser works
            origin = new LatLng(mTour.landmarks.get(0).getLatitude(), mTour.landmarks.get(0).getLongitude());
            destination = origin;

            for (int i = 0; i < mTour.landmarks.size(); i++) {
                LatLng landmarkLoc = new LatLng(mTour.landmarks.get(i).getLatitude(), mTour.landmarks.get(i).getLongitude());

                Log.v(mTour.landmarks.get(i).getName(), String.valueOf(mTour.landmarks.get(i).getLatitude()) + "," + String.valueOf(mTour.landmarks.get(i).getLongitude()));

                if (i == 0) {

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


