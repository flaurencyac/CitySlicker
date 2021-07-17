package com.codepath.cityslicker.fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.cityslicker.BuildConfig;
import com.codepath.cityslicker.PlaceParcelableObject;
import com.codepath.cityslicker.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class AddToTripPOIDialogFragment extends DialogFragment {
    private static final String TAG = "POIDialogFragment";

    private TextView tvPlaceName;
    private TextView tvRating;
    private TextView tvNumRatings;
    private TextView tvWebsiteLink;
    private TextView tvAddress;
    private TextView tvPhoneNumber;
    private TextView tvOpeningHours;
    private ImageView ivPhoto;
    private RatingBar ratingBar;
    private Button btnAddToTrip;
    private ImageButton ibClose;
    protected Bitmap bitmap;

    public AddToTripPOIDialogFragment() {}

    public static AddToTripPOIDialogFragment newInstance(PointOfInterest poi) {
        AddToTripPOIDialogFragment frag = new AddToTripPOIDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("place", (Parcelable) new PlaceParcelableObject(poi));
        frag.setArguments(args);
        return frag;
    }

    public static AddToTripPOIDialogFragment newInstance(Place place) {
        AddToTripPOIDialogFragment frag = new AddToTripPOIDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("place", (Parcelable) new PlaceParcelableObject(place));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_poi, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        YoYo.with(Techniques.FadeIn).duration(500).playOn(getView());
        tvPlaceName = view.findViewById(R.id.tvPlaceName);
        tvRating = view.findViewById(R.id.tvRating);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvWebsiteLink = view.findViewById(R.id.tvWebsiteLink);
        tvNumRatings = view.findViewById(R.id.tvNumRatings);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvOpeningHours = view.findViewById(R.id.tvOpeningHours);
        ivPhoto = view.findViewById(R.id.ivPhoto);
        ratingBar = view.findViewById(R.id.ratingBar);
        btnAddToTrip = view.findViewById(R.id.btnAddToTrip);
        ibClose = view.findViewById(R.id.ibClose);

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnAddToTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: pass the place/poi as a parcel and init the New Trip Form Fragment
                //  Create/save the parse place object when user clicks "add to trip"
                //  if there doesn't already exist a place with the same trip owner and placeID
                //  add objectID or placeID to places array within Trip class

            }
        });

        PlaceParcelableObject placeParcelableObject = (PlaceParcelableObject) getArguments().getParcelable("place");
        Places.initialize(getContext(), BuildConfig.MAPS_API_KEY);
        PlacesClient placesClient = Places.createClient(getContext());
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING,
                Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.USER_RATINGS_TOTAL, Place.Field.PRICE_LEVEL, Place.Field.TYPES, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeParcelableObject.getId(), placeFields);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            tvPlaceName.setText(place.getName());
            fetchPhoto(place.getPhotoMetadatas());
            tvAddress.setText(place.getAddress());
            tvPhoneNumber.setText(place.getPhoneNumber());
            tvRating.setText("" + place.getRating());
            tvNumRatings.setText("(" + place.getUserRatingsTotal()+")");
            ratingBar.setRating(place.getRating().floatValue());
            tvWebsiteLink.setText(""+ place.getWebsiteUri());
            if (place.getOpeningHours() != null) {
                tvOpeningHours.setText(
                        String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s",
                                place.getOpeningHours().getWeekdayText().get(0),
                                place.getOpeningHours().getWeekdayText().get(1),
                                place.getOpeningHours().getWeekdayText().get(2),
                                place.getOpeningHours().getWeekdayText().get(3),
                                place.getOpeningHours().getWeekdayText().get(4),
                                place.getOpeningHours().getWeekdayText().get(5),
                                place.getOpeningHours().getWeekdayText().get(6)));
            }
        });
    }

    private void fetchPhoto(List<PhotoMetadata> photoMetadataList) {
        Places.initialize(getContext(), BuildConfig.MAPS_API_KEY);
        PlacesClient placesClient = Places.createClient(getContext());
        if (photoMetadataList == null || photoMetadataList.isEmpty()) {
            Log.i(TAG, "No photo metadata.");
            bitmap = null;
        }
        final PhotoMetadata photoMetadata = photoMetadataList.get(0);
        final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
            bitmap = fetchPhotoResponse.getBitmap();
            ivPhoto.setImageBitmap(bitmap);
            Log.i(TAG, "photo fetched");
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                Log.e(TAG, "Place not found: " + statusCode);
            }
        });
    }

}