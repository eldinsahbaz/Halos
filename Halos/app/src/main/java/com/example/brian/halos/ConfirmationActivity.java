package com.example.brian.halos;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/* This class is for showing the user the recipt of their transaction and notify
* the transaction went through. Returns User to home page when clicking button.
*/
// Source code  from https://www.simplifiedcoding.net/android-paypal-integration-tutorial/ by belal khan
public class ConfirmationActivity extends AppCompatActivity {
    String username3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        //Getting Intent
        Intent intent = getIntent();
        username3 = getIntent().getStringExtra("username");
        Button returnhome = (Button)findViewById(R.id.return_home);
        returnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), HalosMapActivity.class);
                intent1.putExtra("username",username3);
                startActivity(intent1);
            }
        });
        //Get Info stored in Intent to show user the details of the transaction.
        try {
            JSONObject jsonDetails = new JSONObject(intent.getStringExtra("PaymentDetails"));

            //Displaying payment details
            showDetails(jsonDetails.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    //Set fields with the proper data.
    private void showDetails(JSONObject jsonDetails, String paymentAmount) throws JSONException {
        //Views
        TextView textViewId = (TextView) findViewById(R.id.paymentId);
        TextView textViewStatus= (TextView) findViewById(R.id.paymentStatus);
        TextView textViewAmount = (TextView) findViewById(R.id.paymentAmount);

        //Showing the details from json object
        textViewId.setText(jsonDetails.getString("id"));
        textViewStatus.setText(jsonDetails.getString("state"));
        textViewAmount.setText(paymentAmount+" USD");
    }


    @Override
    public void onBackPressed() {
    }

}
