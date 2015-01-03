package com.deal.aje.deal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import java.net.UnknownHostException;


public class home extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Context context = this;

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText("okay");

        try{
            Mongo Client = new Mongo("140.113.216.123");
            DB db = Client.getDB("cloud");
            DBCollection collect = db.getCollection("User");
            String str = collect.findOne().get("username").toString();

            tv.setText(str);


           Client.close();
        } catch (UnknownHostException e){

        }

        Button btn_sell = (Button) findViewById(R.id.button_sell);
        btn_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sell_intent = new Intent(context, SellItem.class);
                startActivity(sell_intent);
            }
        });

        Button btn_buy = (Button) findViewById(R.id.button_buy);
        btn_buy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent buy_intent = new Intent(context, FindItem.class);
                startActivity(buy_intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
