package com.codepath.cityslicker.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.codepath.cityslicker.fragments.EditTripFragment;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.google.android.libraries.places.api.model.Place;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentStateAdapter {
    private static final String TAG = "FragmentAdapter";

    private Integer numCities;
    private Context context;
    private String tripId;
    private Trip trip;
    private ArrayList<ArrayList<Place>> places = new ArrayList<ArrayList<Place>>();
    private ArrayList<ArrayList<Spot>> spots = new ArrayList<ArrayList<Spot>>();


    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context, Integer numCities,
                           ArrayList<ArrayList<Place>> places, ArrayList<ArrayList<Spot>> spots, String tripId, Trip trip) {
        super(fragmentManager, lifecycle);
        this.context = context;
        this.numCities = numCities;
        this.places = places;
        this.spots = spots;
        this.tripId = tripId;
        this.trip = trip;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return new EditTripFragment(context, places.get(position), spots.get(position), tripId, trip);
    }

    @Override
    public int getItemCount() {
        return numCities;
    }
}
