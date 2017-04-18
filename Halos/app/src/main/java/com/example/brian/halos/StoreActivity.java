package com.example.brian.halos;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This class is the Store activity that contains the viewpager and adapter that
 * contains each tab of the store. This class also instantiates each interface class in each
 * tab to handle adding and removing of tours in the shopping cart and handles passing
 * the shopping cart to the Checkout Activity.
 */
public class StoreActivity extends AppCompatActivity  implements Store_Tab_HotTours.OnFragmentInteractionListener,
        Tour_Display_Frag.OnFragmentInteractionListener,Store_Tab_TopPaid.OnFragmentInteractionListener,
        Store_Tab_TopFree.OnFragmentInteractionListener, Store_Tab_HotTours.AddTourCopyListerner,
        Store_Tab_TopFree.AddTourCopyListerner3,Store_Tab_TopPaid.AddTourCopyListerner2{
    final int limit = 5;
    static List<TourCopy> cart = new ArrayList<TourCopy>();
    TourCopy remove1 = new TourCopy();


    String usernameSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        usernameSave = getIntent().getStringExtra("username");

        //Set up Viewpager, Adapter and Toolbar support.
        Toolbar toolbar = (Toolbar)findViewById(R.id.menu);
        setSupportActionBar(toolbar);
        ViewPager viewpager = (ViewPager)findViewById(R.id.Store_Viewpager);
        Store_TabAdapter store_adapter= new Store_TabAdapter(getSupportFragmentManager());
        viewpager.setAdapter(store_adapter);
        viewpager.setOffscreenPageLimit(limit);
    }


    public boolean onCreateOptionsMenu (Menu menu ) {
        getMenuInflater().inflate(R.menu.storetoolbar,menu );
        return true ;
    }

    //Toolbar options to travel around the Application.
    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch( item.getItemId() ) {
            case R.id.Home2:
                Intent intent1 = new Intent(this, HalosMapActivity.class);
                intent1.putExtra("username", usernameSave);
                startActivity(intent1);
                return true ;
            case R.id.store2:
                Intent intent2 = new Intent(this, StoreActivity.class);
                intent2.putExtra("username", usernameSave);
                startActivity(intent2);
                return true ;
            case R.id.profile2:
                Intent intent3 = new Intent(this, UserProfileActivity.class);
                intent3.putExtra("username", usernameSave);
                startActivity(intent3);
                return true ;
            case R.id.activity_settings2:
                Intent intent4 = new Intent(this, SettingsActivity.class);
                intent4.putExtra("username", usernameSave);
                startActivity(intent4);
                return true ;
            case R.id.Logout2:
                Intent intent6 = new Intent(this, LoginActivity.class);
                cart.clear();
                startActivity(intent6);
                return true ;
            case R.id.Cart:
                Intent intent7 = new Intent(this, Checkout_Store.class);
                intent7.putExtra("username",usernameSave);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list",(Serializable)cart);
                intent7.putExtras(bundle);
                startActivity(intent7);
                return true;
            default :
                // If we got here , the user ’s action was not recognized .
                // Invoke the superclass to handle it .
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    //These methods handle adding and removing tour objects from the shopping
    //cart list.

    @Override
    public void AddTourCopy(TourCopy copy) {
        if (cart.contains(copy)){
            cart.remove(copy);
            Toast.makeText(getApplicationContext(),"Removed ",Toast.LENGTH_SHORT).show();
            Log.v("Cart","Removed"+copy.getName());
        }else {
            cart.add(copy);
            Log.v("Cart","Add"+copy.getName());
            Toast.makeText(getApplicationContext(),"Added ",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void AddTourCopy3(TourCopy copy) {

                    if (cart.contains(copy)){
                        cart.remove(copy);
                        Toast.makeText(getApplicationContext(),"Removed ",Toast.LENGTH_SHORT).show();
                        Log.v("Cart","Removed"+copy.getName());
                    }else {
                        cart.add(copy);
                        Log.v("Cart","Add"+copy.getName());
                        Toast.makeText(getApplicationContext(),"Added ",Toast.LENGTH_SHORT).show();
                    }
    }

    @Override
    public void AddTourCopy2(TourCopy copy) {

                    if (cart.contains(copy)){
                        cart.remove(copy);
                        Toast.makeText(getApplicationContext(),"Removed ",Toast.LENGTH_SHORT).show();
                        Log.v("Cart","Removed"+copy.getName());
                    }else {
                        cart.add(copy);
                        Log.v("Cart","Add"+copy.getName());
                        Toast.makeText(getApplicationContext(),"Added ",Toast.LENGTH_SHORT).show();
                    }
    }
}
