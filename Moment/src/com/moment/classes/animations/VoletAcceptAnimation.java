package com.moment.classes.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

public class VoletAcceptAnimation extends Animation {
	
	int height;
    View view;
    int initialHeight;
    //State true= replié, false = déplié
    boolean stateClosed;
    float pixelRatio;
    

    public VoletAcceptAnimation(View view, int height, boolean stateClosed, float pixelRatio) {
        this.view = view;
        this.initialHeight = view.getLayoutParams().height;
        this.height = height;
        this.stateClosed = stateClosed;
        this.pixelRatio = pixelRatio;
        
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
    	
    	if(stateClosed){
    			
    		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.getLayoutParams();
        	params.height = (int)(height*interpolatedTime*pixelRatio);
        	view.setLayoutParams(params);

    	}
    	else{
    		
    		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.getLayoutParams();
        	params.height = initialHeight - (int)((height-30)*interpolatedTime*pixelRatio);
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
