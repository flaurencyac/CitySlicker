package com.codepath.cityslicker.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Comparator;
import java.util.Date;

@ParseClassName("Spot")
public class Spot extends ParseObject {
    public static final String KEY_PLACE_NAME = "name";
    public static final String KEY_ID = "placeId";
    public static final String KEY_TRIP = "trip";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_REGION = "regionId";

    public String getName() {return getString(KEY_PLACE_NAME); }

    public void setName(String name) { put(KEY_PLACE_NAME, name); }

    public String getPlaceID() {return getString(KEY_ID); }

    public void setPlaceID(String placeID) { put(KEY_ID, placeID); }

    public ParseObject getTrip() {return getParseObject(KEY_TRIP);}

    public void setTrip(ParseObject trip) {put(KEY_TRIP, trip); }

    public Date getDate() {return getDate(KEY_DATE);}

    public void setDate(Date date) {put(KEY_DATE, date);}

    public String getTime() {return getString(KEY_TIME); }

    public void setTime(String time) {put(KEY_TIME, time);}

    public String getCityId() {return getString(KEY_REGION);}

    public void setCityId(String cityId) {put(KEY_REGION, cityId);}

    // if 0: they are equal, if int > 0 s1 comes after s2, if int < 0 s1 comes before s2
    public static Comparator<Spot> spotDateTimeComparator = new Comparator<Spot>() {
        @Override
        public int compare(Spot s1, Spot s2) {
            if (s1.getDate() == null && s1.getTime() == null) {
                return -1;
            }
            if (s2.getDate() == null && s2.getTime() == null) {
                return 1;
            }
            if (s1.getDate() == s2.getDate()) {
                double t1 = extractDate(s1.getTime());
                double t2 = extractDate(s2.getTime());
                return Double.compare(t1, t2);
            } else {
                return s1.getDate().compareTo(s2.getDate());
            }
        }

        public double extractDate(String timeString) {
            double time;
            time = Integer.parseInt(timeString.substring(0,2));
            time += (Integer.parseInt(timeString.substring(3, 5)))/100;
            return time;
        }
    };
}
