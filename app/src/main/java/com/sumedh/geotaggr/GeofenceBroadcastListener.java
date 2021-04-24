package com.sumedh.geotaggr;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.sumedh.geotaggr.activities.SplashScreenActivity;
import com.sumedh.geotaggr.domain.Tag;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class GeofenceBroadcastListener extends BroadcastReceiver {
    private final String TAG = "GeofenceBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Geofence triggered");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Geofence triggeringGeofence = geofencingEvent.getTriggeringGeofences().get(0);
            triggeringGeofence.getRequestId();

            sendNotification(triggeringGeofence.getRequestId(), context);
        } else {
            Log.e(TAG, "Error when triggering geofence");
        }
    }

    private void sendNotification(String tagIdString, Context context) {
        Integer tagId = Integer.parseInt(tagIdString);
        Tag tag = TagDatabase.getInstance(context).tagDao().getTagById(tagId);

        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(context.getResources().getString(R.string.notification_alert))
                .setContentText(tag.getTagText())
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(tagId, builder.build());
    }
}
