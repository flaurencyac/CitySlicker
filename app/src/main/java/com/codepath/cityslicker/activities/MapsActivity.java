package com.codepath.cityslicker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.cityslicker.BuildConfig;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.TripParcelableObject;
import com.codepath.cityslicker.fragments.AddToTripDialogFragment;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
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
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AddToTripDialogFragment.AddToTripPOIDialogFragmentListener {
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

    private Trip trip;
    private static LatLngBounds latLngBoundary;
    private String tripId;
    private ArrayList<String> cityIdList = new ArrayList<>();
    private ArrayList<String> cityNames = new ArrayList<>();
    private ArrayList<Place> cityList = new ArrayList<>();
    private Integer currentCityIndex = 0;

    private ArrayList<Place> placesInCurrentCity = new ArrayList<>();
    private ArrayList<ArrayList<Place>> allPlaces = new ArrayList<ArrayList<Place>>();
    private ArrayList<String> placesIdsCurrentCity = new ArrayList<>();
    private ArrayList<ArrayList<String>> allPlaceIds = new ArrayList<ArrayList<String>>();
    private ArrayList<Spot> spotsInCurrentCity = new ArrayList<Spot>();
    private ArrayList<ArrayList<Spot>> allSpots = new ArrayList<ArrayList<Spot>>();
    private ArrayList<String> spotIds = new ArrayList<>();
    private ArrayList<String> allSpotIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.context = getApplicationContext();
        btnNext = findViewById(R.id.btnNext);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
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
                    updateTripWithAllPlaces();
                } else {
                    moveToNextCity();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        tripId = getIntent().getStringExtra("tripId");
        TripParcelableObject parcel = Parcels.unwrap(getIntent().getParcelableExtra("tripObj"));
        trip = parcel.getTrip();
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
        AddToTripDialogFragment addToTripPOIDialogFragment = AddToTripDialogFragment.newInstance(poi);
        addToTripPOIDialogFragment.show(fm, "dialog_fragment_add_to_trip_poi");
    }

    public void showPlaceDetailsFragment(Place place) {
        FragmentManager fm = getSupportFragmentManager();
        AddToTripDialogFragment addToTripPOIDialogFragment = AddToTripDialogFragment.newInstance(place);
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
            cityNames.add(place.getName());
            if (currentCityIndex == cityIdList.size()-1) {
                btnNext.setText("Done!");
            }
        });
    }

    private void moveToNextCity() {
        currentCityIndex += 1;
        panToCity(cityIdList.get(currentCityIndex));
        ArrayList<Place> placeListClone = new ArrayList<>(placesInCurrentCity);
        allPlaces.add(placeListClone);
        ArrayList<String> placeIdsClone = new ArrayList<>(placesIdsCurrentCity);
        allPlaceIds.add(placeIdsClone);
        ArrayList<Spot> spotsClone = new ArrayList<>(spotsInCurrentCity);
        allSpots.add(spotsClone);
        ArrayList<String> spotIdsClone = new ArrayList<>(spotIds);
        allSpotIds.add(spotIdsClone.toString());
        spotIds.clear();
        spotsInCurrentCity.clear();
        placesInCurrentCity.clear();
        placesIdsCurrentCity.clear();
    }

    private void updateTripWithAllPlaces() {
        allPlaces.add(placesInCurrentCity);
        allPlaceIds.add(placesIdsCurrentCity);
        allSpots.add(spotsInCurrentCity);
        allSpotIds.add(spotIds.toString());
        ParseQuery<Trip> query  = ParseQuery.getQuery("Trip");
        query.getInBackground(tripId, (object, e) -> {
            object.setPlaces(new JSONArray(allSpotIds));
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(context, "Saved Trip!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("tripId", tripId);
                    TripParcelableObject parcel = new TripParcelableObject();
                    parcel.setTrip(trip);
                    parcel.setSpotsInParcel(allSpots);
                    parcel.setPlacesInParcel(allPlaces);
                    intent.putExtra("tripObj", Parcels.wrap(parcel));
                    intent.putStringArrayListExtra("cityNames", cityNames);
                    intent.putStringArrayListExtra("cityIdList", cityIdList);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("allPlaceIds", allPlaceIds);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        });
    }

    public void onFinishAddToTripPOIDialogFragment(Boolean addToTrip, String placeId, String placeName, Place place) {
        Toast.makeText(context, "Added "+placeName+" to trip!", Toast.LENGTH_SHORT).show();
        if (addToTrip == true) {
            Spot spot = new Spot();
            spot.setName(placeName);
            spot.setPlaceID(placeId);
            spot.setCityId(cityIdList.get(currentCityIndex));
            spot.setTrip(trip);
            spot.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e!= null) {
                        Log.e(TAG, "Error creating spot in Parse", e);
                    } else {
                        spotIds.add(spot.getObjectId());
                        Log.i(TAG, "Saved spot to parse!");
                    }
                }
            });
            placesInCurrentCity.add(place);
            placesIdsCurrentCity.add(place.getId());
            spotsInCurrentCity.add(spot);

        }
    }
}