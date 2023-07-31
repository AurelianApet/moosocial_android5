package com.moosocial.moosocialapp.util.GCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.gcm.GcmListenerService;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.presentation.view.activities.SplashActivity;
import com.moosocial.moosocialapp.util.MooGlobals;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */

        SharedPreferences sharedSettings = getApplicationContext().getSharedPreferences(MooGlobals.MOO_SHARED_GLOBAL, MODE_PRIVATE);
        String sSettingNotification = sharedSettings.getString(MooGlobals.MOO_SETTING_NOTIFICATION, "1");
        if (!sSettingNotification.equals("1"))
        {
            return;
        }

        if (from.startsWith("/topics/")) {
            sendTopic(data);
        } else {
            sendNotification(data);
        }
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param data GCM message received.
     */
    private void sendNotification(Bundle data) {
        String sToken = MooGlobals.getInstance().getSharedSettings().getString(QuickstartPreferences.GCM_TOKEN, null);
        if (sToken != null) {
            Intent intent = new Intent(this, SplashActivity.class);
            String notification_id = data.getString("notification_id");
            String notification_url = data.getString("notification_url");
            String sSound = data.getString("sound");
            intent.putExtra("notification_id", notification_id);
            intent.putExtra("notification_url", notification_url);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            int color = getResources().getColor(R.color.blue);

            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(Html.fromHtml(data.getString("message")).toString())
                    .setAutoCancel(true)
                    .setColor(color)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            if (sSound.equals("1"))
            {
                notificationBuilder.setSound(defaultSoundUri);
            }

            String url = data.getString("photo_url");
            ImageRequest request = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            int height = (int) getResources().getDimension(android.R.dimen.notification_large_icon_height);
                            int width = (int) getResources().getDimension(android.R.dimen.notification_large_icon_width);

                            notificationBuilder.setLargeIcon(Bitmap.createScaledBitmap(bitmap, width, height, false));
                            sendNotifyToUser(notificationBuilder);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            sendNotifyToUser(notificationBuilder);
                        }
                    });
            MooGlobals.getInstance().getRequestQueue().add(request);
        }
    }

    public void sendNotifyToUser(NotificationCompat.Builder notificationBuilder)
    {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendTopic(Bundle data) {
        Intent intent = new Intent(this, SplashActivity.class);
        String notification_id = "";
        String notification_url = data.getString("notification_url");

        if (!notification_url.isEmpty()) {
            if (notification_url.indexOf(MooGlobals.getInstance().getConfig().urlHost) == -1) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(notification_url));
            } else {
                if (notification_url.indexOf("?") == -1) {
                    notification_url += "?android=1";
                }
            }
        }
        intent.putExtra("notification_id", notification_id);
        intent.putExtra("notification_url", notification_url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int color = getResources().getColor(R.color.blue);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(Html.fromHtml(data.getString("message")).toString())
                .setAutoCancel(true)
                .setColor(color)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);

        notificationBuilder.setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
