package gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.deal.aje.deal.home;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Lalala on 1/11/15.
 */
public class GcmController {
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    public static final String SENDER_ID = "782343216258";
    /**
     * API KEY for GCM
     */
    public static final String API_KEY = "AIzaSyB0fIJkZ4j_N94utV5c-i90dgNE43-xCB4";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int NUMBER_OF_TRIAL = 5;
    public static String regid;
    public static GoogleCloudMessaging gcm;

    private GcmController() {
    }

    public static GcmController getInstance() {
        return WriterHolder.INSTANCE;
    }

    private static class WriterHolder {

        private static final GcmController INSTANCE = new GcmController();
    }

    /* GCM */

    /* Google Cloud Messaging */

    AtomicInteger msgId = new AtomicInteger();


    /**
     * Tag used on log messages.
     */
    static final String TAG = "DEAL"+GcmController.class;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "1";
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                activity.finish();
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
    public String getRegistrationId(Context context) {
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
    public void registerInBackground(final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(GcmController.SENDER_ID);
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
        return context.getSharedPreferences(home.class.getSimpleName(),
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

    /**
     * @param content   Message content
     * @param target_id Destination Id
     */
    public void sendMessage(final String content, final String target_id) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String target;
                target = target_id;
                Sender s = new Sender(GcmController.API_KEY);
                Message m = new Message.Builder()
                        .addData("message", content)
                        .addData("sender", "Server")
                        .build();
                try {
                    Result send = s.send(m, target, NUMBER_OF_TRIAL); // Try 5 times
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
                    MulticastResult send = s.send(m, target_list, NUMBER_OF_TRIAL); // Try 5 times
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
}
