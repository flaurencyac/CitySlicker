package com.codepath.cityslicker.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.TripParcelableObject;
import com.codepath.cityslicker.activities.DetailsActivity;
import com.codepath.cityslicker.adapters.ProfileFragmentAdapter;
import com.codepath.cityslicker.adapters.TripsAdapter;
import com.codepath.cityslicker.databinding.FragmentProfileBinding;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment implements TripsAdapter.TripClickedListener {
    public static final String TAG = "ProfileFragment";
    private final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING,
            Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.USER_RATINGS_TOTAL, Place.Field.PRICE_LEVEL, Place.Field.TYPES, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS);
    public final static int PICK_PHOTO_CODE = 1000;

    private Context context;
    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private TextView tvUpdatePP;
    private TextView tvUsername;
    private ImageView ivProfilePicture;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProfileFragmentAdapter fragmentAdapter;

    private List<Trip> trips = new ArrayList<>();
    private List<ParseUser> friends = new ArrayList<>();

    private Bitmap bitmap;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getContext();
        tvUpdatePP = binding.tvUpdatePP;
        tvUsername = binding.tvUsername;
        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;
        ivProfilePicture = binding.ivProfilePicture;

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        ParseFile profilePicture = (ParseFile) ParseUser.getCurrentUser().get("profilePicture");
        if (profilePicture != null) {
            Glide.with(getContext()).load(profilePicture.getUrl()).circleCrop().into(ivProfilePicture);
        } else {
            Glide.with(getContext()).load(R.drawable.ic_baseline_account_circle_24).circleCrop().into(ivProfilePicture);
        }
        tvUpdatePP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

        FragmentManager fm = getActivity().getSupportFragmentManager();
        getAllTrips(fm);

        tabLayout.addTab(tabLayout.newTab().setText("Trips"));
        tabLayout.addTab(tabLayout.newTab().setText("Friends"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    private void getAllTrips(FragmentManager fm) {
        ParseQuery<Trip> query = ParseQuery.getQuery("Trip");
        query.include(Trip.KEY_OWNER);
        query.whereEqualTo(Trip.KEY_OWNER, ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> objects, ParseException e) {
                if (e == null) {
                    trips.addAll(objects);
                    getAllFriends(fm);
                } else {
                    Log.e(TAG, "Issue getting trips");
                }
            }
        });
    }

    private void getAllFriends(FragmentManager fm) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.addDescendingOrder("createdAt");
        query.findInBackground((users, e) -> {
            if (e == null) {
                friends.addAll(users);
                fragmentAdapter = new ProfileFragmentAdapter(fm, getLifecycle(), context, trips, friends);
                viewPager.setAdapter(fragmentAdapter);
            }
        });
    }

    public void onPickPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            bitmap = loadFromUri(photoUri);
            Glide.with(getContext()).asBitmap().load(bitmap).circleCrop().into(ivProfilePicture);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
            byte[] byteArray = stream.toByteArray();
            ParseFile newProfilePicture = new ParseFile("profile.png", byteArray);
            ParseUser user = ParseUser.getCurrentUser();
            user.put("profilePicture", newProfilePicture);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.i(TAG, "Successfully uploaded image");
                        Toast.makeText(getContext(), "Profile pic uploaded!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void openTripDetails(Trip trip) {
        PlacesClient placesClient = Places.createClient(context);
        ArrayList<ArrayList<Place>> allPlaces = new ArrayList<ArrayList<Place>>();
        ArrayList<ArrayList<String>> allPlaceIds = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Spot>> allSpots = new ArrayList<ArrayList<Spot>>();
        ArrayList<String> cityIds = new ArrayList<>();
        ArrayList<String> cityNames = new ArrayList<>();

        Intent intent = new Intent(context, DetailsActivity.class);
        TripParcelableObject tripParcelableObject = new TripParcelableObject();
        tripParcelableObject.setTrip(trip);

        allPlaceIds = Trip.parseForPlaces(trip.getPlaces());
        intent.putExtra("allPlaceIds", allPlaceIds);

        cityIds = trip.getRegions();
        intent.putExtra("cityIdList", cityIds);
        cityNames = trip.getCityNames();
        intent.putExtra("cityNames", cityNames);

        for (ArrayList<String> city : allPlaceIds) {
            ArrayList<Place> placesInCity = new ArrayList<>();
            for (String id : city) {
                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(id, placeFields);
                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    placesInCity.add(place);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: "+exception.getMessage());
                    }});
            }
            allPlaces.add(placesInCity);
        }
        for (ArrayList<String> city : allPlaceIds) {
            ArrayList<Spot> spotsInCity = new ArrayList<>();
            for (String id : city) {
                ParseQuery<Spot> query = ParseQuery.getQuery("Spot");
                query.whereEqualTo("trip", trip.getObjectId());
                query.whereEqualTo("placeId", id);
                query.findInBackground(new FindCallback<Spot>() {
                    @Override
                    public void done(List<Spot> spots, ParseException e) {
                        spotsInCity.add(spots.get(0));
                    }
                });
            }
            allSpots.add(spotsInCity);
        }
        tripParcelableObject.setPlacesInParcel(allPlaces);
        tripParcelableObject.setSpotsInParcel(allSpots);

        intent.putExtra("tripObj", (Parcelable) tripParcelableObject);
        intent.putExtra("tripId", trip.getObjectId());

        intent.putExtra("trip", Parcels.wrap(trip));
        startActivity(intent);
    }
}