package com.deal.aje.deal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mongodb.DBObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import gcm.GcmController;
import gcm.Message;
import gcm.MulticastResult;
import gcm.Result;
import gcm.Sender;
import mongo.controller.DbController;
import mongo.entity.User;


public class home extends ActionBarActivity {
    SharedPreferences sp = null;

    /**
     * Tag used on log messages.
     */
    static final String TAG = "DEAL";

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = this;
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        String user_name = sp.getString("UserName", "null");
        Log.d("DEAL", "username from sp: " + user_name);
        TextView tv = (TextView) findViewById(R.id.textView);

    /*    // Testing read user
        User u = new User();
        DBCollection collection = DbController.getInstance().getCollection(u.getCollectionName());
        DBCursor cursor = DbController.getInstance().filterCollection(collection, u.getColumns()[1], user_name);
        while(cursor.hasNext())
        {
            DBObject next = cursor.next();
            if(next!=null)
            {
                u = new User(next);
                tv.setText(u.getUsername());
            }
        }
    */
        Button btn_sell = (Button) findViewById(R.id.button_sell);
        btn_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sell_intent = new Intent(context, SellItem.class);
                startActivity(sell_intent);
                //sendMessage("Sell",
                //        "APA91bF1gTTApift4O2xUQ4Dsa-bzXyDWPQCPvYOHqlcOpqIsCaayVJTDELjw67B6OpoSNa5BseKPYJALRddRkklMGCO6SFIlx8N30xfWvopzMlgYclNhvABLTd57NTmzfdUXwgD6L-78znkj3lOIwzIlUu9ZzKjAg");
            }
        });

        Button btn_buy = (Button) findViewById(R.id.button_buy);
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buy_intent = new Intent(context, FindItem.class);
                startActivity(buy_intent);
            }
        });
    }

    /**
     * @param content   Message content
     * @param target_id Destination Id
     */
    public void sendMessage(final String content, final String target_id) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String target;
                if (target_id == null)
                    target = "APA91bF1gTTApift4O2xUQ4Dsa-bzXyDWPQCPvYOHqlcOpqIsCaayVJTDELjw67B6OpoSNa5BseKPYJALRddRkklMGCO6SFIlx8N30xfWvopzMlgYclNhvABLTd57NTmzfdUXwgD6L-78znkj3lOIwzIlUu9ZzKjAg";
                else target = target_id;
                Sender s = new Sender(GcmController.API_KEY);
                Message m = new Message.Builder()
                        .addData("message", content)
                        .addData("sender", "Server")
                        .build();
                try {
                    Result send = s.send(m, target, 5); // Try 5 times
                    Log.i(TAG, send.toString());
                    return send.toString();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                    return ex.getMessage();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    return ex.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String msg) {
                // Show something ???
            }
        }.execute(null, null, null);
    }

    /**
     * @param content   Message content
     * @param target_list Destination Id
     */
    public void sendMessage(final String content, final List<String> target_list) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String target;
                Sender s = new Sender(GcmController.API_KEY);
                Message m = new Message.Builder()
                        .addData("message", content)
                        .addData("sender", "Server")
                        .build();
                try {
                    MulticastResult send = s.send(m, target_list, 5); // Try 5 times
                    Log.i(TAG, send.toString());
                    return send.toString();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                    return ex.getMessage();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    return ex.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String msg) {
                // Show something ???
            }
        }.execute(null, null, null);
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

    /* GCM */
    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        GcmController.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
