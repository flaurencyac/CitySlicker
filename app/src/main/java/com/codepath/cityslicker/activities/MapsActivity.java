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
import android.widget.Toast;

import com.codepath.cityslicker.BuildConfig;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.databinding.ActivityMapsBinding;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float ZOOM = 15f;
    private final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING,
            Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.USER_RATINGS_TOTAL, Place.Field.PRICE_LEVEL, Place.Field.TYPES, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS);
    private static LatLngBounds restrictedBoundsArea;

    private PlacesClient placesClient;
    private String tripId;
    private ArrayList<String> cityIdList = new ArrayList<>();
    private ArrayList<Place> cityList = new ArrayList<>();
    private Context context;
    private GoogleMap googleMap;
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private SupportMapFragment supportMapFragment;
    // TODO: set the target city to the first city in the list of cities that belongs to the Trip object ID
    private Place targetCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.context = getApplicationContext();
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        Places.initialize(context, BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(context);
        // TODO: restrict place search
        autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        //autocompleteSupportFragment.setLocationRestriction();
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull @NotNull Place selectedPlace) {
                 final FetchPlaceRequest request = FetchPlaceRequest.newInstance(selectedPlace.getId(), placeFields);
                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    moveCamera(place.getLatLng(),ZOOM, place.getName());
                    showPlaceDetailsFragment(place);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: "+exception.getMessage());
                        final int statusCode = apiException.getStatusCode();
                    }});
            }
            @Override
            public void onError(@NonNull @NotNull Status status) {
                Log.i(TAG, "Error searching occurred: " + status);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        tripId = getIntent().getStringExtra("tripId");
        getTargetCities();
        googleMap = gMap;
        restrictedBoundsArea = googleMap.getProjection().getVisibleRegion().latLngBounds;
        googleMap.setLatLngBoundsForCameraTarget(restrictedBoundsArea);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json));
        googleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                showPOIDetailsFragment(pointOfInterest);
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        googleMap.addMarker(options);
    }


    public void showPOIDetailsFragment(PointOfInterest poi) {
        Toast.makeText(this, "Clicked: " + poi.name + "Place ID:" + poi.placeId + "Latitude:" + poi.latLng.latitude + " Longitude:" + poi.latLng.longitude, Toast.LENGTH_SHORT).show();
        FragmentManager fm = getSupportFragmentManager();
        POIDialogFragment poiDialogFragment = POIDialogFragment.newInstance(poi);
        poiDialogFragment.show(fm, "dialog_fragment_add_to_trip_poi");
    }

    public void showPlaceDetailsFragment(Place place) {
        FragmentManager fm = getSupportFragmentManager();
        POIDialogFragment poiDialogFragment = POIDialogFragment.newInstance(place);
        poiDialogFragment.show(fm, "dialog_fragment_add_to_trip_poi");
    }

    private void getTargetCity() {
        if(cityList.size() > 0) {
            targetCity = cityList.get(0);
        }
    }

    // convert cityIds stored in Parse to place objects with Places API
    private void getTargetCities() {
        ParseQuery<Trip> query = ParseQuery.getQuery("Trip");
        query.include((Trip.KEY_REGIONS));
        query.getInBackground(tripId, (object, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting regions", e);
                return;
            } else {
                cityIdList = (ArrayList<String>) object.get(Trip.KEY_REGIONS);
                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(cityIdList.get(0), placeFields);
                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    moveCamera(place.getLatLng(), ZOOM, place.getName());
                    cityList.add(place);
                    targetCity = place;
                });
//                for (String id : cityIdList) {
//                    final FetchPlaceRequest request = FetchPlaceRequest.newInstance(id, placeFields);
//                    placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
//                        Place place = response.getPlace();
//                        Log.d(TAG, "Place: "+ place.getLatLng());
//                        // TODO : find out why the place API call works but cityList.add(place does not)
//                        moveCamera(place.getLatLng(), ZOOM, place.getName());
//                        cityList.add(place);
//                    }).addOnFailureListener((exception) -> {
//                        if (exception instanceof ApiException) {
//                            final ApiException apiException = (ApiException) exception;
//                            Log.e(TAG, "Place not found: "+exception.getMessage());
//                            final int statusCode = apiException.getStatusCode();
//                            Log.e(TAG, "Status Code: "+statusCode);
//                        }});
//                }
            }
        });
    }
}