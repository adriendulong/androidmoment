package com.moment.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.moment.R;

import static com.moment.classes.CommonUtilities.SENDER_ID;

public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);



    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");


    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message. Extras: " + intent.getExtras());
       String title = intent.getStringExtra("titre");
       String message = intent.getStringExtra("message");
        


        generateNotification(context, title, message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");




    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);

    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {

        Log.i(TAG, "Received recoverable error: " + errorId);


        return super.onRecoverableError(context, errorId);
    }

    private static void generateNotification(Context context, String title, String message) {
    	NotificationCompat.Builder mBuilder =
    	        new NotificationCompat.Builder(context)
    	        .setSmallIcon(R.drawable.picto_o)
    	        .setContentTitle(title)
    	        .setContentText(message);

    	Intent resultIntent = new Intent(context, TimelineActivity.class);





    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

    	stackBuilder.addParentStack(TimelineActivity.class);

    	stackBuilder.addNextIntent(resultIntent);
    	PendingIntent resultPendingIntent =
    	        stackBuilder.getPendingIntent(
    	            0,
    	            PendingIntent.FLAG_UPDATE_CURRENT
    	        );
    	mBuilder.setContentIntent(resultPendingIntent);
    	NotificationManager mNotificationManager =
    	    (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

    	mNotificationManager.notify(1111, mBuilder.build());
    }

}