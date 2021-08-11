package com.codepath.cityslicker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.cityslicker.activities.DetailsActivity;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
            tvPhoneNumber.setText("Phone number: "+place.getPhoneNumber());
        } else {
            tvPhoneNumber.setText("Phone number unavailable");
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

    public static void openTripDetails(Trip trip, Context context, String TAG) {
        PlacesClient placesClient = Places.createClient(context);
        ArrayList<ArrayList<Place>> allPlaces = new ArrayList<ArrayList<Place>>();
        ArrayList<ArrayList<String>> allSpotIds = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Spot>> allSpots = new ArrayList<ArrayList<Spot>>();
        ArrayList<String> cityIds = new ArrayList<>();
        ArrayList<String> cityNames = new ArrayList<>();

        Intent intent = new Intent(context, DetailsActivity.class);
        TripParcelableObject tripParcelableObject = new TripParcelableObject();
        tripParcelableObject.setTrip(trip);

        allSpotIds = trip.getPlacesList();
        intent.putExtra("allPlaceIds", allSpotIds);

        cityIds = trip.getRegions();
        intent.putExtra("cityIdList", cityIds);
        cityNames = trip.getCityNames();
        intent.putExtra("cityNames", cityNames);

        ArrayList<ArrayList<String>> finalAllSpotIds = allSpotIds;
        for (int i = 0; i < cityIds.size() ; i ++) {
            String cityId = cityIds.get(i);
            ParseQuery<Spot> query = ParseQuery.getQuery("Spot");
            query.whereEqualTo("trip", trip);
            query.whereEqualTo("regionId", cityId);
            query.orderByAscending(Spot.KEY_DATE);
            int finalI = i;
            query.findInBackground(new FindCallback<Spot>() {
                @Override
                public void done(List<Spot> spots, ParseException e) {
                    ArrayList<Spot> copy = new ArrayList<> ((ArrayList<Spot>) spots);
                    allSpots.add(copy);
                    if (finalI == finalAllSpotIds.size()-1) {
                        Thread t = new Thread() {
                            public void run() {
                                for (ArrayList<Spot> city : allSpots) {
                                    Future<ArrayList<Place>> future = Utilities.fetchPlaces(city, placesClient, TAG);
                                    while(!future.isDone()) {
                                        System.out.println("Fetching places...");
                                    }
                                    ArrayList<Place> result = new ArrayList<>();
                                    try {
                                        result = future.get();
                                        allPlaces.add(result);
                                    } catch (ExecutionException executionException) {
                                        executionException.printStackTrace();
                                    } catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    }
                                }
                            }
                        };
                        t.start();
                        startDetailsActivity(intent, ((MainActivity)context), tripParcelableObject, allPlaces, allSpots, trip);
                    }
                }
            });
        }
    }

    public static Future<ArrayList<Place>> fetchPlaces(ArrayList<Spot> spotsInCity, PlacesClient placesClient, String TAG) {
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING,
                Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.USER_RATINGS_TOTAL, Place.Field.PRICE_LEVEL, Place.Field.TYPES, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            ArrayList<Place> placesInCity = new ArrayList<>();
            for (int j = 0; j < spotsInCity.size(); j++) {
                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(spotsInCity.get(j).getPlaceID(), placeFields);
                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    placesInCity.add(place);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                    } else {
                        Log.e(TAG, "Other exception: " + exception.getMessage());
                    }
                });
            }
        });
        return (Future<ArrayList<Place>>) placesInCity;
    }


    public static void startDetailsActivity(Intent intent, Context context, TripParcelableObject tripParcelableObject, ArrayList<ArrayList<Place>> allPlaces, ArrayList<ArrayList<Spot>> allSpots, Trip trip) {
        tripParcelableObject.setPlacesInParcel(allPlaces);
        tripParcelableObject.setSpotsInParcel(allSpots);

        intent.putExtra("tripObj", Parcels.wrap(tripParcelableObject));
        intent.putExtra("tripId", trip.getObjectId());
        intent.putExtra("trip", Parcels.wrap(trip));

        context.startActivity(intent);
    }

}
