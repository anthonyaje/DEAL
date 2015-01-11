package com.deal.aje.deal;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mongo.controller.DbController;
import mongo.entity.Offer;
import mongo.entity.Request;


public class ListItem extends ListActivity {
    List<String> item_arr = new ArrayList<String>();
    List<String> desc_arr = new ArrayList<String>();
    List<String> sellid_arr = new ArrayList<String>();
    List<byte[]> img_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent in = getIntent();
        String hashtag = in.getExtras().getString("hashtag");
        String reqid = in.getExtras().getString("reqid");
        Request myreq=null;

        List<DBObject> myreq_list = DbController.getInstance().filterCollection(new Offer().getCollectionName(), new Request().getColumns()[0], reqid);
        if (myreq_list!=null && myreq_list.size()>0) {
            myreq = new Request(myreq_list.get(0));
        }

        //setContentView(R.layout.activity_list_item);
        //ListView item_list = (ListView) findViewById(R.id.list);

        for(int i=0; i<4; i++){
            item_arr.add(i,"item"+i);
            desc_arr.add(i,"Desc Item"+i);
        }

        List<DBObject> list = DbController.getInstance().findAll(new Offer().getCollectionName());
        for(DBObject it: list){
            Offer o = new Offer(it);
            final String hash = o.getHashtag();
            for(String s: hashtag.split(" ")){
                for(String ss: hash.split(" ")){
                    if(ss.contains(s) || s.contains(ss) && !ss.isEmpty() && !s.isEmpty()
                            &&rangeCovered(myreq, o) && o.getComplete()==0){
                        item_arr.add(o.getHashtag());
                        desc_arr.add(o.getDetail());
                        img_list.add(o.getPicture());
                        sellid_arr.add(o.getUser_id());
                        break;
                    }
                }
            }

        }

        SimpleArrayAdapter adapter = new SimpleArrayAdapter(this, item_arr.toArray(new String[item_arr.size()]),desc_arr.toArray(new String[item_arr.size()]), img_list);
        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected from Seller: "+sellid_arr.get(position), Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(this)
                .setTitle("Send Message")
                .setMessage("Send Message to seller?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_item, menu);
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

    private boolean rangeCovered(Request request, Offer offer)
    {
        final double distance = distFrom(request.getGpsLat(), request.getGpsLong(), offer.getGpsLat(), offer.getGpsLong());
        boolean ok = false;
        if(distance < request.getRange() && distance < offer.getRange()) ok = true;
        Log.i(Constants.TAG, "Distance Accepted");
        return ok;
    }

    private static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; //kilometers
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (float) (earthRadius * c);
        Log.i(Constants.TAG, "Distance: "+dist);
        return dist;
    }
}
