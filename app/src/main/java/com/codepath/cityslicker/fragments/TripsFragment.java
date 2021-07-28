package com.codepath.cityslicker.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codepath.cityslicker.R;
import com.codepath.cityslicker.activities.DetailsActivity;
import com.codepath.cityslicker.adapters.TripsAdapter;
import com.codepath.cityslicker.models.Trip;
import com.codepath.cityslicker.ui.profile.ProfileFragment;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class TripsFragment extends Fragment implements TripsAdapter.TripClickedListener {
    private static final String TAG = "TripsFragment";
    private Context context;

    private RecyclerView rvTrips;
    private List<Trip> trips;
    private TripsAdapter adapter;


    public TripsFragment(Context context, List<Trip> trips) {
        this.context = context;
        this.trips = trips;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvTrips = view.findViewById(R.id.rvTrips);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        rvTrips.setLayoutManager(llm);



        adapter = new TripsAdapter(context, trips, (ProfileFragment) TripsFragment.this.getParentFragment());




        rvTrips.setAdapter(adapter);
    }

    @Override
    public void openTripDetails(Trip trip) {
        Intent intent = new Integer(context, DetailsActivity.class);
        intent.putExtra("trip", Parcels.wrap(trip));
        startActivity(intent);
    }
}