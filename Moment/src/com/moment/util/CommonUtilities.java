package com.moment.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public final class CommonUtilities {

    static final String SERVER_URL = null;

    public static final String SENDER_ID = "249628823523";

    static final String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

    static final String TAG = "GCMDemo";

    static final String EXTRA_MESSAGE = "message";

    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    static public String getDeviceName() {
    	  String manufacturer = Build.MANUFACTURER;
    	  String model = Build.MODEL;
    	  if (model.startsWith(manufacturer)) {
    	    return capitalize(model);
    	  } else {
    	    return capitalize(manufacturer) + " " + model;
    	  }
    	}

    	private static String capitalize(String s) {
    	  if (s == null || s.length() == 0) {
    	    return "";
    	  }
    	  char first = s.charAt(0);
    	  if (Character.isUpperCase(first)) {
    	    return s;
    	  } else {
    	    return Character.toUpperCase(first) + s.substring(1);
    	  }
    	}

    public static void disableHardwareRendering(View v) {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public static int longToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isValidTel(String target) {
        target = target.replaceAll("[^\\d+]", "");
        if( target.length() == 10 || target.length() == 12)
        {
            if(target.subSequence(0,3).equals("+33") && target.length() == 12)
            {
                try {
                    Integer.parseInt(target.substring(3));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else if( (target.subSequence(0,2).equals("06") || target.subSequence(0,2).equals("07")) && target.length() == 10)
            {
                try {
                    Integer.parseInt(target);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public final static void popAlert(String title, String message, String cancel, Context context){
        // NO SMS HERE :(
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public static boolean isSuccessRequest(HttpResponse response){
        Integer[] successCodes = {200, 201, 202, 203, 204, 205, 206, 207, 210, 226};
        if(Arrays.asList(successCodes).contains(response.getStatusLine().getStatusCode())) return true;
        return false;
    }

    public static JSONObject getJSONResponse(HttpResponse response){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            return new JSONObject(tokener);
        }catch(IOException e){
            return null;
        }catch(JSONException e){
            return null;
        }

    }
}
