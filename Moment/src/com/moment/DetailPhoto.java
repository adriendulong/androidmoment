package com.moment;

import com.moment.MomentInfosActivity.Exchanger;
import com.moment.classes.ImageLoadTask;
import com.moment.classes.Photo;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageView;

public class DetailPhoto extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_detail_photo);
        ImageView imageView = (ImageView) findViewById(R.id.photo_moment_detail);
        
        int position = getIntent().getIntExtra("position", 0);
        System.out.println("POSITION " + position);
        Photo photo = Exchanger.photos.get(position);
        
        photo.loadPhoto(imageView);
    }

}
