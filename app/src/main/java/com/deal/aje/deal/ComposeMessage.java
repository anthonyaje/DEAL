package com.deal.aje.deal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import gcm.GcmController;
import mongo.controller.DbController;
import mongo.entity.Message;
import mongo.entity.Offer;
import mongo.entity.User;


public class ComposeMessage extends ActionBarActivity {
    EditText msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        Intent in = getIntent();
        final Bundle extras = in.getExtras();
        Log.d(Constants.TAG, ComposeMessage.class + " Extras : " + extras.toString());
        String buyer_id = extras.getString("buyerid");
        String seller_id = extras.getString("sellerid");
        String offer_id = extras.getString("offerid");
        String req_id = extras.getString("reqid");
        Log.d(Constants.TAG, ComposeMessage.class + " buyer_id : " + buyer_id);
        Log.d(Constants.TAG, ComposeMessage.class + " seller_id : " + seller_id);
        Log.d(Constants.TAG, ComposeMessage.class + " offerid : " + offer_id);
        Log.d(Constants.TAG, ComposeMessage.class + " reqid : " + req_id);
        Log.d(Constants.TAG, ComposeMessage.class + " from : " + extras.getString("from"));
        Log.d(Constants.TAG, ComposeMessage.class + " sender : " + extras.getString("sender"));
        Log.d(Constants.TAG, ComposeMessage.class + " title : " + extras.getString("title"));
        Log.d(Constants.TAG, ComposeMessage.class + " message : " + extras.getString("message"));
        Log.d(Constants.TAG, ComposeMessage.class + " collapse_key : " + extras.getString("collapse_key"));
        Log.d(Constants.TAG, ComposeMessage.class + " android.support.content.wakelockid : " + extras.getString("android.support.content.wakelockid"));
        Log.d(Constants.TAG, ComposeMessage.class + " caller_userid : " + extras.getString("caller_userid"));


        Offer offer = null;
        List<DBObject> dbobj_offer = DbController.getInstance().filterCollection(new Offer().getCollectionName(), new Offer().getColumns()[0], offer_id);
        if (dbobj_offer != null && dbobj_offer.size() > 0) {
            offer = new Offer(dbobj_offer.get(0));
        } else {
            Toast.makeText(this, "Offer NULL", Toast.LENGTH_SHORT).show();
            Log.d(Constants.TAG, ComposeMessage.class + " Offer is NULL : " + offer_id);
        }

        msg = (EditText) findViewById(R.id.editText_msg);
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
        if (buyer == null) {
            buyer = new User();
            Log.e(Constants.TAG, ComposeMessage.class + " Buyer is NULL: " + buyer_id);
        }

//        try {
//            Thread.sleep(2000, 0);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        List<DBObject> seller_list = DbController.getInstance().filterCollection(new User().getCollectionName(), new User().getColumns()[0], seller_id);
        if (seller_list != null && seller_list.size() > 0)
            seller = new User(seller_list.get(0));

        if (seller == null) {
            seller = new User();
            Log.e(Constants.TAG, ComposeMessage.class + " Seller is NULL: " + seller_id);
        }

        List<Message> message = getMessageInTimestampOrder(buyer_id, seller_id, req_id, offer_id);
        // Show message in timestamp order
        msg_history.setText("");

        for(Message m: message)
        {
            if (m.getUser_1().equals(m.getWho_send())) {
                // Show buyer message
                msg_history.append(buyer.getUsername());
                msg_history.append(" : ");
//                msg_history.append(" [BUYER] ");
                msg_history.append(m.getMessage());
                msg_history.append("\n");
            } else {
                // Show seller message
                msg_history.append(seller.getUsername());
                msg_history.append(" : ");
//                msg_history.append(" [SELLER] ");
                msg_history.append(m.getMessage());
                msg_history.append("\n");
            }
        }

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Message cant be empty",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                    String userid = sp.getString("UserId", "null");
                    String buyer_id = extras.getString("buyerid");
                    String seller_id = extras.getString("sellerid");
                    String offer_id = extras.getString("offerid");
                    String req_id = extras.getString("reqid");

                    Message m = new Message();
                    m.setUser_1(buyer_id);
                    m.setUser_2(seller_id);
                    m.setWho_send(userid);
                    m.setOffer_id(offer_id);
                    m.setRequest_id(req_id);
                    m.setTimestamp(System.currentTimeMillis());
                    m.setMessage(msg.getText().toString());
//                    Log.d(Constants.TAG, ComposeMessage.class + " Message: " + m.saveMongoDB().toString());
                    m.insertData(m, m.getCollectionName());

                    msg.setText("");
                    // Send GCM Notification to other party
                    String other_party = userid.equals(buyer_id) ? seller_id : buyer_id;
                    String reg_id = null;
                    String username = null;
                    Log.d(Constants.TAG, ComposeMessage.class + " Buyer      : "+buyer_id);
                    Log.d(Constants.TAG, ComposeMessage.class + " Seller     : "+seller_id);
                    Log.d(Constants.TAG, ComposeMessage.class + " Other party: "+other_party);
                    List<DBObject> users = DbController.getInstance().filterCollection(new User().getCollectionName(),
                            "id",   // Check on id
                            other_party
                    );
                    if(users!=null && !users.isEmpty())
                    {
                        User u = new User(users.get(0));
                        reg_id = u.getRegistrationId();
                        username = u.getUsername();
                    }
                    GcmController.getInstance().sendMessage(m.getMessage(), reg_id, username, Constants.MESSAGE_NEW_MAIL, buyer_id, seller_id, req_id, offer_id);
                    // Finish the page
                    Toast.makeText(getBaseContext(), "Message sent",
                            Toast.LENGTH_SHORT).show();
                    finish();
//                    Intent msg_intent = new Intent(getApplicationContext(), Messaging.class);
//                    startActivity(msg_intent);
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

    private List<Message> getMessageInTimestampOrder(String buyer, String seller, String request_id, String offer_id) {
        List<Message> list = new ArrayList();
        final List<DBObject> dbObjects = DbController.getInstance().filterCollection(new Message().getCollectionName(), new Message().getColumns()[1], buyer);
        for (DBObject o : dbObjects) {
            Message m = new Message(o);
            if (m.getUser_2().equals(seller) && m.getRequest_id().equals(request_id) && m.getOffer_id().equals(offer_id))
                list.add(m);
        }
        return list;
    }
}
