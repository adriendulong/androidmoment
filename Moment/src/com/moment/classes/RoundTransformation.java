package com.moment.classes;

import android.graphics.*;
import com.squareup.picasso.Transformation;

/**
 * Created by adriendulong on 16/07/13.
 */
public class RoundTransformation implements Transformation {
    @Override
    public Bitmap transform(Bitmap bitmap) {
        Bitmap output;
        if(bitmap.getWidth() > bitmap.getHeight()) output = Bitmap.createBitmap(bitmap.getHeight(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        else output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect;
        if(bitmap.getWidth() > bitmap.getHeight()) rect = new Rect(0, 0, bitmap.getHeight(), bitmap.getHeight());
        else rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getHeight()/2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    @Override
    public String key() {
        return "round()";
    }
}
