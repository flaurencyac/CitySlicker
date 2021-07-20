package com.codepath.cityslicker;

import com.codepath.cityslicker.models.Trip;

import org.parceler.Parcel;

@Parcel
public class TripParcelableObject {
    Trip trip;

    public TripParcelableObject() {}

    public void setTrip(Trip trip) {this.trip = trip;}

    public Trip getTrip() {return trip;}
}
