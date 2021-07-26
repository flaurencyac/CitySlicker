package com.codepath.cityslicker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.cityslicker.BuildConfig;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.TripParcelableObject;
import com.codepath.cityslicker.adapters.RecommendedAdapter;
import com.codepath.cityslicker.fragments.AddToTripDialogFragment;
import com.codepath.cityslicker.models.RecommendedPlace;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AddToTripDialogFragment.AddToTripPOIDialogFragmentListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MapsActivity";
    private static final String TYPE_FOOD = "restaurant";
    private static final String KEYWORD_FAMILY = "kids";
    private static final String TYPE_FAMILY = "tourist_attractions";
    private static final String TYPE_ADULT = "night_club";
    private static final String TYPE_ATTRACTIONS = "tourist_attraction";
    private static final String TYPE_SHOPPING = "shopping_mall";
    private static final Integer PROXIMITY_RADIUS = 1500;

    private static final float BOUNDARY_ZOOM = 11f;
    private static final float CENTER_ZOOM = 15f;
    private final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING,
            Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.USER_RATINGS_TOTAL, Place.Field.PRICE_LEVEL, Place.Field.TYPES, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS);

    private Button btnNext;
    private Context context;
    private GoogleMap googleMap;
    private PlacesClient placesClient;
    private GoogleApiClient googleApiClient;
    private SupportMapFragment supportMapFragment;
    private RecyclerView rvRecommended;
    private RecommendedAdapter adapter;
    private AutocompleteSupportFragment autocompleteSupportFragment;

    private Integer adultPref;
    private Integer familyPref;
    private Integer foodPref;
    private Integer attractionsPref;
    private Integer shoppingPref;
    private Integer budget;
    private Trip trip;
    private String tripId;
    private static LatLngBounds latLngBoundary;
    private Integer currentCityIndex = 0;
    private ArrayList<String> cityIdList = new ArrayList<>();
    private ArrayList<String> cityNames = new ArrayList<>();
    private ArrayList<Place> cityList = new ArrayList<>();

    private ArrayList<Place> placesInCurrentCity = new ArrayList<>();
    private ArrayList<ArrayList<Place>> allPlaces = new ArrayList<ArrayList<Place>>();
    private ArrayList<String> placesIdsCurrentCity = new ArrayList<>();
    private ArrayList<ArrayList<String>> allPlaceIds = new ArrayList<ArrayList<String>>();
    private ArrayList<Spot> spotsInCurrentCity = new ArrayList<Spot>();
    private ArrayList<ArrayList<Spot>> allSpots = new ArrayList<ArrayList<Spot>>();
    private ArrayList<String> spotIds = new ArrayList<>();
    private ArrayList<String> allSpotIds = new ArrayList<>();
    private ArrayList<RecommendedPlace> allRecommendedPlaces = new ArrayList<RecommendedPlace>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.context = getApplicationContext();
        btnNext = findViewById(R.id.btnNext);
        rvRecommended = findViewById(R.id.rvRecommended);

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

        LinearLayoutManager llm = new LinearLayoutManager(context);
        rvRecommended.setLayoutManager(llm);
        buildGoogleApiClient();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        tripId = getIntent().getStringExtra("tripId");
        TripParcelableObject parcel = Parcels.unwrap(getIntent().getParcelableExtra("tripObj"));
        trip = parcel.getTrip();
        budget = trip.getBudget();
        adultPref = trip.getAdultPreference();
        attractionsPref = trip.getAttractionsPreference();
        familyPref = trip.getFamilyPreference();
        foodPref = trip.getFoodPreference();
        shoppingPref = trip.getShoppingPreference();
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

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
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
        FragmentManager fm = getSupportFragmentManager();
        AddToTripDialogFragment addToTripPOIDialogFragment = AddToTripDialogFragment.newInstance(poi);
        addToTripPOIDialogFragment.show(fm, "dialog_fragment_add_to_trip_poi");
    }

    public void showPlaceDetailsFragment(Place place) {
        FragmentManager fm = getSupportFragmentManager();
        AddToTripDialogFragment addToTripPOIDialogFragment = AddToTripDialogFragment.newInstance(place);
        addToTripPOIDialogFragment.show(fm, "dialog_fragment_add_to_trip_poi");
    }


    private void getRecommendedPlaces(){
        if (adultPref != 0) {
            allRecommendedPlaces.addAll(getTypeOfRecommendedPlaces(TYPE_ADULT, adultPref));
        }
        if (attractionsPref != 0) {
            allRecommendedPlaces.addAll(getTypeOfRecommendedPlaces(TYPE_ATTRACTIONS, attractionsPref));
        }
        if (familyPref != 0) {
            allRecommendedPlaces.addAll(getTypeOfRecommendedPlaces(TYPE_FAMILY, familyPref));
        }
        if (foodPref != 0) {
            allRecommendedPlaces.addAll(getTypeOfRecommendedPlaces(TYPE_FOOD, foodPref));
        }
        if (shoppingPref != 0) {
            allRecommendedPlaces.addAll(getTypeOfRecommendedPlaces(TYPE_SHOPPING, shoppingPref));
        }
        if (shoppingPref == 0 && adultPref == 0 && attractionsPref == 0 && foodPref == 0 && shoppingPref == 0) {
            allRecommendedPlaces.addAll(getTypeOfRecommendedPlaces(TYPE_ATTRACTIONS, attractionsPref));
        }
    }

    private ArrayList<RecommendedPlace> getTypeOfRecommendedPlaces(@Nullable String type, @NonNull Integer prefWeight) {
        ArrayList<RecommendedPlace> recommendedPlaces = null;
        HttpURLConnection connection = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlaceUrl.append("location="+cityList.get(currentCityIndex).getLatLng().latitude+","+cityList.get(currentCityIndex).getLatLng().longitude);
            googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
            googlePlaceUrl.append("&rankby=prominence");
            googlePlaceUrl.append("&maxprice="+budget);
            if (type == null) {
                googlePlaceUrl.append("&type="+TYPE_ATTRACTIONS);
            } else if(type.equals(TYPE_FAMILY)) {
                googlePlaceUrl.append("&type="+type);
                googlePlaceUrl.append("&keyword="+KEYWORD_FAMILY);
            }
            else {
                googlePlaceUrl.append("&type="+type);
            }
            googlePlaceUrl.append("&sensor=true");
            googlePlaceUrl.append("&key="+BuildConfig.MAPS_API_KEY);
            URL url = new URL(googlePlaceUrl.toString());
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            int read;
            char[] buffer = new char[1050];
            while((read=in.read(buffer))!=-1) {
                jsonResults.append(buffer, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        try {
            JSONObject jsonObject = new JSONObject((jsonResults.toString()));
            JSONArray resultsJsonArray = jsonObject.getJSONArray("results");
            recommendedPlaces = new ArrayList<RecommendedPlace>();
            for (int i = 0; i < resultsJsonArray.length(); i++) {
                RecommendedPlace recommendedPlace = new RecommendedPlace(resultsJsonArray.getJSONObject(i));
                recommendedPlace.setWeight(prefWeight);
                recommendedPlaces.add(recommendedPlace);
            }
        } catch (JSONException e){
            Log.e(TAG, "Error processing JSON results", e);
        }
        return recommendedPlaces;
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
            new Thread(new Runnable() {
                public void run() {
                    getRecommendedPlaces();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            allRecommendedPlaces = RecommendedPlace.sortRecommendedPlaces(allRecommendedPlaces);
                            adapter = new RecommendedAdapter(context, allRecommendedPlaces);
                            rvRecommended.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();
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
        allRecommendedPlaces.clear();
    }

    private void updateTripWithAllPlaces() {
        allPlaces.add(placesInCurrentCity);
        allPlaceIds.add(placesIdsCurrentCity);
        allSpots.add(spotsInCurrentCity);
        allSpotIds.add(spotIds.toString());
        ParseQuery<Trip> query  = ParseQuery.getQuery("Trip");
        query.include(Trip.KEY_PLACES);
        query.getInBackground(tripId, (object, e) -> {
            object.setPlaces(new JSONArray(allSpotIds));
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(context, "Saved Trip!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("tripId", tripId);
                    TripParcelableObject parcel = new TripParcelableObject();
                    parcel.setTrip(object);
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

    @Override
    public void onConnected(@Nullable @org.jetbrains.annotations.Nullable Bundle bundle) { Log.i(TAG, "API Client connected!"); }

    @Override
    public void onConnectionSuspended(int i) { Log.i(TAG, "API Client connection suspended!"); }

    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) { Log.e(TAG, "API Client connection failed!"); }

    @Override
    public void onLocationChanged(Location location) { Log.i(TAG, "API Client location changed to: "+location.toString()); }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { Log.i(TAG, "API Client pointer capture changed to: "+hasCapture); }

}