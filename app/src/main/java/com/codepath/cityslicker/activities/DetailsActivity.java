package com.codepath.cityslicker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.cityslicker.MainActivity;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.TripParcelableObject;
import com.codepath.cityslicker.adapters.FragmentAdapter;
import com.codepath.cityslicker.databinding.ActivityDetailsBinding;
import com.codepath.cityslicker.fragments.EditTripFragment;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.codepath.cityslicker.ui.explore.ExploreFragment;
import com.codepath.cityslicker.ui.profile.ProfileFragment;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.Parse;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";

    private Spinner citiesSpinner;
    private Button btnDone;
    private Context context;
    private KonfettiView konfettiView;
    private Drawable plane;
    private Shape.DrawableShape planeShape;
    private Button btnAddPlaces;
    private TextView tvTripTitle;
    private TextView tvCollaborators;

    private ArrayList<ArrayList<Place>> allPlaces = new ArrayList<ArrayList<Place>>();
    private ArrayList<ArrayList<String>> allPlaceIds = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<Spot>> allSpots = new ArrayList<ArrayList<Spot>>();
    private ArrayList<String> cityIds = new ArrayList<>();
    private ArrayList<String> cityNames = new ArrayList<>();
    private ArrayList<String> collaborators = new ArrayList<>();
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
        konfettiView = findViewById(R.id.viewKonfetti);
        citiesSpinner = findViewById(R.id.citiesSpinner);
        btnDone = findViewById(R.id.btnDone);
        btnAddPlaces = findViewById(R.id.btnAddPlaces);
        tvTripTitle = findViewById(R.id.tvTripTitle);
        tvCollaborators = findViewById(R.id.tvCollaborators);

        arrayAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, cityNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle(), context, cityIds.size(), allPlaces, allSpots, tripId, trip);

        citiesSpinner.setAdapter(arrayAdapter);
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectFragment(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectFragment(0);

            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        plane = ContextCompat.getDrawable(context, R.drawable.ic_baseline_flight_24);
        planeShape  = new Shape.DrawableShape(plane, true);
        konfettiView.build()
                .addColors(Color.TRANSPARENT, Color.RED, Color.BLUE, Color.WHITE, Color.argb( 100, 0,191,255))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, planeShape)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 3000L);

        btnAddPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        tvTripTitle.setText(trip.getTripName());
        collaborators = trip.getCollaborators();
        String collaboratorsString = collaborators.toString();
        tvCollaborators.setText("Collaborators: "+collaboratorsString.substring(1,collaboratorsString.length()-1));
    }

    private void selectFragment(Integer position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.FrameLayout, new EditTripFragment(context, allPlaces.get(position), allSpots.get(position), tripId, trip));
        ft.commit();
    }

}