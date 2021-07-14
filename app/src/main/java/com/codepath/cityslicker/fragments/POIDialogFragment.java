package com.codepath.cityslicker.fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.cityslicker.PlaceParcelableObject;
import com.codepath.cityslicker.R;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.libraries.places.api.model.Place;

import org.parceler.Parcels;

public class POIDialogFragment extends DialogFragment {

    private TextView tvPlaceName;
    private TextView tvRating;
    private TextView tvNumRatings;
    private TextView tvDescription;
    private TextView tvAddress;
    private TextView tvPhoneNumber;
    private TextView tvOpeningHours;
    private ImageView ivPhoto;
    private RatingBar ratingBar;
    private Button btnNewTrip;
    private Button btnExistingTrip;
    private ImageButton ibClose;

    public POIDialogFragment() {}

    public static POIDialogFragment newInstance(PointOfInterest poi) {
        POIDialogFragment frag = new POIDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("place", (Parcelable) new PlaceParcelableObject(poi));
        frag.setArguments(args);
        return frag;
    }

    public static POIDialogFragment newInstance(Place place) {
        POIDialogFragment frag = new POIDialogFragment();
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
        tvPlaceName = view.findViewById(R.id.tvPlaceName);
        tvRating = view.findViewById(R.id.tvRating);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvNumRatings = view.findViewById(R.id.tvNumRatings);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvOpeningHours = view.findViewById(R.id.tvOpeningHours);
        ivPhoto = view.findViewById(R.id.ivPhoto);
        ratingBar = view.findViewById(R.id.ratingBar);
        btnExistingTrip = view.findViewById(R.id.btnExisitngTrip);
        btnNewTrip = view.findViewById(R.id.btnNewTrip);
        ibClose = view.findViewById(R.id.ibClose);

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Create/save the parse object, pass the place/poi as a parcel and init the New Trip Form Activity
            }
        });


        PlaceParcelableObject placeParcelableObject = (PlaceParcelableObject) getArguments().getParcelable("place");

        tvPlaceName.setText(placeParcelableObject.getName());

    }
}