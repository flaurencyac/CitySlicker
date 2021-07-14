package com.codepath.cityslicker.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Place")
public class Place extends ParseObject {
    public static final String KEY_PLACE_NAME = "placeName";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_ID = "placeID";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_RATING = "rating";
    public static final String KEY_NUM_OF_RATINGS = "numOfRatings";
    public static final String KEY_IMAGE = "image";
    // TODO finish declaring latLng, websiteUri, openingHours, and addressComponents (Q: is there a Parse type for each of these?)

    public Place() {}

}
