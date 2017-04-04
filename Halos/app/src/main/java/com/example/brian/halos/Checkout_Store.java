package com.example.brian.halos;
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

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

//check on sandbox payal -- faciliator account and buyer account
//Source code from https://www.simplifiedcoding.net/android-paypal-integration-tutorial/ by belal khan
public class Checkout_Store extends AppCompatActivity implements View.OnClickListener {
    static List<TourCopy> cart2 = new ArrayList<TourCopy>();
    RecyclerView recyclerView;
    public static final int PAYPAL_REQUEST_CODE = 123;
    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId("ATG--DNg034gTxOzSV66Fk16qjYHv7cjdvtbZPJjIWUBhsIBP9TvkW2dyWBctX40wsgiMR9uZIxR3L3a");
    private Button buttonPay;
    private TextView showamount;
    int amount ;
    //Payment Amount
    private String paymentAmount;
    Checkout_RecycleAdapter checkout_recycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout__store);

        buttonPay = (Button) findViewById(R.id.checkout_button);
        showamount = (TextView)findViewById(R.id.cart_total);
        Intent intent2 = this.getIntent();
        Bundle bundle = intent2.getExtras();
        amount = 0;
        cart2 = (List<TourCopy>) bundle.getSerializable("list");
        for (int i=0; i< cart2.size() ; i++){
            amount += (int) cart2.get(i).getPrice();
        }
        buttonPay.setOnClickListener(this);
        showamount.setText("Total Amount: $" +String.valueOf(amount));
        recyclerView = (RecyclerView)findViewById(R.id.RecycleView_Checkout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        checkout_recycleAdapter = new Checkout_RecycleAdapter(getApplication(),cart2);
        recyclerView.setAdapter(checkout_recycleAdapter);

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
         getPayment();
    }

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

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(this, ConfirmationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
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

}
