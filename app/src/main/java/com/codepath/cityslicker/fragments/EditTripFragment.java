package com.codepath.cityslicker.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.codepath.cityslicker.R;
import com.codepath.cityslicker.adapters.PlaceAdapter;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.google.android.libraries.places.api.model.Place;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

public class EditTripFragment extends Fragment {
    private static final String TAG = "EditTripFragment";

    private Context context;
    private RecyclerView rvPlaces;
    private PlaceAdapter adapter;
    private String tripId;
    private ArrayList<Place> placesInCity = new ArrayList<Place>();
    private ArrayList<Spot> spots = new ArrayList<Spot>();
    private Trip trip;


    public EditTripFragment(Context context, ArrayList<Place> placesInCity, ArrayList<Spot> spots, String tripId, Trip trip) {
        this.context = context;
        this.placesInCity = placesInCity;
        this.spots = spots;
        this.tripId = tripId;
        this.trip = trip;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_trip, container, false); }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPlaces = view.findViewById(R.id.rvPlaces);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        rvPlaces.setLayoutManager(llm);
        adapter = new PlaceAdapter(context, placesInCity, spots, tripId, trip);
        rvPlaces.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }






}