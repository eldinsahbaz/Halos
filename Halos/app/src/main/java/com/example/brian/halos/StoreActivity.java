package com.example.brian.halos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class StoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        Toolbar toolbar = (Toolbar)findViewById(R.id.menu);
        setSupportActionBar(toolbar);
    }


    public boolean onCreateOptionsMenu ( Menu menu ) {
        getMenuInflater().inflate(R.menu.toolbar,menu );
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch( item.getItemId() ) {
            case R.id.Home:
                Intent intent1 = new Intent(this, HalosMapActivity.class);
                startActivity(intent1);
                return true ;
            case R.id.store:
                Intent intent2 = new Intent(this, StoreActivity.class);
                startActivity(intent2);
                return true ;
            case R.id.profile:
                Intent intent3 = new Intent(this, UserProfileActivity.class);
                startActivity(intent3);
                return true ;
            case R.id.Logout:
                Intent intent4 = new Intent(this, LoginActivity.class);
                startActivity(intent4);
                return true ;
            case R.id.checkout:
                Intent intent5 = new Intent(this, Checkout_Store.class);
                startActivity(intent5);
            default :
// If we got here , the user ’s action was not recognized .
// Invoke the superclass to handle it .
                return super.onOptionsItemSelected(item);
        }
    }
}
