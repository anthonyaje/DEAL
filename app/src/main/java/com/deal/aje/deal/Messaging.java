package com.deal.aje.deal;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mongo.controller.DbController;
import mongo.entity.Message;
import mongo.entity.User;


public class Messaging extends ListActivity {
    List<String> msg_name = new ArrayList<>();       //buyer=me to seller
    List<String> msg_body = new ArrayList<>();
    String my_id;
    List<String> bf_uid = new ArrayList<>(); //bf buy from
    List<String> bf_oid = new ArrayList<>();
    List<String> bf_rid = new ArrayList<>();
    List<String> st_uid = new ArrayList<>(); //st_sell to
    List<String> st_oid = new ArrayList<>();
    List<String> st_rid = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_messaging);
        //Intent inte = getIntent();
        //inte.getExtras("")
        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        my_id = sp.getString("UserId","null");

        //List<String> sell_msg_name = new ArrayList<>();     //seller=me to buyer
        //List<String> sell_msg_body = new ArrayList<>();


        for(int i=0; i<9;i++){
            msg_name.add("Buy from: aje "+i);
            msg_body.add("This is message from aje "+i);
        }

        for(int i=0; i<9;i++){
            msg_name.add("Sell to: aje "+i);
            msg_body.add("This is message to aje "+i);
        }


        Message m = new Message();
        List<DBObject> list = DbController.getInstance().filterCollection(m.getCollectionName(), m.getColumns()[1], my_id);
        for(DBObject it:list){
            m = new Message(it);
            bf_uid.add(m.getUser_2());
            bf_oid.add(m.getOffer_id());
            bf_rid.add(m.getRequest_id());

            List<DBObject> list_namadia = DbController.getInstance().filterCollection(new User().getCollectionName(), new User().getColumns()[0], m.getUser_2());
            if(list_namadia == null){
                Toast.makeText(this, "Query 1 not found on his name",
                        Toast.LENGTH_LONG).show();
                break;
            }
            User he = new User(list_namadia.get(0));
            msg_name.add("Buy from: "+he.getUsername()+" Time:"+new Date(m.getTimestamp()).toString());
            msg_body.add(m.getMessage());
        }

        List<DBObject> llist = DbController.getInstance().filterCollection(m.getCollectionName(), m.getColumns()[2], my_id);
        for(DBObject it:llist){
            m = new Message(it);
            st_uid.add(m.getUser_1());
            st_oid.add(m.getOffer_id());
            st_rid.add(m.getRequest_id());

            List<DBObject> list_namadia = DbController.getInstance().filterCollection(new User().getCollectionName(), new User().getColumns()[0], m.getUser_1());
            if(list_namadia == null){
                Toast.makeText(this, "Query 2 not found on his name",
                        Toast.LENGTH_LONG).show();
                break;
            }
            User he = new User(list_namadia.get(0));
            msg_name.add("Sell to: "+he.getUsername()+" Time:"+new Date(m.getTimestamp()).toString());
            msg_body.add(m.getMessage());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, msg_name.toArray(new String[msg_name.size()]));
        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, final int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        final int i = l.getFirstVisiblePosition()+position;
        Log.d(Constants.TAG, "index real: "+i +"position: "+position);
        
        new AlertDialog.Builder(this)
                .setTitle("Send Message")
                .setMessage("Send Message to seller?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //FIXME
                        // position
                        Intent msg_intent = new Intent(getApplicationContext(), ComposeMessage.class);
                        msg_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if(msg_name.contains("Buy from")) {
                            msg_intent.putExtra("buyerid", my_id);
                            msg_intent.putExtra("sellerid", bf_uid.get(position));
                            msg_intent.putExtra("offerid", bf_oid.get(position));
                            msg_intent.putExtra("reqid", bf_rid.get(position));
                        }else{
                            msg_intent.putExtra("buyerid", st_uid.get(position));
                            msg_intent.putExtra("sellerid",my_id);
                            msg_intent.putExtra("offerid", st_oid.get(position));
                            msg_intent.putExtra("reqid", st_rid.get(position));

                        }
                        startActivity(msg_intent);

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
        getMenuInflater().inflate(R.menu.menu_messaging, menu);
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
