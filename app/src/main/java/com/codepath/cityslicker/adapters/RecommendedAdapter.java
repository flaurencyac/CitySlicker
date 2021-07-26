package com.codepath.cityslicker.adapters;

import android.content.Context;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.cityslicker.R;
import com.codepath.cityslicker.Utilities;
import com.codepath.cityslicker.models.RecommendedPlace;
import com.codepath.cityslicker.models.Spot;
import com.google.android.libraries.places.api.model.Place;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder>{
    private static final String TAG = "RecommendedAdapter";

    RecommendedSpotListener recommendedSpotListener;
    private Context context;
    private ArrayList<RecommendedPlace> recommendedPlaces = new ArrayList<RecommendedPlace>();

    public RecommendedAdapter(Context context, ArrayList<RecommendedPlace> recommendedPlaces, RecommendedSpotListener listener ) {
        this.context = context;
        this.recommendedPlaces = recommendedPlaces;
        this.recommendedSpotListener = listener;
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
            btnAddToTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recommendedSpotListener.addRecommendedSpot(recommendedPlaces.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface RecommendedSpotListener {
        void addRecommendedSpot(RecommendedPlace recommendedPlace);
    }

}
