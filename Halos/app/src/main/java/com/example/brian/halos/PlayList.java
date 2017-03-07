package com.example.brian.halos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class PlayList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        Toolbar toolbar = (Toolbar)findViewById(R.id.playlist_toolbar);
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
            case R.id.activity_settings:
                Intent intent4 = new Intent(this, SettingsActivity.class);
                startActivity(intent4);
                return true ;
            case R.id.Logout:
                Intent intent6 = new Intent(this, LoginActivity.class);
                startActivity(intent6);
                return true ;
            case R.id.play_list:
                Intent intent7 = new Intent(this, PlayList.class);
                startActivity(intent7);
                return true;
            default :
                // If we got here , the user ’s action was not recognized .
                // Invoke the superclass to handle it .
                return super.onOptionsItemSelected(item);
        }
    }
}
