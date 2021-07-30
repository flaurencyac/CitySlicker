package com.codepath.cityslicker.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Rating;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.cityslicker.BuildConfig;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.Utilities;
import com.codepath.cityslicker.activities.MapsActivity;
import com.codepath.cityslicker.models.RecommendedPlace;
import com.codepath.cityslicker.models.Spot;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tyrantgit.explosionfield.ExplosionField;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder>{
    private static final String TAG = "RecommendedAdapter";
    private final List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING,
            Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.USER_RATINGS_TOTAL, Place.Field.PRICE_LEVEL, Place.Field.TYPES, Place.Field.PHOTO_METADATAS);

    private PlacesClient placesClient;
    RecommendedSpotListener recommendedSpotListener;
    private Context context;
    private ArrayList<RecommendedPlace> recommendedPlaces = new ArrayList<RecommendedPlace>();

    public RecommendedAdapter(Context context, ArrayList<RecommendedPlace> recommendedPlaces, RecommendedSpotListener listener ) {
        this.context = context;
        this.recommendedPlaces = recommendedPlaces;
        this.recommendedSpotListener = listener;
        Places.initialize(context, BuildConfig.MAPS_API_KEY);
        this.placesClient = Places.createClient(context);
    }

    @NonNull
    @NotNull
    @Override
    public RecommendedAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View placeView = LayoutInflater.from(context).inflate(R.layout.item_recommended, parent, false);
        return new RecommendedAdapter.ViewHolder(placeView, recommendedSpotListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecommendedAdapter.ViewHolder holder, int position) {
        RecommendedPlace place = recommendedPlaces.get(position);
        holder.bind(place);
    }

    @Override
    public int getItemCount() {
        return recommendedPlaces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPlaceName;
        private TextView tvRating;
        private RatingBar ratingBar;
        private TextView tvNumRatings;
        private TextView tvWebsiteLink;
        private TextView tvAddress;
        private TextView tvPhoneNumber;
        private TextView tvPrice;
        private TextView tvOpeningHours;
        private ImageView ivPhoto;
        private Button btnAddToTrip;
        private RecommendedSpotListener recommendedSpotListener;
        private ExplosionField explosionField;

        public ViewHolder(@NonNull @NotNull View itemView, RecommendedSpotListener listener) {
            super(itemView);
            tvPlaceName=itemView.findViewById(R.id.tvPlaceName);
            tvRating = itemView.findViewById(R.id.tvRating);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvNumRatings = itemView.findViewById(R.id.tvNumRatings);
            tvWebsiteLink = itemView.findViewById(R.id.tvWebsiteLink);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvOpeningHours = itemView.findViewById(R.id.tvOpeningHours);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            btnAddToTrip = itemView.findViewById(R.id.btnAddToTrip);
            tvPrice = itemView.findViewById(R.id.tvPrice);

            this.recommendedSpotListener = listener;
        }

        public void bind(RecommendedPlace place) {
            tvPlaceName.setText(place.getName());
            tvAddress.setText(place.getVicinity());
            tvNumRatings.setText("(" + place.getUserRatingsTotal()+")");
            tvRating.setText("" + place.getRating());
            if (place.getRating() != null) {
                ratingBar.setRating(place.getRating().floatValue());
            }
            tvOpeningHours.setText("Open now: "+place.getOpen());
            explosionField = ExplosionField.attach2Window((MapsActivity) context);
            btnAddToTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recommendedSpotListener.addRecommendedSpot(recommendedPlaces.get(getAdapterPosition()));
                    explosionField.explode(btnAddToTrip);
                }
            });
            setPhoto(place, ivPhoto, tvPrice, tvWebsiteLink, tvPhoneNumber);

        }
    }

    private void setPhoto(RecommendedPlace recommendedPlace, ImageView ivPhoto, TextView tvPrice, TextView tvWebsiteLink, TextView tvPhoneNumber) {
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(recommendedPlace.getPlaceId(), placeFields);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Utilities.fetchPhoto(context, place.getPhotoMetadatas(), ivPhoto, TAG);
            tvPrice.setText(Utilities.convertToDollars(place.getPriceLevel()));
            tvWebsiteLink.setText(""+place.getWebsiteUri());
            tvPhoneNumber.setText(""+place.getPhoneNumber());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: "+exception.getMessage());
            }});
    }

    public interface RecommendedSpotListener {
        void addRecommendedSpot(RecommendedPlace recommendedPlace);
    }

}
