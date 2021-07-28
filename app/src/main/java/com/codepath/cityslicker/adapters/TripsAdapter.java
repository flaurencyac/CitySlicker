package com.codepath.cityslicker.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.cityslicker.R;
import com.codepath.cityslicker.models.Trip;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {
    Context context;
    List<Trip> trips;
    private TripClickedListener tripClickedListener;

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTripTitle;

        public ViewHolder (View itemView) {
            super(itemView);
            tvTripTitle = itemView.findViewById(R.id.tvFriendName);
        }

        public void bind(Trip trip) {
            tvTripTitle.setText(trip.getTripName());
        }

        public void onClick(View v) {
            int position = getAdapterPosition();
            tripClickedListener.openTripDetails(trips.get(position));
        }
    }

    public interface TripClickedListener {
        void openTripDetails(Trip trip);
    }
}
