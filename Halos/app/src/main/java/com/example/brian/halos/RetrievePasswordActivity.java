package com.example.brian.halos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Class meant for retrieving User's password but wasn't implemented
 * due to Time constraints. For final demo it just brought user
 * back to Login Screen.
 */

public class RetrievePasswordActivity extends AppCompatActivity {
    Button retrievePwButton;

    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);

        email = (EditText) findViewById(R.id.forgot_pw_email);
        retrievePwButton = (Button) findViewById(R.id.btn_get_pw);
        retrievePwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to retrieve password activity
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
