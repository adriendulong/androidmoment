package com.moment.customfont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by adriendulong on 01/08/13.
 */
public class MomentButton extends Button {
    public MomentButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MomentButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MomentButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Numans-Regular.otf");
            setTypeface(tf);
        }
    }

}
