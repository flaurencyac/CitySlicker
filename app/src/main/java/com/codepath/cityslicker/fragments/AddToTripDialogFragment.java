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
import com.codepath.cityslicker.Utilities;
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

public class AddToTripDialogFragment extends DialogFragment {
    private static final String TAG = "POIDialogFragment";
    private static final String PARCEL_NAME = "place";
    private final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING,
            Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.USER_RATINGS_TOTAL, Place.Field.PRICE_LEVEL, Place.Field.TYPES, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS);

    private TextView tvPlaceName;
    private TextView tvRating;
    private TextView tvNumRatings;
    private TextView tvWebsiteLink;
    private TextView tvAddress;
    private TextView tvPhoneNumber;
    private TextView tvOpeningHours;
    private TextView tvPrice;
    private ImageView ivPhoto;
    private RatingBar ratingBar;
    private Button btnAddToTrip;
    private ImageButton ibClose;
    private AddToTripPOIDialogFragmentListener listener;
    private PlacesClient placesClient;

    private Boolean addToTrip;
    private String placeId;
    private String placeName;
    private Place mPlace;

    public AddToTripDialogFragment() {}

    public static AddToTripDialogFragment newInstance(PointOfInterest poi) {
        AddToTripDialogFragment frag = new AddToTripDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARCEL_NAME, (Parcelable) new PlaceParcelableObject(poi));
        frag.setArguments(args);
        return frag;
    }

    public static AddToTripDialogFragment newInstance(Place place) {
        AddToTripDialogFragment frag = new AddToTripDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARCEL_NAME, (Parcelable) new PlaceParcelableObject(place));
        frag.setArguments(args);
        return frag;
    }

    public interface AddToTripPOIDialogFragmentListener {
        void onFinishAddToTripPOIDialogFragment(Boolean addToTrip, String placeId, String placeName, Place place);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_add_to_trip_poi, container);
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
        tvPrice = view.findViewById(R.id.tvPrice);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToTrip = false;
                dismiss();
            }
        });
        btnAddToTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToTrip = true;
                listener = (AddToTripPOIDialogFragmentListener) getActivity();
                listener.onFinishAddToTripPOIDialogFragment(addToTrip, placeId, placeName, mPlace);
                dismiss();
            }
        });

        PlaceParcelableObject placeParcelableObject = (PlaceParcelableObject) getArguments().getParcelable("place");
        Places.initialize(getContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(getContext());
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeParcelableObject.getId(), placeFields);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            mPlace = place;
            placeId = place.getId();
            placeName = place.getName();
            Utilities.setDialogViews(getContext(), mPlace, tvPlaceName, tvAddress, tvPrice, tvPhoneNumber, tvRating, tvNumRatings, tvWebsiteLink, tvOpeningHours, ratingBar);
            Utilities.fetchPhoto(getContext(), place.getPhotoMetadatas(), ivPhoto, TAG);
        });
    }

}