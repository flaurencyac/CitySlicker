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

import com.codepath.cityslicker.MainActivity;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.TripParcelableObject;
import com.codepath.cityslicker.activities.DetailsActivity;
import com.codepath.cityslicker.activities.MapsActivity;
import com.codepath.cityslicker.models.RecommendedPlace;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {
    private final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING,
            Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.USER_RATINGS_TOTAL, Place.Field.PRICE_LEVEL, Place.Field.TYPES, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS);
    private static final String TAG = "TripsAdapter";
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
            ParseQuery<Trip> query = ParseQuery.getQuery("Trip");
            query.include(Trip.KEY_PLACES);
            query.include(Trip.KEY_CITY_NAMES);
            query.include(Trip.KEY_REGIONS);
            query.include(Trip.KEY_OWNER);
            query.include(Trip.KEY_NAME);
            query.include(Trip.KEY_BUDGET);
            query.include(Trip.KEY_START_DATE);
            query.include(Trip.KEY_END_DATE);
            query.getInBackground(trip.getObjectId(), (object, e) -> {
                if(e==null) {
                    tvTripTitle.setText(object.getString("tripName"));
                    trips.set(getAdapterPosition(), object);
                }
            });
            tvTripTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTripDetails(trips.get(getAdapterPosition()));
                }
            });
        }

        public void openTripDetails(Trip trip) {
            PlacesClient placesClient = Places.createClient(context);
            ArrayList<ArrayList<Place>> allPlaces = new ArrayList<ArrayList<Place>>();
            ArrayList<ArrayList<String>> allSpotIds = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<Spot>> allSpots = new ArrayList<ArrayList<Spot>>();
            ArrayList<String> cityIds = new ArrayList<>();
            ArrayList<String> cityNames = new ArrayList<>();

            Intent intent = new Intent(context, DetailsActivity.class);
            TripParcelableObject tripParcelableObject = new TripParcelableObject();
            tripParcelableObject.setTrip(trip);

            allSpotIds = Trip.parseForSpots(trip.getPlaces());
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
                            new Thread(new Runnable() {
                                public void run() {
                                    for (ArrayList<Spot> city : allSpots) {
                                        ArrayList<Place> placesInCity = new ArrayList<>();
                                        for (Spot spot : city) {
                                            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(spot.getPlaceID(), placeFields);
                                            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                                                Place place = response.getPlace();
                                                placesInCity.add(place);
                                                startDetailsActivity(intent, tripParcelableObject, allPlaces,
                                                        allSpots, trip);
                                            }).addOnFailureListener((exception) -> {
                                                if (exception instanceof ApiException) {
                                                    final ApiException apiException = (ApiException) exception;
                                                    Log.e(TAG, "Place not found: " + exception.getMessage());
                                                } else {
                                                    Log.e(TAG, "Other exception: " + exception.getMessage());
                                                }
                                            });
                                        }
                                        allPlaces.add(placesInCity);
                                    }
                                }
                            }).start();
                        }
                    }
                });
            }
        }

        private void startDetailsActivity(Intent intent, TripParcelableObject tripParcelableObject, ArrayList<ArrayList<Place>> allPlaces, ArrayList<ArrayList<Spot>> allSpots, Trip trip) {
            tripParcelableObject.setPlacesInParcel(allPlaces);
            tripParcelableObject.setSpotsInParcel(allSpots);

            intent.putExtra("tripObj", Parcels.wrap(tripParcelableObject));
            intent.putExtra("tripId", trip.getObjectId());
            intent.putExtra("trip", Parcels.wrap(trip));

            ((MainActivity)context).startActivity(intent);
        }
    }
}
