package com.example.brian.halos;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*This class is for showing all the tour in the cart and allows user to check
* the total price for all the tours and pay it. Contains the Paypal API in
* sandbox environment that processes the transaction made by the User and
* starts ConfirmationActivity after transaction is complete.
*/
//Source code from https://www.simplifiedcoding.net/android-paypal-integration-tutorial/ by belal khan
public class Checkout_Store extends AppCompatActivity implements View.OnClickListener {
    static List<TourCopy> cart2 = new ArrayList<TourCopy>();
    static List<String> cart_tour_ids = new ArrayList<String>();
    String retVal;
    RecyclerView recyclerView;
    public static final int PAYPAL_REQUEST_CODE = 123;
    private static PayPalConfiguration config = new PayPalConfiguration()

            //  (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId("ATG--DNg034gTxOzSV66Fk16qjYHv7cjdvtbZPJjIWUBhsIBP9TvkW2dyWBctX40wsgiMR9uZIxR3L3a");
    private Button buttonPay;
    private TextView showamount;
    int amount ;
    //Payment Amount
    private String paymentAmount;
    Checkout_RecycleAdapter checkout_recycleAdapter;
    String username3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout__store);
        cart2.clear();
        buttonPay = (Button) findViewById(R.id.checkout_button);
        showamount = (TextView)findViewById(R.id.cart_total);
        //Retrieves information passed by bundle and intent from store activity.
        Intent intent2 = this.getIntent();
        username3 = getIntent().getStringExtra("username");
        Bundle bundle = intent2.getExtras();

        amount = 0;
        cart2 = (List<TourCopy>) bundle.getSerializable("list");

        for (int i=0; i< cart2.size() ; i++){
            amount += (int) cart2.get(i).getPrice();
            cart_tour_ids.add(cart2.get(i).getName());
        }
        buttonPay.setOnClickListener(this);

        //Button to cancels and return back to the store.
        Button cancel = (Button)findViewById(R.id.cancel_buy);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(getApplicationContext(),StoreActivity.class);
                intent4.putExtra("username", username3);
                startActivity(intent4);
            }
        });
        showamount.setText("Total Amount: $" +String.valueOf(amount));
        recyclerView = (RecyclerView)findViewById(R.id.RecycleView_Checkout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        checkout_recycleAdapter = new Checkout_RecycleAdapter(getApplication(),cart2);
        recyclerView.setAdapter(checkout_recycleAdapter);
        checkout_recycleAdapter.notifyDataSetChanged();
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }


    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        cart2.clear();
        getPayment();
    }

    //Method to give Paypal API parameters to run.
    private void getPayment() {
        //Getting the amount from editText
        //Change to Tour amount.
        paymentAmount = String.valueOf(amount);

        //Creating a paypalpayment
        java.math.BigDecimal amount = new java.math.BigDecimal(String.valueOf(paymentAmount));
        PayPalPayment payment = new PayPalPayment(amount, "USD", "Fee",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);
        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    //Method that starts the transaction and returns result of transaction
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Checkout_Store.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);
                        UpdateBought updateBought = new UpdateBought();
                        updateBought.execute();
                        StoreActivity.cart.clear();
                        Checkout_Store.cart2.clear();
                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(this, ConfirmationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("username",username3)
                                .putExtra("PaymentAmount", paymentAmount));

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Checkout_Store.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    //Class that executes a JSON Post request to server to update User's information on
    //tours they bought.
    private class UpdateBought extends AsyncTask<Void,Void,String> {
        OkHttpClient client = new OkHttpClient();

        protected UpdateBought(){
        }

        @Override
        protected String doInBackground(Void... voids) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Map json_params = new HashMap<String, String>();
            json_params.put("tour_id", cart_tour_ids);
            json_params.put("username",username3);

            JSONObject json_parameter = new JSONObject(json_params);
            RequestBody json_body = RequestBody.create(JSON, json_parameter.toString());
            Request request = new Request.Builder()
                    // if you want to run on local use http://10.0.2.2:12344
                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
                    .url("http://lcs-vc-esahbaz.syr.edu:12344/bought")
                    .post(json_body)
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

                    Log.e("TourCreationActivity", "onResponse:" + responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject respObject = jsonObject.getJSONObject("response");
                        String result = respObject.getString("result");

                        retVal = result;



                    } catch (Exception e){
                        Log.e("TourCreationActivity", "Exception Thrown: " + e);
                        retVal = e.toString();
                    }
                }


            });
            return retVal;
        }

        @Override
        protected void onPostExecute(String result) {
            cart_tour_ids.clear();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}


    }

    @Override
    public void onBackPressed() {
    }

}
