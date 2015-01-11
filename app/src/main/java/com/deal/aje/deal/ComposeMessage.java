package com.deal.aje.deal;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.DBObject;

import java.util.List;

import mongo.controller.DbController;
import mongo.entity.Message;
import mongo.entity.Offer;


public class ComposeMessage extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        Intent in = getIntent();
        final String buyer_id = in.getExtras().getString("buyerid");
        final String seller_id = in.getExtras().getString("sellerid");
        final String offer_id = in.getExtras().getString("offerid");
        final String req_id = in.getExtras().getString("reqid");

        Offer offer=null;
        List<DBObject> dbobj_offer = DbController.getInstance().filterCollection(new Offer().getCollectionName(), new Offer().getColumns()[0],offer_id);
        if(dbobj_offer != null && dbobj_offer.size()>0){
            offer = new Offer(dbobj_offer.get(0));
        }else{
            Toast.makeText(this, "Offer NULL", Toast.LENGTH_SHORT).show();

        }

        EditText msg = (EditText) findViewById(R.id.editText_msg);
        Button btn_send = (Button) findViewById(R.id.button_send_msg);
        TextView msg_history = (TextView) findViewById(R.id.textView_history);
        msg_history.setMovementMethod(new ScrollingMovementMethod());
        ImageView img = (ImageView) findViewById(R.id.imageView_compose_message);
        img.setImageBitmap(BitmapFactory.decodeByteArray(offer.getPicture(), 0 , offer.getPicture().length));
        
        final String str_msg = msg.getText().toString();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(str_msg.isEmpty()){
                    Toast.makeText(getBaseContext(), "Message cant be empty",
                            Toast.LENGTH_LONG).show();

                }else{
                    //TODO
                    // send mesage to DB and back to home
                    Message m = new Message();
                    m.setUser_1(buyer_id);
                    m.setUser_2(seller_id);
                    m.setOffer_id(offer_id);
                    m.setRequest_id(req_id);
                    m.setRead_timestamp((long) System.currentTimeMillis());
                    m.setMessage(str_msg);
                    m.insertData(m,m.getCollectionName());
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_message, menu);
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
