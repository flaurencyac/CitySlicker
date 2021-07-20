package com.codepath.cityslicker.adapters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.cityslicker.BuildConfig;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.activities.DetailsActivity;
import com.codepath.cityslicker.fragments.EditTripFragment;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.codepath.cityslicker.ui.compose.ComposeFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    static final String TAG = "PlaceAdapter";
    Context context;
    ArrayList<Spot> spots = new ArrayList<Spot>();
    ArrayList<Place> places = new ArrayList<Place>();
    String tripId;
    Trip trip;

    public PlaceAdapter(Context context, ArrayList<Place> places, ArrayList<Spot> spots, String tripId, Trip trip) {
        this.context = context;
        this.spots = spots;
        this.places = places;
        this.tripId = tripId;
        this.trip = trip;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View placeView = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(placeView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Place place = places.get(position);
        holder.bind(place);
    }

    @Override
    public int getItemCount() {
        return  places.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements DatePickerDialog.OnDateSetListener {
        private CardView cardView;
        private TextView tvPlaceName;
        private EditText etDate;
        private EditText etTime;
        private Button btnRemove;
        private ImageView ivImage;
        private TextView tvAddress;

        private com.codepath.cityslicker.DatePicker datePickerDialogFragment;
        private Bitmap bitmap;

        public ViewHolder (View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.CardView);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            etDate = itemView.findViewById(R.id.etDate);
            etTime = itemView.findViewById(R.id.etTime);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }

        public void bind(Place place) {
            tvPlaceName.setText(place.getName());
            tvAddress.setText(place.getAddress());
            fetchPhoto(place.getPhotoMetadatas());
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO : remove the cardview, update the adapter notify of data set change
                    // TODO : update the list of spots in Parse for that trip
                    // TODO : remove the spot object rom Parse
                    places.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());


                }
            });
            etTime.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    setTime();
                    return false;
                }
            });
            etTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTime();
                }
            });
            datePickerDialogFragment = new com.codepath.cityslicker.DatePicker();
            //datePickerDialogFragment.setTargetFragment((EditTripFragment) context, 0);
            etDate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    datePickerDialogFragment.show(((DetailsActivity)context).getSupportFragmentManager(), "DATE PICK");
                    return false;
                }
            });
        }

        public void setTime() {
            TimePickerDialog timePickerDialog = new TimePickerDialog((DetailsActivity)context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String amPm;
                    if (hourOfDay >= 12) {
                        amPm = "PM";
                    } else {
                        amPm = "AM";
                    }
                    etTime.setText(String.format("%02d:%02d", hourOfDay, minute) + amPm);
                    saveTime();
                }
            }, 0, 0, false);
            timePickerDialog.show();
        }

        public void saveTime() {
            Spot spot = spots.get(getAdapterPosition());
            spot.setTime(etTime.getText().toString());
            spot.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Unable to save spot time", e);
                    } else {
                        Log.i(TAG, "Saved spot time!");
                    }
                }
            });
        }

        private void fetchPhoto(List<PhotoMetadata> photoMetadataList) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY);
            PlacesClient placesClient = Places.createClient(context);
            if (photoMetadataList == null || photoMetadataList.isEmpty()) {
                Log.i(TAG, "No photo metadata.");
                bitmap = null;
            }
            final PhotoMetadata photoMetadata = photoMetadataList.get(0);
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                bitmap = fetchPhotoResponse.getBitmap();
                ivImage.setImageBitmap(bitmap);
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

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar mCalender = Calendar.getInstance();
            mCalender.set(Calendar.YEAR,year);
            mCalender.set(Calendar.MONTH,month);
            mCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            etDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(mCalender.getTime()));
        }


    }
}
