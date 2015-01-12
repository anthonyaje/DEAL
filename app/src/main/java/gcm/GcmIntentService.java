/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.Toast;

import com.deal.aje.deal.*;
import com.deal.aje.deal.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.concurrent.atomic.AtomicInteger;


// References
// https://developer.android.com/google/gcm/index.html
// https://code.google.com/p/gcm/

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
//        showToast("message type: " + messageType);
        Log.d(com.deal.aje.deal.Constants.TAG, GcmIntentService.class + " Message Type: " + messageType);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            showToast(extras.toString());
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e(com.deal.aje.deal.Constants.TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e(com.deal.aje.deal.Constants.TAG, "Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//                Log.i(com.deal.aje.deal.Constants.TAG, GcmIntentService.class + " (Before send Notification) Extras: " + extras.toString());
                // Post notification of received message.
                sendNotification(extras);
                // extras.get("sender") --> will get the username of the sender
//                Log.i(com.deal.aje.deal.Constants.TAG, GcmIntentService.class + " (After send Notification) Extras: " + extras.toString());
            }
        }
        Log.i(com.deal.aje.deal.Constants.TAG, GcmIntentService.class + " FINAL Extras: " + intent.getExtras().toString());
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle extras) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        String msg = extras.get("message").toString();
        String title = extras.get("title").toString();

        // Where to show the notification when receive notification
        Intent open = new Intent(getApplicationContext(), ComposeMessage.class);
        Log.d(Constants.TAG, GcmIntentService.class + " Seller id: " + extras.get("sellerid").toString());
        Log.d(Constants.TAG, GcmIntentService.class + " Buyer id : " + extras.get("buyerid").toString());
        Log.d(Constants.TAG, GcmIntentService.class + " Offer id : " + extras.get("offerid").toString());
        Log.d(Constants.TAG, GcmIntentService.class + " Requestid: " + extras.get("reqid").toString());
        open.putExtra("sellerid", extras.get("sellerid").toString());
        open.putExtra("buyerid", extras.get("buyerid").toString());
        open.putExtra("offerid", extras.get("offerid").toString());
        open.putExtra("reqid", extras.get("reqid").toString());
        Log.d(Constants.TAG, GcmIntentService.class + " Extras: " + extras.toString());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                open, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
//                        .setStyle(new NotificationCompat.InboxStyle().addLine(msg))
                        .setWhen(System.currentTimeMillis())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setAutoCancel(true)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        // One instance (old)
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        // Handle multiple instance of notifications with Atomic Integer
        if(title.equals(Constants.MESSAGE_FROM_SELLER)) {
            mNotificationManager.notify(GcmController.GCM_ATOMIC_NOTIFICATION_ID.getAndIncrement(), mBuilder.build());
        }
        else if(title.equals(Constants.MESSAGE_NEW_MAIL)) {
            mNotificationManager.notify(Constants.MESSAGE_NOTIFICATION_ID, mBuilder.build());
        }
        Log.i(Constants.TAG, GcmIntentService.class + " MSG_ID: " + GcmController.GCM_ATOMIC_NOTIFICATION_ID.get());
    }
}
