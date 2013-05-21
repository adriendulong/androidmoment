package com.moment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.moment.classes.AppMoment;
import com.moment.classes.Images;

public class ProfilActivity extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        Log.d("URMMMMM", AppMoment.getInstance().user.getPicture_profile_url());
        
        // Si elle existe on recupere l'image du profil
        if(AppMoment.getInstance().user.getPicture_profile_url()!=null) {
        	Log.d("URMMMMM", AppMoment.getInstance().user.getPicture_profile_url());
        	
        	AsyncHttpClient client = new AsyncHttpClient();
        	String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };
        	client.get(AppMoment.getInstance().user.getPicture_profile_url(), new BinaryHttpResponseHandler(allowedContentTypes) {
        		
        	    @Override
        	    public void onSuccess(byte[] fileData) {
        	        // Do something with the file
        	    	ImageView profilePictureImageView = (ImageView)findViewById(R.id.profile_picture);
        	    	Log.d("SUCCESS", "SUCCESSS");
        	    	
        	    	InputStream is = new ByteArrayInputStream(fileData);
        	    	Bitmap bmp = BitmapFactory.decodeStream(is);
        	    	
        	    	profilePictureImageView.setImageBitmap(Images.getRoundedCornerBitmap(bmp));
        	    }
        	    
        	    @Override
        	    public void handleFailureMessage(Throwable e, byte[] responseBody) {
        	    	Log.d("RATEE", "RATEE");
        	        onFailure(e, responseBody);
        	    }
        	});
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//getSupportMenuInflater().inflate(R.menu.activity_profil, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
