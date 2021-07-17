package com.codepath.cityslicker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.cityslicker.BuildConfig;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.databinding.ActivityMapsBinding;
import com.codepath.cityslicker.fragments.AddToTripPOIDialogFragment;
import com.codepath.cityslicker.fragments.POIDialogFragment;
import com.codepath.cityslicker.models.Trip;
import com.codepath.cityslicker.ui.explore.ExploreFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private static final float BOUNDARY_ZOOM = 11f;
    private static final float CENTER_ZOOM = 15f;
    private final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING,
            Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.USER_RATINGS_TOTAL, Place.Field.PRICE_LEVEL, Place.Field.TYPES, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS);

    private Button btnNext;
    private Context context;
    private GoogleMap googleMap;
    private PlacesClient placesClient;
    private SupportMapFragment supportMapFragment;
    private AutocompleteSupportFragment autocompleteSupportFragment;

    private static LatLngBounds latLngBoundary;
    private String tripId;
    private ArrayList<String> cityIdList = new ArrayList<>();
    private ArrayList<Place> cityList = new ArrayList<>();
    private Integer currentCityIndex = 0;
    private ArrayList<ArrayList<Place>> allPlaces = new ArrayList<>();
    private ArrayList<Place> placesInCurrentCity = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.context = getApplicationContext();
        btnNext = findViewById(R.id.btnNext);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        // TODO: replace w custom txt view so someone can search for nearby stuff
        Places.initialize(context, BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(context);
        autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteSupportFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull @NotNull Place selectedPlace) {
                 final FetchPlaceRequest request = FetchPlaceRequest.newInstance(selectedPlace.getId(), placeFields);
                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    moveCamera(place.getLatLng(),CENTER_ZOOM, place.getName());
                    showPlaceDetailsFragment(place);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: "+exception.getMessage());
                    }});
            }
            @Override
            public void onError(@NonNull @NotNull Status status) {
                Log.i(TAG, "Error searching occurred: " + status);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCityIndex == cityIdList.size()-1) {
                    // TODO navigate to edit trip screen
                    // TODO WHEN UPDATING A TRIP WITH ALL ITS PLACES: update trip object w/ JSONArray of [city1["_place_ParseObject_ids_"], city2[], city3[]]
                } else {
                    currentCityIndex += 1;
                    panToCity(cityIdList.get(currentCityIndex));
                    // TODO save list of places for that old city into the list of places array of arrays
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        tripId = getIntent().getStringExtra("tripId");
        getTargetCities();
        googleMap = gMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json));
        googleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                showPOIDetailsFragment(pointOfInterest);
            }
        });
    }

    private void setLatLngBoundary(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, BOUNDARY_ZOOM));
        latLngBoundary = googleMap.getProjection().getVisibleRegion().latLngBounds;
        // TODO DEBUG: googleMap.setLatLngBoundsForCameraTarget(latLngBoundary);
        autocompleteSupportFragment.setLocationRestriction(RectangularBounds.newInstance(latLngBoundary));
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        googleMap.addMarker(options);
    }


    public void showPOIDetailsFragment(PointOfInterest poi) {
        Toast.makeText(this, "Clicked: " + poi.name + "Place ID:" + poi.placeId + "Latitude:" + poi.latLng.latitude + " Longitude:" + poi.latLng.longitude, Toast.LENGTH_SHORT).show();
        FragmentManager fm = getSupportFragmentManager();
        AddToTripPOIDialogFragment addToTripPOIDialogFragment = AddToTripPOIDialogFragment.newInstance(poi);
        // TODO : if place is added to trip create a Parse obj for that place (including place API ID)
        // TODO: place Parse object ID update User's list of places for that city
        addToTripPOIDialogFragment.show(fm, "dialog_fragment_add_to_trip_poi");
    }

    public void showPlaceDetailsFragment(Place place) {
        FragmentManager fm = getSupportFragmentManager();
        AddToTripPOIDialogFragment addToTripPOIDialogFragment = AddToTripPOIDialogFragment.newInstance(place);
        // TODO : if poi is added to trip add to list of places for that city
        addToTripPOIDialogFragment.show(fm, "dialog_fragment_add_to_trip_poi");
    }

    // get list of cityIds stored in Parse and pan to the first city
    private void getTargetCities() {
        ParseQuery<Trip> query = ParseQuery.getQuery("Trip");
        query.include((Trip.KEY_REGIONS));
        query.getInBackground(tripId, (object, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting regions", e);
                return;
            } else {
                cityIdList = (ArrayList<String>) object.get(Trip.KEY_REGIONS);
                panToCity(cityIdList.get(currentCityIndex));
            }
        });
    }

    private void panToCity(String cityId) {
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(cityId, placeFields);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            setLatLngBoundary(place.getLatLng());
            moveCamera(place.getLatLng(), CENTER_ZOOM, place.getName());
            cityList.add(place);
            if (currentCityIndex == cityIdList.size()-1) {
                btnNext.setText("Done!");
            }
        });
    }
}