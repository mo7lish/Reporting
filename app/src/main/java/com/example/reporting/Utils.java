package com.example.reporting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class Utils {
    // Media related methods
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
            return compressImage(context, imageUri, maxSize).length <= maxSize;
        } catch (IOException e) {
            return false;
        }
    }

    // Location related methods
    public static String getAddressFromLocation(Context context, double latitude, double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        }
        return null;
    }

    // String related methods
    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        String[] words = input.trim().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(" ");
            String word = words[i];
            result.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase());
        }
        return result.toString();
    }
}