package com.example.brian.halos;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.*;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;


/**
 * Getting Location Updates.
 *
 * Demonstrates how to use the Fused Location Provider API to get updates about a device's
 * location. The Fused Location Provider is part of the Google Play services location APIs.
 *
 * For a simpler example that shows the use of Google Play services to fetch the last known location
 * of a device, see
 * https://github.com/googlesamples/android-play-location/tree/master/BasicLocation.
 *
 * This sample uses Google Play services, but it does not require authentication. For a sample that
 * uses Google Play services for authentication, see
 * https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart.
 */

/*
    this class is our "home" page of our application which displays points of interest around you
    usings Google places API retreieved from our server. It tracks your location and displays it on
    the map.
 */

public class HalosMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener  {

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
    protected Tour mTour = new Tour();
    protected User user;
    protected LinkedList<Landmark> mTourList = new LinkedList<Landmark>();

    private OkHttpClient client = new OkHttpClient();

    // give a tag for debugging purposes
    protected static final String TAG = "Location Updates";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000000;

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

    Button startTourBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Holds all locations on map with the key being their id (stored as snippet on marker)
        mLocsOnMapSet = new HashMap<>();
        mTour.landmarks.clear();
        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();

        setContentView(R.layout.activity_halos_map);
        usernameSave= getIntent().getStringExtra("username");
        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.menu);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startTourBtn = (Button) findViewById(R.id.start_tour_btn);
        startTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTourList.size() != 0 && mTourList != null) {
                    Toast.makeText(HalosMapActivity.this, "Enjoy Your Tour!", Toast.LENGTH_SHORT).show();

                    // Create tour and add list of landmarks to the tour
                    Tour mTour = new Tour();
                    mTour.addLandmarks(mTourList);

                    Landmark currLoc = new Landmark("Current Location", 0, true, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    mTour.landmarks.addFirst(currLoc);

                    // This takes user to create tour activity
                    Intent i = new Intent();
                    Bundle b = new Bundle();
                    b.putParcelable("Tour", mTour);
                    i.putExtras(b);
                    String username2 = getIntent().getStringExtra("username");
                    i.setClass(HalosMapActivity.this, TourMapActivity.class);
                    i.putExtra("username", username2);
                    startActivity(i);

                } else {
                    Toast.makeText(HalosMapActivity.this, "No Landmarks Added to Tour", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            Log.e("LocationActivity", e.getMessage());
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

        // Move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

        // now get all of the places of interest withing users radius
        getNearbyPlaces();

        Log.e(TAG, "Maps Markers Updated");
    }

    /**
     *
     */
    protected void getNearbyPlaces() {
        // This is the API call
        PlacesRequest placesRequest = new PlacesRequest();
        placesRequest.execute();

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

//        // initialize new OkHttpClient for rest calls when location changes
//        client = new OkHttpClient();
//
//        // fire the call to the server
//        PlacesRequest placesRequest = new PlacesRequest();
//        placesRequest.execute();

//        Toast.makeText(this, "Location Updated",
//                Toast.LENGTH_SHORT).show();
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

        styleMap();

        Log.e(TAG, "Map is Ready");

        double currentLatitude = 0.0;
        double currentLongitude = 0.0;
        LatLng currentLocation;

        if (mCurrentLocation == null) {
            currentLatitude = 40.4406;
            currentLongitude = -79.9959;
            currentLocation = new LatLng(
                    currentLatitude,
                    currentLongitude);
        } else {
            currentLatitude = mCurrentLocation.getLatitude();
            currentLongitude = mCurrentLocation.getLongitude();
            currentLocation = new LatLng(
                    currentLatitude,
                    currentLongitude);
        }

        // Add a marker in current location and move the camera
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        markerOptions.title("Current Location");
        markerOptions.snippet("You Are Here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

    }

    public void addPlaces(double lat, double lng, String name, String id) {
        // format this places location
        LatLng currentLocation = new LatLng(lat,lng);

        // add this places location marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        markerOptions.title(name);
        markerOptions.snippet(id);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(markerOptions);

        /**
         * handle marker click event
         */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    if (marker.getTitle().equals("Current Location")) {
                        // DO NOT SHOW INFO WINDOW FOR USER'S LOCATION -- MAYBE ADD LATER BUT NEED DIFFERENT WINDOW
                        return true;
                    }

                    mClickedLocationMarker = marker;
                    marker.showInfoWindow();
                    Log.i(TAG, "Info Window triggered on " + mClickedLocationMarker.getTitle() + "\tID: " + mClickedLocationMarker.getSnippet());
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Info Window cannot be display");
                    return false;
                }
            }

        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker selectMarker) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker selectMarker) {
                Log.i(TAG, "Info Window triggered on " + selectMarker.getTitle()+  "\tID: " + selectMarker.getSnippet());
                View v = getLayoutInflater().inflate(R.layout.landmark_pop_up, null);

                // get the position of the marker selected
                LatLng latLng = selectMarker.getPosition();

                // move camera to center on the selected marker
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(14));

                TextView pName = (TextView) v.findViewById(R.id.popup_name);
                TextView pRating = (TextView) v.findViewById(R.id.popup_rating);
                TextView pType = (TextView) v.findViewById(R.id.popup_type);
                TextView pAddress = (TextView) v.findViewById(R.id.popup_address);

                Log.i(TAG, selectMarker.getSnippet());
                Landmark clickedLandmark = mLocsOnMapSet.get(selectMarker.getSnippet());
                Log.i(TAG, "clicked landmark data " + mClickedLocationMarker.getTitle()+  "\tID: " + mClickedLocationMarker.getSnippet() + "\tID:" + selectMarker.getSnippet());

                v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                pName.setText(clickedLandmark.getName());
                pRating.setText(String.valueOf(clickedLandmark.getRating()));
                pType.setText(clickedLandmark.getTypes());

                GeocodeRequest geoRequest = new GeocodeRequest(clickedLandmark.getLatitude(), clickedLandmark.getLongitude());
                geoRequest.execute();

                // Hold for time to update result
                try {
                    Log.i("Geocode API", "Waiting for process to catch up");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                pAddress.setText(mGeoAddr);

                return v;
            }
        });

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                //stuff goes here
                Landmark toAdd = mLocsOnMapSet.get(mClickedLocationMarker.getSnippet());
                if (mTourList.contains(toAdd)) {
                    mTourList.remove(toAdd);
                    Log.v(TAG, toAdd.getName() + " has been removed from the tour");
                    Toast.makeText(HalosMapActivity.this, toAdd.getName() + " Removed to Tour", Toast.LENGTH_SHORT).show();
                } else {
                    if(mTourList.size() < 9) {
                        mTourList.add(toAdd);
                        Log.v(TAG, toAdd.getName() + " has been added to the tour");
                        Toast.makeText(HalosMapActivity.this, toAdd.getName() + " Added to Tour", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.v(TAG, "max tour size reached,current size = "+ mTourList.size());
                        Toast.makeText(HalosMapActivity.this, "Max landmarks selected already.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public boolean onCreateOptionsMenu ( Menu menu ) {
        getMenuInflater().inflate(R.menu.toolbar,menu );
        return true ;
    }

    // this handles the call to the server
    private class PlacesRequest extends AsyncTask<Void, Void, String> {
        User user;

        OkHttpClient client = new OkHttpClient();

        protected PlacesRequest() {

        }

        @Override
        protected String doInBackground(Void... params) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Map<String, String> json_params = new HashMap<String, String>();
            json_params.put("lat", String.valueOf(mCurrentLocation.getLatitude()));
            json_params.put("lng", String.valueOf(mCurrentLocation.getLongitude()));
            json_params.put("radius", String.valueOf(User.getRadius()*1610));
            json_params.put("keyword", String.valueOf(User.getKeyword()));
            Log.e("HalosMap Radius", User.getRadius() + "\t");
            // TODO: need to have an id associated and maybe other things (travelled, guided, etc + cookies, ip, etc)
            // TODO: need to encrypt data going over the wire

            JSONObject json_parameter = new JSONObject(json_params);
            RequestBody json_body = RequestBody.create(JSON, json_parameter.toString());
            Request request = new Request.Builder()
                    // if you want to run on local use http://10.0.2.2:12344
                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
                    .url("http://lcs-vc-esahbaz.syr.edu:12344/get_places")
                    .post(json_body)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Server Failure Response", call.request().body().toString());
                    mRetVal = "cannot connect to server";

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // get the response data from the server
                    String responseData = response.body().string();

                    Log.v(TAG, "onResponse:" + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        final JSONArray resArray = jsonObject.getJSONArray("results");
//                        Log.v("resObject", resArray.toString());

                            // handler lets us run back on the UI thread
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // parse the places and add them to the map
                                    for (int i = 0; i < resArray.length(); i++) {
                                        try {
                                            String rating, openNow;
                                            JSONObject resObject = resArray.getJSONObject(i);
                                            JSONObject geoObject = resObject.getJSONObject("geometry");
                                            String name = resObject.get("name").toString();
                                            String id = resObject.getString("place_id");
                                            JSONArray typeArr = resObject.getJSONArray("types");
                                            String types = "";
                                            for (int k = 0; k < typeArr.length(); k++) {
                                                types += ", " + typeArr.get(k);
                                            }
                                            types = types.substring(1);
                                            try {
                                                rating = resObject.get("rating").toString();
                                            } catch (Exception e) {
                                                rating = "0.0";
                                            }
                                            JSONObject locObject = geoObject.getJSONObject("location");
                                            String lat = locObject.getString("lat");
                                            String lng = locObject.getString("lng");
                                            try {
                                                JSONObject hoursObject = resObject.getJSONObject("opening_hours");
                                                openNow = hoursObject.getString("open_now");
                                            } catch (Exception e) {
                                                openNow = "true";
                                            }
//                                            Log.v("resObject", name);
//                                            Log.v("resObject id", id);
//                                            Log.v("resObject", rating);
//                                            Log.v("locObject lat", lat);
//                                            Log.v("locObject lng", lng);
//                                            Log.v("hoursObject", openNow);
//                                            Log.v("----------------------", "new resObject " + i);
                                            Landmark currLoc = new Landmark(
                                                    name,
                                                    Double.valueOf(rating),
                                                    Boolean.valueOf(openNow),
                                                    Double.valueOf(lat),
                                                    Double.valueOf(lng),
                                                    types);
                                            mLocsOnMapSet.put(id, currLoc);
                                            addPlaces(Double.valueOf(lat), Double.valueOf(lng), name, id);
                                        } catch (Exception e) {
                                            Log.e("Location Parse Handler", e.getMessage());
//                                            Log.e("JSON DATA", resArray.toString());
                                        }
                                    }
                                }
                            });

                        mRetVal = responseData;

                    } catch (Exception e){
                        Log.e(TAG, "Exception Thrown: " + e);
                        mRetVal = e.toString();
                    }

                }


            });
            return mRetVal;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO: Must check that the location was processed to the database before making announcement
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private class GeocodeRequest extends AsyncTask<Void, Void, String> {
        String latitude;
        String longitude;

        protected GeocodeRequest(double lat, double lng) {
            latitude = String.valueOf(lat);
            longitude = String.valueOf(lng);
        }


        @Override
        protected String doInBackground(Void... params) {
            // TODO: need to have an id associated and maybe other things (cookies, ip, etc)
            // TODO: need to encrypt data going over the wire
            Request request = new Request.Builder()
                    // if you want to run on local use http://10.0.2.2:12344
                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
                    .url("http://lcs-vc-esahbaz.syr.edu:12344/geocode?lat=" + latitude + "&lng=" + longitude)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Server Failure Response", call.request().body().toString());
                    mGeoAddr = "cannot connect to server";
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // get the response data from the server
                    String responseData = response.body().string();

                    Log.e("Geocode API", "onResponse:" + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject respObject = jsonObject.getJSONObject("response");
                        String result = respObject.getString("result");

                        mGeoAddr = result;

                    } catch (Exception e){
                        Log.e(TAG, "Exception Thrown: " + e);
                        mRetVal = e.toString();
                    }
                }

            });

            return mGeoAddr;
        }

        @Override
        protected void onPostExecute(String result) {
            // Hold for time to update result
            try {
                Log.i("Geocode API", "Waiting for process to catch up");
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
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

