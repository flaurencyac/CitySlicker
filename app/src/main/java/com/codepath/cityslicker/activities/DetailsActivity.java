package com.codepath.cityslicker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.cityslicker.R;
import com.codepath.cityslicker.TripParcelableObject;
import com.codepath.cityslicker.adapters.FragmentAdapter;
import com.codepath.cityslicker.databinding.ActivityDetailsBinding;
import com.codepath.cityslicker.fragments.EditTripFragment;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";

    private Spinner citiesSpinner;
    private Button btnDone;
    private Context context;

    private ArrayList<ArrayList<Place>> allPlaces = new ArrayList<ArrayList<Place>>();
    private ArrayList<ArrayList<String>> allPlaceIds = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<Spot>> allSpots = new ArrayList<ArrayList<Spot>>();
    private ArrayList<String> cityIds = new ArrayList<>();
    private ArrayList<String> cityNames = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private FragmentAdapter fragmentAdapter;
    private FragmentManager fragmentManager;
    private FrameLayout frameLayout;
    private Trip trip;
    private String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        context = DetailsActivity.this;

        TripParcelableObject parcel = Parcels.unwrap(getIntent().getParcelableExtra("tripObj"));
        trip = parcel.getTrip();
        allPlaces = parcel.getPlacesInParcel();
        allSpots = parcel.getSpotsInParcel();
        tripId = getIntent().getStringExtra("tripId");
        cityNames = getIntent().getStringArrayListExtra("cityNames");
        cityIds = getIntent().getStringArrayListExtra("cityIdList");
        allPlaceIds = (ArrayList<ArrayList<String>>) getIntent().getExtras().getSerializable("allPlaceIds");

        frameLayout = findViewById(R.id.FrameLayout);
        citiesSpinner = findViewById(R.id.citiesSpinner);
        btnDone = findViewById(R.id.btnDone);

        arrayAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, cityNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle(), context, cityIds.size(), allPlaces, allSpots, tripId, trip);

        citiesSpinner.setAdapter(arrayAdapter);
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, "Selected "+cityNames.get(position), Toast.LENGTH_SHORT).show();
                switch(position) {
                    case 0:
                        selectFragment(position);
                    default:
                        selectFragment(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectFragment(0);

            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void selectFragment(Integer position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.FrameLayout, new EditTripFragment(context, allPlaces.get(position), allSpots.get(position), tripId, trip));
        ft.commit();
    }

}