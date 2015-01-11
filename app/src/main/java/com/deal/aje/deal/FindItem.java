package com.deal.aje.deal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import mongo.entity.Request;


public class FindItem extends ActionBarActivity implements LocationListener {
    // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            curLat =  (location.getLatitude());
            curLong =  (location.getLongitude());
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    boolean locEnabled;
    private String provider;
    double curLat, curLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_item);

        //Getting Location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!locEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        provider = LocationManager.NETWORK_PROVIDER;
        Location location = locationManager.getLastKnownLocation(provider);
        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            Toast.makeText(this, "Location Longitude: "+location.getLongitude()+"Location Latitude: "+location.getLatitude(),
                    Toast.LENGTH_LONG).show();
            onLocationChanged(location);
        } else {
            Log.d("DEAL_LOG", "Location Unavailable");
        }


        final EditText et_hashtag = (EditText) findViewById(R.id.editText_hashtag);
        final EditText et_desc = (EditText) findViewById(R.id.editText_desc);
        Button next_find = (Button) findViewById(R.id.button_find);
        next_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time= System.currentTimeMillis();
                int complete = 0;
                String hashtag = et_hashtag.getText().toString();
                String desc = et_desc.getText().toString();
                SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                String my_id = sp.getString("UserId","uid not found");
                String gps_rage = sp.getString("GpsRange","range not found");
                // create send data to database (GPS, TIME, complete)
                Request r = new Request();
                r.setUser_id(my_id);
                r.setHashtag(hashtag);
                r.setDetail(desc);
                r.setRange(Integer.parseInt(gps_rage));
                r.setGpsLat(curLat);
                r.setGpsLong(curLong);
                r.setRequest_time(time);
                //r.setValid_time();
                r.setComplete(complete);
                r.insertData(r, r.getCollectionName());

                Intent list_intent = new Intent(v.getContext(), ListItem.class);
                startActivity(list_intent);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        curLat =  (location.getLatitude());
        curLong =  (location.getLongitude());
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
