package com.example.brian.halos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {
    Button submitButton;
    EditText radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        radius = (EditText) findViewById(R.id.radius_input);

        submitButton = (Button) findViewById(R.id.settings_submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AUTHENTICATE HERE


                Intent i = new Intent(getApplicationContext(), HalosMapActivity.class);
                startActivity(i);
            }
        });
    }
}
