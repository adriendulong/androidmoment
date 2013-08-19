package com.moment;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.moment.activities.MomentActivity;
import com.moment.activities.MomentInfosActivity;
import com.moment.activities.TimelineActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by adriendulong on 05/07/13.
 */
public class MomentBroadcastReceiver extends BroadcastReceiver {

    private int CHAT_PUSH = 3;
    private int PHOTO_PUSH = 2;
    private int INVIT_PUSH = 0;

    static final String TAG = "GCMDemo";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
    private String title, message;
    private int type_id;
    private Long moment_id;
    private Intent destIntent;

    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        ctx = context;
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            sendNotification("Send error: " + intent.getExtras().toString());
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            sendNotification("Deleted messages on server: " +
                    intent.getExtras().toString());
        } else {
            Log.v(TAG, intent.getExtras().toString());
            Log.v(TAG, intent.getExtras().getString("data"));
            sendNotification(intent.getExtras().getString("data"));
        }
        setResultCode(Activity.RESULT_OK);
    }

    // Put the GCM message into a notification and post it.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        try{
            JSONArray array = new JSONArray(msg);
            JSONObject jsonMessage = array.getJSONObject(0);
            title = jsonMessage.getString("titre");
            message = jsonMessage.getString("message");
            moment_id = jsonMessage.getLong("moment_id");
            type_id = jsonMessage.getInt("type_notif");

        }catch(JSONException e){
            Log.e(TAG, "JSON PRoblem");
        }

        if(message!=""){

            if(type_id==INVIT_PUSH){
                Log.v(TAG, "INVITATION PUSH");
                destIntent = new Intent(ctx, MomentInfosActivity.class);
                destIntent.putExtra("type_id", type_id);
                destIntent.putExtra("moment_id", moment_id);
                destIntent.putExtra("message", message);
                destIntent.putExtra("precedente", "push");
                Log.v(TAG, message);
            }
            else if(type_id==CHAT_PUSH){
                Log.v(TAG, "PUSH CHAT");
                destIntent = new Intent(ctx, MomentInfosActivity.class);
                destIntent.putExtra("type_id", type_id);
                destIntent.putExtra("moment_id", moment_id);
                destIntent.putExtra("message", message);
                destIntent.putExtra("precedente", "push");
                Log.d(TAG, "Type : "+type_id);
            }
            else if(type_id==PHOTO_PUSH){
                Log.v(TAG, "PHOTO PUSH");
                destIntent = new Intent(ctx, MomentInfosActivity.class);
                destIntent.putExtra("type_id", type_id);
                destIntent.putExtra("moment_id", moment_id);
                destIntent.putExtra("message", message);
                destIntent.putExtra("precedente", "push");
                Log.v(TAG, "PHOTO PUSH : "+destIntent.getIntExtra("type_id", 0));
            }


            mNotificationManager = (NotificationManager)
                    ctx.getSystemService(Context.NOTIFICATION_SERVICE);

            PendingIntent contentIntent = PendingIntent.getActivity(ctx, Calendar.getInstance().get(Calendar.MILLISECOND),
                    destIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(ctx)
                            .setSmallIcon(R.drawable.app_icon)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setContentText(message)
                            .setAutoCancel(true);

            mBuilder.setContentIntent(contentIntent);
            mBuilder.setLights(Color.argb(100, 255, 156, 8), 500, 500);
            long[] pattern = {500,500,500,500,500,500,500,500,500};
            mBuilder.setVibrate(pattern);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mNotificationManager.notify(Calendar.getInstance().get(Calendar.MILLISECOND), mBuilder.build());




        }
    }
}
