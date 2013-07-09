package com.moment;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.moment.activities.MomentActivity;
import com.moment.activities.MomentInfosActivity;
import com.moment.activities.TimelineActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adriendulong on 05/07/13.
 */
public class MomentBroadcastReceiver extends BroadcastReceiver {

    private int CHAT_PUSH = 3;
    private int PHOTO_PUSH = 2;
    private int INVIT_PUSH = 0;

    private Intent destIntent;

    static final String TAG = "GCMDemo";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
    private String title, message;
    private int type_id;
    private Long moment_id;

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

            if(type_id==CHAT_PUSH){
                Log.v(TAG, "PUSH CHAT");
                destIntent = new Intent(ctx, MomentInfosActivity.class);
                destIntent.putExtra("type_id", type_id);
                destIntent.putExtra("moment_id", moment_id);
                destIntent.putExtra("message", message);
                destIntent.putExtra("precedente", "push");
            }
            else if(type_id==PHOTO_PUSH){
                Log.v(TAG, "PHOTO PUSH");
                destIntent = new Intent(ctx, MomentInfosActivity.class);
                destIntent.putExtra("type_id", type_id);
                destIntent.putExtra("moment_id", moment_id);
                destIntent.putExtra("message", message);
                destIntent.putExtra("precedente", "push");
            }
            else if(type_id==INVIT_PUSH){
                Log.v(TAG, "INVITATION PUSH");
                destIntent = new Intent(ctx, TimelineActivity.class);
                destIntent.putExtra("type_id", type_id);
                destIntent.putExtra("moment_id", moment_id);
                destIntent.putExtra("message", message);
                destIntent.putExtra("precedente", "push");
                Log.v(TAG, message);
            }

            PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                    destIntent, 0);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(ctx)
                            .setSmallIcon(R.drawable.app_icon)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setContentText(message);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        }
    }
}
