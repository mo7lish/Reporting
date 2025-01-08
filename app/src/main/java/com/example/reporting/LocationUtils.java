package com.example.reporting;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtils {
    
    public static String getAddressFromLocation(Context context, double latitude, double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        
        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            StringBuilder addressBuilder = new StringBuilder();
            
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressBuilder.append(address.getAddressLine(i));
                if (i < address.getMaxAddressLineIndex()) {
                    addressBuilder.append(", ");
                }
            }
            
            return addressBuilder.toString();
        }
        
        return null;
    }
}