package com.codepath.cityslicker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.cityslicker.R;
import com.codepath.cityslicker.TripParcelableObject;
import com.codepath.cityslicker.databinding.ActivityDetailsBinding;
import com.codepath.cityslicker.models.Trip;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.textfield.TextInputLayout;

import org.parceler.Parcels;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";

    private Spinner citiesSpinner;
    private Context context;

    private ArrayList<String> cityIds = new ArrayList<>();
    private ArrayList<String> cityNames = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private Trip trip;
    private String tripId;
    private ArrayList<ArrayList<String>> allPlaceIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        this.context = getApplicationContext();

        TripParcelableObject parcel = Parcels.unwrap(getIntent().getParcelableExtra("tripObj"));
        trip = parcel.getTrip();
        tripId = getIntent().getStringExtra("tripId");
        cityNames = getIntent().getStringArrayListExtra("cityNames");
        cityIds = getIntent().getStringArrayListExtra("cityIdList");
        allPlaceIds = (ArrayList<ArrayList<String>>) getIntent().getExtras().getSerializable("allPlaceIds");


        citiesSpinner = findViewById(R.id.citiesSpinner);
        arrayAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, cityNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citiesSpinner.setAdapter(arrayAdapter);
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, "Selected "+cityNames.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}