package com.example.brian.halos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ForwardingListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *  Class for user's Profile which contains a recycleview the data retrieved from the server
 *  about all the tour's this user has every bought or created. It also handles retrieving
 *  a tour's data from the database if the user clicks on a cardview of a tour and recreates that
 *  tour by passing a copy of that Tour to TourMapActivity_User class.
 */
public class UserProfileActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    String usernameSave;

    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    Profile_Adapter adapter;

    List<String> createdTours = new ArrayList<String>();
    List<String> BoughtTours = new ArrayList<String>();
    String passtoServerTour="";
    protected LinkedList<Landmark> TourList = new LinkedList<Landmark>();
    private OkHttpClient client = new OkHttpClient();
    protected static final String TAG = "User Location";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected Boolean mRequestingLocationUpdates = true;
    protected String mLastUpdateTime;
    protected String mRetVal;
    protected String mGeoAddr;
    Marker mCurrentLocationMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Set up toolbar and retreieve data from Server and intent.
        Toolbar toolbar = (Toolbar)findViewById(R.id.menu);
        setSupportActionBar(toolbar);
        usernameSave = getIntent().getStringExtra("username");
        TextView name = (TextView) findViewById(R.id.userProfileName);
        name.setText(usernameSave);
        createdTours.add("Test Sample");
        getProfile profile = new getProfile();
        profile.execute();
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
        recyclerView = (RecyclerView) findViewById(R.id.RecycleView_UserProfile);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Profile_Adapter(getApplicationContext(),createdTours);
        recyclerView.setAdapter(adapter);

        //Handle On-click event when user wants to take a Tour he created or Bought.
        //Passes Tour_Id to the class constructor.
        adapter.SetTourListener(new Profile_Adapter.TourclickListerner() {
            @Override
            public void userTourClick(View view, int position) {
                passtoServerTour = createdTours.get(position);
                getTourID tourID = new getTourID(passtoServerTour);
                tourID.execute();

            }
        });

    }

    public boolean onCreateOptionsMenu ( Menu menu ) {
        getMenuInflater().inflate(R.menu.toolbar,menu );
        return true ;
    }

    //Methods for allows User to travel to different parts of the App
    //when using the Toolbar.
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


    //Class to retrieve data of the User about the tours they bought or created
    //using a Json Get request with their username as part of the request.

    private class getProfile extends AsyncTask<Void,Void,String> {
        OkHttpClient client = new OkHttpClient();
        String retVal;

        protected getProfile(){
        }

        @Override
        protected String doInBackground(Void... voids) {

            Request request = new Request.Builder()
                    // if you want to run on local use http://10.0.2.2:12344
                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344

                    .url("http://lcs-vc-esahbaz.syr.edu:12344/get_tour_by_user?username="+usernameSave)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();



            //Client that adds all the tours in database to the local arraylist of tours.
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Server Failure Response", call.request().body().toString());
                    retVal = "cannot connect to server";
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseData = response.body().string();
                    Log.v("userprofile", "onResponse:" + responseData);
                    retVal = "success";


                    try {
                          Log.v("Try","here")    ;
                        createdTours.clear();
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject respObject = jsonObject.getJSONObject("response");

                        try {
                            JSONArray rep = respObject.getJSONArray("created");
                            for (int i = 0; i < rep.length() ; i++){
                                String tourname = rep.getString(i);
                                createdTours.add(tourname);
                            }
                        }catch (Exception e){
                            Log.e("Error", "nothing in created");
                        }

                        try {
                            JSONArray rep2 = respObject.getJSONArray("bought");
                            Log.v("Result","Got result array from Json object");
                            for (int i = 0; i < rep2.length() ; i++){
                                String tourname2 = rep2.getString(i);
                                createdTours.add(tourname2);
                            }
                        }catch (Exception e2){
                            Log.e("Error", "nothing in bought");
                        }

                    } catch (Exception e){
                        Log.e("User_Profile", "Exception Thrown: " + e);
                    }

                }


            });
            return retVal;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO: Must check that the location was processed to the database before making announcement
            //Log.d("RESULT", result);
            //  Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}


    }

    //Class that retrieves a Json Array from the server Database containing 2 string of all
    // the Latitudes and Longitudes of all the landmarks which is parsed to add to the local
    // Tour Object and passed to the TourMapActivity_User to generate the tour.

    private class getTourID extends AsyncTask<Void,Void,String> {
        OkHttpClient client = new OkHttpClient();
        String retVal;
        String id;

        protected getTourID(String tour_name){
            id = tour_name;
        }

        @Override
        protected String doInBackground(Void... voids) {


        Request request = new Request.Builder()
                // if you want to run on local use http://10.0.2.2:12344
                // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344

                .url("http://lcs-vc-esahbaz.syr.edu:12344/get_created?tour_id="+id)
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

                    String responseData = response.body().string();
                    Log.v("userprofile", "onResponse:" + responseData);
                    retVal = "success";


                    try {
                        //createdTours.clear();
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject respObject = jsonObject.getJSONObject("response");
                        JSONArray rep = respObject.getJSONArray("result");
                        Log.v("Result","Got result array from Json object");
                        JSONObject list = new JSONObject();
                        list = rep.getJSONObject(0);
                        String lat = list.getString("Lat");
                        Log.d("LatitudeCheck",lat);
                        String[] Lats = lat.substring(1, lat.length()-1).split(",");
                        String longitude = list.getString("Long");
                        Log.d("LongitudeCheck",longitude);
                        String[] Longs = longitude.substring(1, longitude.length()-1).split(",");
                        Log.e("User_Check_lat", lat);

                        for (int i = 0; i < Longs.length ; i++)
                        {
                            Log.d("LongitudeCheck2",Longs[i]);
                            Landmark landmark = new Landmark();
                            landmark.setLatitude(Double.valueOf(Lats[i]));
                            landmark.setLongitude(Double.valueOf(Longs[i]));
                            TourList.add(landmark);
                        }
                        for (int i =0; i < TourList.size() ; i++){
                            Log.v("TourList-Contain", ""+ TourList.get(i).getLatitude());
                            Log.v("TourList2-Contain", ""+ TourList.get(i).getLongitude());
                        }
                    } catch (Exception e){
                        Log.e("User_Profile", "Exception Thrown: " + e);
                    }

                }


            });
            return retVal;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent tourProfile = new Intent(getApplicationContext(),TourMapActivity_User.class);
            tourProfile.putExtra("username", usernameSave);
            Tour mTour = new Tour();
            mTour.addLandmarks(TourList);
            Landmark currLoc = new Landmark("Current Location", 0, true, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mTour.landmarks.addFirst(currLoc);
            Log.v("current location", ""+mCurrentLocation.getLatitude()+ " long: " + mCurrentLocation.getLatitude());
            for (int i =0; i < TourList.size() ; i++){
                Log.v("TourList2-Contain", ""+ TourList.get(i).getLatitude());
                Log.v("TourList2-Contain", ""+ TourList.get(i).getLongitude());
            }

            if (mTour.landmarks.size() > 1) {
                Bundle b = new Bundle();
                b.putParcelable("Tour", mTour);
                tourProfile.putExtras(b);
                startActivity(tourProfile);
            }
            else {
               // Toast.makeText(getApplicationContext(),"Tour Doesn't Exist", Toast.LENGTH_SHORT).show();
                Log.d("MapActivty", "tour empty");
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}


    }
    ///////////////////////////////////////////////////////////////////////


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


        // update the marker location on the map
        // now get all of the places of interest withing users radius

        Log.e(TAG, "Maps Markers Updated");
    }

    /**
     *
     */

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


    @Override
    public void onBackPressed() {
    }

}
