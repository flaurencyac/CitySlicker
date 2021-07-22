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
import com.google.android.libraries.places.api.model.Place;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Place> recommendedPlaces = new ArrayList<Place>();

    public RecommendedAdapter(Context context, ArrayList<Place> recommendedPlaces) {
        this.context = context;
        this.recommendedPlaces = recommendedPlaces;
    }

    @NonNull
    @NotNull
    @Override
    public RecommendedAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View placeView = LayoutInflater.from(context).inflate(R.layout.item_recommended, parent, false);
        return new RecommendedAdapter.ViewHolder(placeView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecommendedAdapter.ViewHolder holder, int position) {
        Place place = recommendedPlaces.get(position);
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

        public ViewHolder(@NonNull @NotNull View itemView) {
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
        }

        public void bind(Place place) {
            tvPlaceName.setText(place.getName());
            tvAddress.setText(place.getAddress());
            tvPhoneNumber.setText(place.getPhoneNumber());
            tvNumRatings.setText("(" + place.getUserRatingsTotal()+")");
            tvRating.setText("" + place.getRating());
            if (place.getPriceLevel() != null) {
                tvPrice.setText(Utilities.convertToDollars(place.getPriceLevel()));
            } else {
                tvPrice.setText("Unknown price level");
            }
            if (place.getRating() != null) {
                ratingBar.setRating(place.getRating().floatValue());
            }
            if (place.getWebsiteUri()!= null) {
                tvWebsiteLink.setText(""+ place.getWebsiteUri());
            }
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
            // TODO get photo of place
            btnAddToTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO : if clicked communicate to maps activity that this should be
                }
            });
        }
    }
}
