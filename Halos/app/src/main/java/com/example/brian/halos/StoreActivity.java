package com.example.brian.halos;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.Vector;

public class StoreActivity extends AppCompatActivity  implements Store_Tab_HotTours.OnFragmentInteractionListener,
        Tour_Display_Frag.OnFragmentInteractionListener,Store_Tab_TopPaid.OnFragmentInteractionListener,
        Store_Tab_TopFree.OnFragmentInteractionListener,Store_Tab_Checkout.OnFragmentInteractionListener{
    final int limit = 5;
    String usernameSave;
    //MIGHT need FragmentInteractionListerner for all tabs- due to replacing this layouts container
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        usernameSave = getIntent().getStringExtra("username");
        Toolbar toolbar = (Toolbar)findViewById(R.id.menu);
        setSupportActionBar(toolbar);
        ViewPager viewpager = (ViewPager)findViewById(R.id.Store_Viewpager);
        Store_TabAdapter store_adapter= new Store_TabAdapter(getSupportFragmentManager());
        viewpager.setAdapter(store_adapter);
        viewpager.setOffscreenPageLimit(limit);
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
    public void onFragmentInteraction(Uri uri) {

    }
}
