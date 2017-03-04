package com.example.brian.halos;

import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class TourMapActivity extends FragmentActivity implements OnMapReadyCallback {

    protected String TAG = "tour-map-activity";

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

        for(int i = 0; i < mTour.landmarks.size(); i++) {
            Landmark l = new Landmark();
            l = mTour.landmarks.get(i);
            Log.v("Tour Values", l.getName());
            Log.v("\t\t", String.valueOf(l.getLatitude()));
            Log.v("\t\t", String.valueOf(l.getLongitude()));

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
            mMap.addMarker(new MarkerOptions().position(landmarkLoc).title(mTour.landmarks.get(i).getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(landmarkLoc));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        }
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
}
