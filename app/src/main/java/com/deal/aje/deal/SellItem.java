package com.deal.aje.deal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.DBObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;

import gcm.GcmController;
import mongo.controller.DbController;
import mongo.entity.Offer;
import mongo.entity.Request;
import mongo.entity.User;
import mongo.entity.base.MongoObj;


public class SellItem extends ActionBarActivity {
    EditText et_hashatg, et_desc;
    Button btn_pic, btn_next;
    ImageView iv_img;
    byte[] img_byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_item);
        et_hashatg = (EditText) findViewById(R.id.editText_hashtag_sell);
        et_desc = (EditText) findViewById(R.id.editText_desc_sell);
        btn_pic = (Button) findViewById(R.id.btn_pict);
        btn_next = (Button) findViewById(R.id.btn_sell);
        iv_img = (ImageView) findViewById(R.id.imageView_item_image);

        // Get Location
        final Location location = LocationController.getInstance().getLocation(this);
        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + LocationController.getInstance().getProvider() + " has been selected.");
            Toast.makeText(this, "Location Longitude: " + location.getLongitude() + "\nLocation Latitude: " + location.getLatitude(),
                    Toast.LENGTH_LONG).show();
        } else {
            Log.d("DEAL_LOG", "Location Unavailable");
        }

        btn_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO bring to next intent
                String hashtag = et_hashatg.getText().toString();
                String desc = et_desc.getText().toString();

                if(hashtag==null && hashtag.trim().isEmpty()) {
                    return;
                }

                long time = System.currentTimeMillis();
                int complete = 0;
                SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                String my_id = sp.getString("UserId", "uid not found");
                String gps_rage = sp.getString("GpsRange", "range not found");
                // create send data to database (GPS, TIME, complete)
                Offer offer = new Offer();
                offer.setUser_id(my_id);
                offer.setHashtag(hashtag);
                offer.setDetail(desc);
                offer.setRange(Integer.parseInt(gps_rage));
                offer.setGpsLat(location.getLatitude());
                offer.setGpsLong(location.getLongitude());
                offer.setRequest_time(time);
                offer.setValid_time(DateHelper.MONTH);
                offer.setComplete(complete);
                if (img_byteArray != null)
                    offer.setPicture(img_byteArray);

                MongoObj.insertData(offer, new Offer().getCollectionName());
                Log.i(Constants.TAG, "Data stored in Database, sending all notifications on Buyer");
                Toast.makeText(getApplicationContext(), "Data stored in Database, sending all notifications on Buyer", Toast.LENGTH_SHORT);
                // Check on the database, if there is buyer with similar tag
                Log.i(Constants.TAG, "Request collection name : "+new Request().getCollectionName());
                Log.i(Constants.TAG, "Offer collection name : "+new Offer().getCollectionName());
                final List<DBObject> all_request = DbController.getInstance().findAll(new Request().getCollectionName());
                int total_match = 0;
                for (DBObject db : all_request) {
                    Request req = new Request(db);
                    for (String s : hashtag.split(" ")) // This offer
                    {
                        // Check for matching hashtag
                        for (String ss : req.getHashtag().split(" ")) // Compare with all request in DB
                        {
                            if ((s.contains(ss) || ss.contains(s)) && !s.isEmpty() && !ss.isEmpty() && rangeCovered(req, offer) && req.getComplete()==0) {
                                // Matching request and offer
                                List<DBObject> users = DbController.getInstance().filterCollection(new User().getCollectionName(),
                                        "id",   // Check on id
                                        req.getUser_id()
                                );
                                Log.i(Constants.TAG, "===============================");
                                Log.i(Constants.TAG, "Request User: "+req.getUser_id());
                                Log.i(Constants.TAG, "Matching request and offer");
                                Log.i(Constants.TAG, "Request: " + req.getHashtag() + " : " + req.getId());
                                Log.i(Constants.TAG, "Offer  : " + offer.getHashtag() + " : " + offer.getId());
                                Log.i(Constants.TAG, "Users Size : " + users.size());
                                Log.i(Constants.TAG, "===============================");
                                for (DBObject obj : users) {
                                    User u = new User(obj);
                                    Log.i(Constants.TAG, "Request from : " + u.getUsername());
                                    // Send GCM notification
                                    GcmController.getInstance().sendMessage(req.getHashtag()
                                            , u.getRegistrationId(), u.getUsername(), Constants.MESSAGE_FROM_SELLER, u.getId());
                                }
                                total_match++;
                                // Already match
                                break;
                            }
                        }
                    }
                }
                Log.i(Constants.TAG, "Total Match : " + total_match);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv_img.setImageBitmap(imageBitmap);
            //compressing to byte array
            ByteArrayOutputStream img_stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, img_stream);
            img_byteArray = img_stream.toByteArray();
            Toast.makeText(this, "byte array Image size: " + img_byteArray.length,
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sell_item, menu);
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

    private boolean rangeCovered(Request request, Offer offer) {
        final double distance = distFrom(request.getGpsLat(), request.getGpsLong(), offer.getGpsLat(), offer.getGpsLong());
        boolean ok = false;
        if (distance < request.getRange() && distance < offer.getRange()) ok = true;
        Log.i(Constants.TAG, "Distance Accepted");
        return ok;
    }

    private static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; //kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = (float) (earthRadius * c);
        Log.i(Constants.TAG, "Distance: " + dist);
        return dist;
    }
}
