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
import com.codepath.cityslicker.activities.DetailsActivity;
import com.codepath.cityslicker.models.Trip;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {
    Context context;
    List<Trip> trips;
    private TripClickedListener tripClickedListener;

    public TripsAdapter(Context context, List<Trip> trips, TripClickedListener listener) {
        this.context = context;
        this.trips = trips;
        this.tripClickedListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public TripsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View tripView = LayoutInflater.from(context).inflate(R.layout.item_trip, parent, false);
        return new ViewHolder(tripView, tripClickedListener);
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
        private TripClickedListener tripClickedListener;

        public ViewHolder (View itemView, TripClickedListener listener) {
            super(itemView);
            tvTripTitle = itemView.findViewById(R.id.tvFriendName);
            this.tripClickedListener = listener;
        }

        public void bind(Trip trip) {
            tvTripTitle.setText(trip.getTripName());

//            tvTripTitle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //int position = getAdapterPosition();
//                    tripClickedListener.openTripDetails(getAdapterPosition());
//                }
//            });


            Intent intent = new Integer(context, DetailsActivity.class);
            intent.putExtra("trip", Parcels.wrap(trip));
            startActivity(intent);



        }

//        @Override
//        public void onClick(View v) {
//            int position = getAdapterPosition();
//            tripClickedListener.openTripDetails(trips.get(position));
//        }
    }

//    public interface TripClickedListener {
//        void openTripDetails(int position);
//    }
}
