package com.codepath.cityslicker.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Place")
public class Place extends ParseObject {
    public static final String KEY_PLACE_NAME = "placeName";
    public static final String KEY_ID = "placeID";
    public static final String KEY_TRIP = "trip";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_REGION = "region";

    public String getName() {return getString(KEY_PLACE_NAME); }

    public void setName(String name) { put(KEY_PLACE_NAME, name); }

    public String getPlaceID() {return getString(KEY_ID); }

    public void setPlaceID(String placeID) { put(KEY_ID, placeID); }

    public ParseObject getTrip() {return getParseObject(KEY_TRIP);}

    public void setStrip(ParseObject trip) {put(KEY_TRIP, trip); }

    public Date getDate() {return getDate(KEY_DATE);}

    public void setDate(Date date) {put(KEY_DATE, date);}

    public String getTime() {return getString(KEY_TIME); }

    public void setTime(String time) {put(KEY_TIME, time);}

    public String getRegions() {return getString(KEY_REGION);}

    public void setRegions(String regions) {put(KEY_REGION, regions);}

}
