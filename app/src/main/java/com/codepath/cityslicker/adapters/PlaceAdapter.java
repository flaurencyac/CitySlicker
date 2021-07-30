package com.codepath.cityslicker.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.cityslicker.BuildConfig;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.Utilities;
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
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        sortSpotsAndPlaces();
    }

    public void sortSpotsAndPlaces() {
        Collections.sort(spots, Spot.spotDateTimeComparator);
        ArrayList<Place> sortedPlaces = new ArrayList<Place>();
        for (int i= 0; i <spots.size(); i++) {
            for (int j=0; j<places.size(); j++) {
                if (spots.get(i).getPlaceID().equals(places.get(j).getId())) {
                    sortedPlaces.add(places.get(j));
                }
            }
        }
        places=sortedPlaces;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final String pattern = "MM/dd/yyyy HH:mm:ss";

        private CardView cardView;
        private TextView tvPlaceName;
        private EditText etDate;
        private EditText etTime;
        private Button btnRemove;
        private ImageView ivImage;
        private TextView tvAddress;
        private DatePickerDialog datePickerDialog;
        private TimePickerDialog timePickerDialog;
        private Bitmap bitmap;
        private Integer mYear;
        private Integer mMonth;
        private Integer mDay;
        private Date selectedDate;

        private Boolean firstDatePicker = true;
        private Boolean firstTimePicker = true;

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

        @SuppressLint("ClickableViewAccessibility")
        public void bind(Place place) {
            tvPlaceName.setText(place.getName());
            tvAddress.setText(place.getAddress());
            Utilities.fetchPhoto(context, place.getPhotoMetadatas(), ivImage, TAG);
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeSpotFromTrip();
                }
            });
            if (spots.get(getAdapterPosition()).getTime() != null) {
                etTime.setText(spots.get(getAdapterPosition()).getTime());
            }
            etTime.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    firstTimePicker = true;
                    timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            if (firstTimePicker) {
                                firstTimePicker = false;
                                String timeOfDay;
                                if (hourOfDay < 12) {
                                    timeOfDay = "AM";
                                } else {
                                    timeOfDay = "PM";
                                }
                                etTime.setText(String.format("%02d:%02d %s", hourOfDay, minute, timeOfDay));
                                saveDateTime();
                            }
                        }
                    }, 0,0 , false);
                    timePickerDialog.show();
                    return true;
                }
            });
            if (spots.get(getAdapterPosition()).getDate() != null) {
                DateFormat df = new SimpleDateFormat(pattern);
                String formattedDate = df.format(spots.get(getAdapterPosition()).getDate()).substring(0,10);
                etDate.setText(formattedDate);
            }
            etDate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    firstDatePicker = true;
                    final Calendar currentDate = Calendar.getInstance();
                    datePickerDialog = new DatePickerDialog(context, R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            if (firstDatePicker) {
                                firstDatePicker = false;
                                mYear = year;
                                mDay = dayOfMonth;
                                mMonth = month;
                                etDate.setText(String.format("%d/%d/%d", mMonth, mDay, mYear));
                                Calendar mCalender = Calendar.getInstance();
                                mCalender.set(Calendar.YEAR,year);
                                mCalender.set(Calendar.MONTH,month);
                                mCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                                selectedDate = mCalender.getTime();
                                saveDateTime();
                            }
                        }
                    }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getDatePicker().setMinDate(trip.getStartDate().getTime());
                    datePickerDialog.getDatePicker().setMaxDate(trip.getEndDate().getTime());
                    datePickerDialog.show();
                    return true;
                }
            });
        }

        private void removeSpotFromTrip() {
            ArrayList<ArrayList<String>> newPlacesList = Trip.parseForPlaces(trip.getPlaces());
            for (int i = 0; i<newPlacesList.size(); i++) {
                for (int j = 0; j<newPlacesList.get(i).size(); j++) {
                    if (newPlacesList.get(i).get(j).equals(spots.get(getAdapterPosition()).getPlaceID())) {
                        newPlacesList.get(i).remove(j);
                    }
                }
            }
            trip.setPlaces(new JSONArray(newPlacesList));
            trip.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e== null) {
                        Log.i(TAG, "Success: removed spot from trip");
                    } else {
                        Log.e(TAG, "Error removing spot from trip", e);
                    }

                }
            });
            places.remove(getAdapterPosition());
            spots.get(getAdapterPosition()).deleteInBackground(e -> {
                if (e == null) {
                    Log.i(TAG, "Deleted spot from Parse db");
                } else {
                    Log.e(TAG, "Failed to delete spot from Parse", e);
                }
            });
            spots.remove(getAdapterPosition());
            PlaceAdapter.this.notifyItemRemoved(getAdapterPosition());
        }

        private void saveDateTime() {
            if (selectedDate==null) {
                Toast.makeText(context, "Please choose a date!", Toast.LENGTH_SHORT).show();
            } else if (etTime.getText().toString().equals("")) {
                Toast.makeText(context, "Please choose a time!", Toast.LENGTH_SHORT).show();
            } else {
                Spot spot = spots.get(getAdapterPosition());
                spot.setDate(selectedDate);
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
        }
    }
}
