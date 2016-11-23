package com.kiminonawa.mydiary.shared;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daxia on 2016/11/23.
 */

public class BitmapHelper {

    public static Bitmap getBitmapFromReturnedImage(Context context, Uri selectedImage, int reqWidth, int reqHeight) throws IOException {

        InputStream inputStream = context.getContentResolver().openInputStream(selectedImage);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        // close the input stream
        inputStream.close();

        // reopen the input stream
        inputStream = context.getContentResolver().openInputStream(selectedImage);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();
        return bitmap;
    }

    public static Bitmap getBitmapFromTempFileSrc(String tempFileSrc, int reqWidth, int reqHeight) throws IOException {

        InputStream inputStream = new FileInputStream(tempFileSrc);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        // close the input stream
        inputStream.close();

        // reopen the input stream
        inputStream = new FileInputStream(tempFileSrc);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();
        return bitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final double heightRatio = Math.ceil((double) height / (double) reqHeight);
            final double widthRatio = Math.ceil((double) width / (double) reqWidth);

//            Log.e("BitmapHelper", "w,h=" + options.outHeight + "  , " + options.outWidth);
//            Log.e("BitmapHelper", "req w,h=" + reqWidth + "  , " + reqHeight);

            // Choose the max ratio as inSampleSize value, I hope it can show fully without scrolling
            inSampleSize = (int) Math.max(heightRatio, widthRatio);

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).
            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
//        Log.e("BitmapHelper", "inSampleSize=" + inSampleSize);
        return inSampleSize;
    }
}
