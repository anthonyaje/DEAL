package com.deal.aje.deal;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

import java.util.ArrayList;
import java.util.List;

import mongo.controller.DbController;
import mongo.entity.Message;
import mongo.entity.Offer;
import mongo.entity.User;


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

        Offer offer = null;
        List<DBObject> dbobj_offer = DbController.getInstance().filterCollection(new Offer().getCollectionName(), new Offer().getColumns()[0], offer_id);
        if (dbobj_offer != null && dbobj_offer.size() > 0) {
            offer = new Offer(dbobj_offer.get(0));
        } else {
            Toast.makeText(this, "Offer NULL", Toast.LENGTH_SHORT).show();
            Log.d(Constants.TAG, ComposeMessage.class + " Offer is NULL : " + offer_id);
        }

        final EditText msg = (EditText) findViewById(R.id.editText_msg);
        Button btn_send = (Button) findViewById(R.id.button_send_msg);
        TextView msg_history = (TextView) findViewById(R.id.textView_history);
        msg_history.setMovementMethod(new ScrollingMovementMethod());
        ImageView img = (ImageView) findViewById(R.id.imageView_compose_message);
        if (offer != null && offer.getPicture() != null) {
            img.setImageBitmap(BitmapFactory.decodeByteArray(offer.getPicture(), 0, offer.getPicture().length));
        }

        // Show message history in timestamp order
        User buyer = null;
        User seller = null;
        List<DBObject> buyer_list = DbController.getInstance().filterCollection(new User().getCollectionName(), "id", buyer_id);
        if (buyer_list != null && buyer_list.size() > 0)
            buyer = new User(buyer_list.get(0));
        List<DBObject> seller_list = DbController.getInstance().filterCollection(new User().getCollectionName(), "id", seller_id);
        if (seller_list != null && seller_list.size() > 0)
            seller = new User(seller_list.get(0));
        if (buyer == null) {
            buyer = new User();
            Log.e(Constants.TAG, ComposeMessage.class + " Buyer is NULL: " + buyer_id);
        }
        if (seller == null) {
            seller = new User();
            Log.e(Constants.TAG, ComposeMessage.class + " Seller is NULL: " + seller_id);
        }

        List<Message> buyerMessage = getBuyerMessageInTimestampOrder(buyer_id);
        List<Message> sellerMessage = getSellerMessageInTimestampOrder(seller_id);
        // Show message in timestamp order
        msg_history.setText("");
        Log.d(Constants.TAG, ComposeMessage.class + "Buyer Message: " + buyerMessage.size());
        Log.d(Constants.TAG, ComposeMessage.class + "Seller Message: " + sellerMessage.size());
        int i = 0, j = 0;
        while (i < buyerMessage.size() && j < sellerMessage.size()) {
            if (buyerMessage.get(i).getTimestamp() < sellerMessage.get(i).getTimestamp()) {
                // Show buyer message
                msg_history.append(buyer.getUsername());
                msg_history.append(" : ");
                msg_history.append(buyerMessage.get(i).getMessage());
                msg_history.append("\n");
                i++;
            } else {
                // Show seller message
                msg_history.append(seller.getUsername());
                msg_history.append(" : ");
                msg_history.append(sellerMessage.get(j).getMessage());
                msg_history.append("\n");
                j++;
            }
        }

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Message cant be empty",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //TODO
                    // send mesage to DB and back to home
                    Message m = new Message();
                    m.setUser_1(buyer_id);
                    m.setUser_2(seller_id);
                    m.setOffer_id(offer_id);
                    m.setRequest_id(req_id);
                    m.setRead_timestamp((long) System.currentTimeMillis());
                    m.setMessage(msg.getText().toString());
                    Log.d(Constants.TAG, "Message: " + m.saveMongoDB().toString());
                    m.insertData(m, m.getCollectionName());
                }
                // TODO
                // Back to main page
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

    private List<Message> getBuyerMessageInTimestampOrder(String userid) {
        List<Message> list = new ArrayList();
        final List<DBObject> dbObjects = DbController.getInstance().filterCollection(new Message().getCollectionName(), new Message().getColumns()[1], userid);
        for (DBObject o : dbObjects) {
            Message m = new Message(o);
            list.add(m);
        }
        return list;
    }

    private List<Message> getSellerMessageInTimestampOrder(String userid) {
        List<Message> list = new ArrayList();
        final List<DBObject> dbObjects = DbController.getInstance().filterCollection(new Message().getCollectionName(), new Message().getColumns()[2], userid);
        for (DBObject o : dbObjects) {
            Message m = new Message(o);
            list.add(m);
        }
        return list;
    }
}
