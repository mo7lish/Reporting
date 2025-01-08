package com.example.reporting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MediaUtils {
    public static byte[] compressImage(Context context, Uri imageUri, int maxSize) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(imageUri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        input.close();

        int scale = 1;
        while ((options.outWidth * options.outHeight * 4 * (1.0 / Math.pow(scale, 2))) > maxSize) {
            scale *= 2;
        }

        BitmapFactory.Options outOptions = new BitmapFactory.Options();
        outOptions.inSampleSize = scale;
        input = context.getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, outOptions);
        input.close();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
        return outputStream.toByteArray();
    }

    public static boolean isImageSizeValid(Context context, Uri imageUri, int maxSize) {
        try {
            InputStream input = context.getContentResolver().openInputStream(imageUri);
            int size = input.available();
            input.close();
            return size <= maxSize;
        } catch (IOException e) {
            return false;
        }
    }
}