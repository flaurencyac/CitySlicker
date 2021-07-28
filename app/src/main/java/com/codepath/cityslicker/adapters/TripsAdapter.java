package com.codepath.cityslicker.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.cityslicker.R;
import com.codepath.cityslicker.TripParcelableObject;
import com.codepath.cityslicker.activities.DetailsActivity;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {
    private static final String TAG = "TripsAdapter";
    private final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING,
            Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.USER_RATINGS_TOTAL, Place.Field.PRICE_LEVEL, Place.Field.TYPES, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS);

    Context context;
    List<Trip> trips;

    public TripsAdapter(Context context, List<Trip> trips) {
        this.context = context;
        this.trips = trips;
    }

    @NonNull
    @NotNull
    @Override
    public TripsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View tripView = LayoutInflater.from(context).inflate(R.layout.item_trip, parent, false);
        return new ViewHolder(tripView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TripsAdapter.ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTripTitle;

        public ViewHolder (View itemView) {
            super(itemView);
            tvTripTitle = itemView.findViewById(R.id.tvFriendName);
        }

        public void bind(Trip trip) {
            tvTripTitle.setText(trip.getTripName());

            tvTripTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Trip trip = trips.get(position);
                    //openTripDetails(trip);
                }
            });
        }
    }

//    public void openTripDetails(Trip trip) {
//        PlacesClient placesClient = Places.createClient(context);
//        ArrayList<ArrayList<Place>> allPlaces = new ArrayList<ArrayList<Place>>();
//        ArrayList<ArrayList<String>> allPlaceIds = new ArrayList<ArrayList<String>>();
//        ArrayList<ArrayList<Spot>> allSpots = new ArrayList<ArrayList<Spot>>();
//        ArrayList<String> cityIds = new ArrayList<>();
//        ArrayList<String> cityNames = new ArrayList<>();
//
//        Intent intent = new Intent(context, DetailsActivity.class);
//        TripParcelableObject tripParcelableObject = new TripParcelableObject();
//        tripParcelableObject.setTrip(trip);
//
//        allPlaceIds = Trip.parseForPlaces(trip.getPlaces());
//        intent.putExtra("allPlaceIds", allPlaceIds);
//
//        cityIds = trip.getRegions();
//        intent.putExtra("cityIdList", cityIds);
//        cityNames = trip.getCityNames();
//        intent.putExtra("cityNames", cityNames);
//
//        for (ArrayList<String> city : allPlaceIds) {
//            ArrayList<Place> placesInCity = new ArrayList<>();
//            for (String id : city) {
//                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(id, placeFields);
//                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
//                    Place place = response.getPlace();
//                    placesInCity.add(place);
//                }).addOnFailureListener((exception) -> {
//                    if (exception instanceof ApiException) {
//                        final ApiException apiException = (ApiException) exception;
//                        Log.e(TAG, "Place not found: "+exception.getMessage());
//                    }});
//            }
//            allPlaces.add(placesInCity);
//        }
//        for (ArrayList<String> city : allPlaceIds) {
//            ArrayList<Spot> spotsInCity = new ArrayList<>();
//            for (String id : city) {
//                ParseQuery<Spot> query = ParseQuery.getQuery("Spot");
//                query.whereEqualTo("trip", trip.getObjectId());
//                query.whereEqualTo("placeId", id);
//                query.findInBackground(new FindCallback<Spot>() {
//                    @Override
//                    public void done(List<Spot> spots, ParseException e) {
//                        spotsInCity.add(spots.get(0));
//                    }
//                });
//            }
//            allSpots.add(spotsInCity);
//        }
//        tripParcelableObject.setPlacesInParcel(allPlaces);
//        tripParcelableObject.setSpotsInParcel(allSpots);
//
//        intent.putExtra("tripObj", (Parcelable) tripParcelableObject);
//        intent.putExtra("tripId", trip.getObjectId());
//
//        intent.putExtra("trip", Parcels.wrap(trip));
//        startActivity(intent);
//    }
}
