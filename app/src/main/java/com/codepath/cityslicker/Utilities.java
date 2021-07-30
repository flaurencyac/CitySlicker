package com.codepath.cityslicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

    public static void setDialogViews(Context context, Place place, TextView tvPlaceName, TextView tvAddress, TextView tvPrice, TextView tvPhoneNumber,
                                      TextView tvRating, TextView tvNumRatings, TextView tvWebsiteLink, TextView tvOpeningHours,  RatingBar ratingBar) {
        tvPlaceName.setText(place.getName());
        tvAddress.setText(place.getAddress());
        if (place.getPriceLevel() != null) {
            tvPrice.setText(Utilities.convertToDollars(place.getPriceLevel()));
        }
        if (place.getPhoneNumber() != null) {
            tvPhoneNumber.setText(place.getPhoneNumber());
        } else {
            tvPhoneNumber.setText("Not available for this location");
        }
        tvRating.setText("" + place.getRating());
        tvNumRatings.setText("(" + place.getUserRatingsTotal()+")");
        if (place.getRating() != null) {
            ratingBar.setRating(place.getRating().floatValue());
        }
        if (place.getWebsiteUri()!= null) {
            tvWebsiteLink.setText(""+ place.getWebsiteUri());
        }
        if (place.getOpeningHours() != null) {
            tvOpeningHours.setText(Utilities.getOpeningHours(place));
        }
    }


}
