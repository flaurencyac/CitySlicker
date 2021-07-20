package com.codepath.cityslicker;

import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.google.android.libraries.places.api.model.Place;

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class TripParcelableObject {
    Trip trip;
    ArrayList<ArrayList<Place>> places = new ArrayList<ArrayList<Place>>();
    ArrayList<ArrayList<Spot>> spots = new ArrayList<ArrayList<Spot>>();

    public TripParcelableObject() {}

    public void setTrip(Trip trip) {this.trip = trip;}

    public Trip getTrip() {return trip;}

    public void setPlacesInParcel(ArrayList<ArrayList<Place>> places) {this.places = places;}

    public ArrayList<ArrayList<Place>> getPlacesInParcel() {return places;}

    public void setSpotsInParcel(ArrayList<ArrayList<Spot>> spots) {this.spots = spots;}

    public ArrayList<ArrayList<Spot>> getSpotsInParcel() {return spots;}
}
