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
import com.codepath.cityslicker.Utilities;
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
        TextView tvNumCollaborators;

        public ViewHolder (View itemView) {
            super(itemView);
            tvTripTitle = itemView.findViewById(R.id.tvFriendName);
            tvNumCollaborators = itemView.findViewById(R.id.tvNumCollaborators);
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
                    if (object.getCollaborators()!= null) {
                        tvNumCollaborators.setText(""+object.getCollaborators().size());
                    } else {
                        tvNumCollaborators.setText("0");
                    }
                    trips.set(getAdapterPosition(), object);
                }
            });
            tvTripTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.openTripDetails(trips.get(getAdapterPosition()), context, TAG);
                }
            });
        }
    }
}
