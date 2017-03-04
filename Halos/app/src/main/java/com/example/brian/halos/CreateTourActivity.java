package com.example.brian.halos;

import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class CreateTourActivity extends AppCompatActivity {

    protected String TAG = "create-tour-activity";

    protected static Tour mTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);

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
    }
}
