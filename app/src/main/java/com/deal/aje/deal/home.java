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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import gcm.Message;
import gcm.MulticastResult;
import gcm.Result;
import gcm.Sender;
import mongo.Connect;
import mongo.controller.DbController;
import mongo.entity.Setting;
import mongo.entity.User;

import com.facebook.Session;


public class home extends ActionBarActivity {
    SharedPreferences sp = null;
    /* Google Cloud Messaging */
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    public static final String SENDER_ID = "782343216258";

    public static final String API_KEY = "AIzaSyB0fIJkZ4j_N94utV5c-i90dgNE43-xCB4";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "DEAL";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "1";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String regid;
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

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        context = getApplicationContext();
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

//            if (regid.isEmpty())
            {
                registerInBackground();
            }
            Log.i(TAG, regid);
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

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
//                Intent sell_intent = new Intent(context, SellItem.class);
//                startActivity(sell_intent);
                sendMessage("Sell",
                        "APA91bF1gTTApift4O2xUQ4Dsa-bzXyDWPQCPvYOHqlcOpqIsCaayVJTDELjw67B6OpoSNa5BseKPYJALRddRkklMGCO6SFIlx8N30xfWvopzMlgYclNhvABLTd57NTmzfdUXwgD6L-78znkj3lOIwzIlUu9ZzKjAg");
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
                Sender s = new Sender(API_KEY);
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
                Sender s = new Sender(API_KEY);
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
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i(TAG, msg);

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                // Display something ???
            }
        }.execute(null, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(home.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }
}
