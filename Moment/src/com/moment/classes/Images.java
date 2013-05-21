package com.moment.classes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class Images {

	public Images() {
		// TODO Auto-generated constructor stub
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	    Bitmap output; 
	    if(bitmap.getWidth() > bitmap.getHeight()) output = Bitmap.createBitmap(bitmap.getHeight(),
		         bitmap.getHeight(), Config.ARGB_8888);
	    else output = Bitmap.createBitmap(bitmap.getWidth(),
		         bitmap.getWidth(), Config.ARGB_8888);
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

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);

	    return output;
	    }
	
	
	public static boolean saveImageToInternalStorage(Bitmap image, Context context, String filename, int compression) {

		try {
			// Use the compress method on the Bitmap object to write image to
			// the OutputStream
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
	
			// Writing the bitmap to the output stream
			image.compress(Bitmap.CompressFormat.PNG, compression, fos);
			fos.close();
	
			return true;
			} catch (Exception e) {
			Log.e("saveToInternalStorage()", e.getMessage());
			return false;
		}
	}
	
	public static Bitmap getBitmapFromInternalStorage(String filename, Context context){
		Bitmap image = null;
		
		try {
				
			File filePath = context.getFileStreamPath(filename);
			FileInputStream fi = new FileInputStream(filePath);
			image = BitmapFactory.decodeStream(fi);
			
		} catch (Exception ex) {
			Log.e("getThumbnail() on internal storage", ex.getMessage());
		}
		
		return image;
	
	}
	
	
	
	public static Bitmap resizeBitmap(Bitmap bitmap, int maxSize){
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float ratio;
		
		
		if(width > height){
			ratio = Math.abs((float)width / (float)maxSize);
		}
		else {
			ratio = Math.abs((float)height / (float)maxSize);
		}
		
		int finalWidth = Math.round(width/ratio);
		int finalHeight = Math.round(height/ratio);
		
		return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, false); 
		
	}
	
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
		    // Raw height and width of image
		    final int height = options.outHeight;
		    final int width = options.outWidth;
		    int inSampleSize = 1;
		
		    if (height > reqHeight || width > reqWidth) {
		
		        // Calculate ratios of height and width to requested height and width
		        final int heightRatio = Math.round((float) height / (float) reqHeight);
		        final int widthRatio = Math.round((float) width / (float) reqWidth);
		
		        // Choose the smallest ratio as inSampleSize value, this will guarantee
		        // a final image with both dimensions larger than or equal to the
		        // requested height and width.
		        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		    }
		
		    return inSampleSize;
		}
	
	
	
	public static Bitmap decodeSampledBitmapFromFile(String pathName,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(pathName, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    
	    return BitmapFactory.decodeFile(pathName, options);
	}
	
	
	/**
	 * Transforme uneURI en un Absolute Paht
	 * @param contentUri
	 * @return
	 */
	
	public static String getRealPathFromURI(Uri contentUri, Context mContext) {
	    String[] proj = { MediaColumns.DATA };
	    CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
	    Cursor cursor = loader.loadInBackground();
	    int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	
	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

				int width = bm.getWidth();
		
				int height = bm.getHeight();
		
				float scaleWidth = ((float) newWidth) / width;
		
				float scaleHeight = ((float) newHeight) / height;
		
				// CREATE A MATRIX FOR THE MANIPULATION
		
				Matrix matrix = new Matrix();
		
				// RESIZE THE BIT MAP
		
				matrix.postScale(scaleWidth, scaleHeight);
		
				Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
				
				return resizedBitmap;
			}
	
	
	public static void printImageFromUrl(final ImageView targetView, final Boolean isRounded, String url){
		// On recupere la photo et on l'affiche lorsque on l'a
		System.out.println("Get photo url : "+ url);
		
			AsyncHttpClient client = new AsyncHttpClient();
	    	String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };
	    	client.get(url, new BinaryHttpResponseHandler(allowedContentTypes) {
	    		
	    	    @Override
	    	    public void onSuccess(byte[] fileData) {
	    	        
	    	    	
	    	    	InputStream is = new ByteArrayInputStream(fileData);
	    	    	Bitmap bmp = BitmapFactory.decodeStream(is);
	    	    	
	    	    	if (isRounded) targetView.setImageBitmap(Images.getRoundedCornerBitmap(bmp));
	    	    	else targetView.setImageBitmap(bmp);
	    	    }
	    	    
	    	    @Override
	    	    public void handleFailureMessage(Throwable e, byte[] responseBody) {
	    	    	Log.d("RATEE", "RATEE");
	    	        onFailure(e, responseBody);
	    	    }
	    	});
		
	}


}
