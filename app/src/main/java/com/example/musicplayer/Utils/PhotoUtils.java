package com.example.musicplayer.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;

public class PhotoUtils {
    public static Bitmap getScalePhoto(String photoPath, int desHeight, int desWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        int scaleFactor = Math.max(1, Math.min(
                srcWidth / desWidth, srcHeight / desHeight));
        options.inJustDecodeBounds=false;
        options.inSampleSize = scaleFactor;

        return
                BitmapFactory.decodeFile(photoPath, options);
    }

    public static Bitmap getScalePhoto(String photoPath, Activity activity){
        Point point=new Point();

        activity.getWindowManager().getDefaultDisplay().getSize(point);

        return getScalePhoto(photoPath, point.x,point.y);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

}
