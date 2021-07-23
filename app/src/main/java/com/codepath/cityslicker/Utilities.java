package com.codepath.cityslicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;

public class Utilities {

    public static String convertToDollars(Integer n) {
        switch(n) {
            case 0:
                return "Free";
            case 1:
                return "$";
            case 2:
                return "$$";
            case 3:
                return "$$$";
            case 4:
                return "$$$$";
            default:
                return "Unknown price level";
        }
    }

    public static String getOpeningHours(Place place) {
        return String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s",
                place.getOpeningHours().getWeekdayText().get(0),
                place.getOpeningHours().getWeekdayText().get(1),
                place.getOpeningHours().getWeekdayText().get(2),
                place.getOpeningHours().getWeekdayText().get(3),
                place.getOpeningHours().getWeekdayText().get(4),
                place.getOpeningHours().getWeekdayText().get(5),
                place.getOpeningHours().getWeekdayText().get(6));
    }

    public static void fetchPhoto(Context context, List<PhotoMetadata> photoMetadataList, ImageView view, String TAG) {
        Places.initialize(context, BuildConfig.MAPS_API_KEY);
        PlacesClient placesClient = Places.createClient(context);
        if (photoMetadataList == null || photoMetadataList.isEmpty()) {
            Log.i(TAG, "No photo metadata.");
            view.setImageBitmap(null);
        } else {
            final PhotoMetadata photoMetadata = photoMetadataList.get(0);
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                final Bitmap bitmap = fetchPhotoResponse.getBitmap();
                view.setImageBitmap(bitmap);
                Log.i(TAG, "photo fetched");
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    Log.e(TAG, "Place not found: " + statusCode);
                }
            });
        }
    }


}
