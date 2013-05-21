package com.moment.classes.animations;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.moment.R;

public class TimelineAnimation extends Animation {
	
	int fondSizeInitial;
    View view;
    int initWidth;
    float ratioPx;
    int initHeight;
    boolean big;

    public TimelineAnimation(View view, float ratioPx, boolean big) {
        this.view = view;
        this.initWidth = view.getLayoutParams().width;
        this.initHeight = view.getLayoutParams().height;
        this.ratioPx = ratioPx;
        this.big = big;
        Log.d("Donnees", "");
        Log.d("Width", ""+this.initWidth);
        Log.d("Height", ""+this.initHeight);
        Log.d("Ratio", ""+this.ratioPx);
        
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
    	
    	if(!big){
    		Log.d("Interpolated", ""+interpolatedTime);
        	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();
        	params.height = (int)(initHeight + ratioPx*(150*interpolatedTime));
        	params.width = (int)(initWidth + ratioPx*(150*interpolatedTime));
        	params.gravity = Gravity.CENTER_HORIZONTAL;
        	view.setLayoutParams(params);
        	
        	
        	if( interpolatedTime>0.8){
        		AlphaAnimation animAlpha = new AlphaAnimation(0.0f, 1.0f);
        		animAlpha.setDuration(600);
        		
        		//On recupere les boutons
        		ImageButton btn_infos = (ImageButton)view.findViewById(R.id.btn_info_timeline);
        		ImageButton btn_chat = (ImageButton)view.findViewById(R.id.btn_chat_timeline);
        		ImageButton btn_photo = (ImageButton)view.findViewById(R.id.btn_photo_timeline);
        		
        		btn_infos.setVisibility(View.VISIBLE);
        		btn_chat.setVisibility(View.VISIBLE);
        		btn_photo.setVisibility(View.VISIBLE);
        		
        		
        		btn_infos.setAnimation(animAlpha);
        		btn_chat.setAnimation(animAlpha);
        		btn_photo.setAnimation(animAlpha);
        		
        		
        		Log.d("Alpha anim", "Ok");
        	}

    	}
    	else{
    		Log.d("Interpolated", ""+interpolatedTime);
    		
    		if(interpolatedTime<0.3){
    			ImageButton btn_infos = (ImageButton)view.findViewById(R.id.btn_info_timeline);
	    		ImageButton btn_chat = (ImageButton)view.findViewById(R.id.btn_chat_timeline);
	    		ImageButton btn_photo = (ImageButton)view.findViewById(R.id.btn_photo_timeline);
	    		
	    		btn_infos.setVisibility(View.INVISIBLE);
	    		btn_chat.setVisibility(View.INVISIBLE);
	    		btn_photo.setVisibility(View.INVISIBLE);
    		}
	    		
    		
    		
        	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();
        	params.height = (int)(initHeight - ratioPx*(150*interpolatedTime));
        	params.width = (int)(initWidth - ratioPx*(150*interpolatedTime));
        	params.gravity = Gravity.CENTER_HORIZONTAL;
        	view.setLayoutParams(params);
    	}
    	
    	
    	
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
            int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}
