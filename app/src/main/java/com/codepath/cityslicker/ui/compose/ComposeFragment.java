package com.codepath.cityslicker.ui.compose;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.codepath.cityslicker.BuildConfig;
import com.codepath.cityslicker.MainActivity;
import com.codepath.cityslicker.PlaceParcelableObject;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.TripParcelableObject;
import com.codepath.cityslicker.activities.MapsActivity;
import com.codepath.cityslicker.databinding.FragmentComposeBinding;
import com.codepath.cityslicker.models.Trip;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.parceler.Parcels;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ComposeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "ComposeFragment";

    private EditText etTripName;
    private TextView tvCollaborators;
    private ArrayAdapter<String> adapter;
    private ComposeViewModel composeViewModel;
    private FragmentComposeBinding binding;
    private AutoCompleteTextView actvCollaborators;
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private com.codepath.cityslicker.DatePicker datePickerDialogFragment;
    private TextView tvCities;
    private TextView tvFromDate;
    private TextView tvToDate;
    private Button btnFromDate;
    private Button btnToDate;
    private SeekBar seekBar;
    private SeekBar sbAdult;
    private SeekBar sbFamily;
    private SeekBar sbFood;
    private SeekBar sbAttractions;
    private SeekBar sbShopping;
    private Button btnCreateTrip;

    public static ArrayList<String> preferences = new ArrayList<>();
    private ArrayList<String> collaborators = new ArrayList<>();
    private ArrayList<String> cityIDs = new ArrayList<>();
    private ArrayList<String> cityNames = new ArrayList<>();
    private static ArrayList<String> usersList = new ArrayList<>();
    private ArrayList<String> regions = new ArrayList<>();
    private Boolean fromDateBool = false;
    private Boolean toDateBool = false;
    private String selectedDate;
    private Integer budget = 1;
    private Date startDate;
    private Date endDate;
    private String tripId;
    private Trip trip;
    private Integer adultPref;
    private Integer familyPref;
    private Integer foodPref;
    private Integer attractionsPref;
    private Integer shoppingPref;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        composeViewModel = new ViewModelProvider(this).get(ComposeViewModel.class);
        binding = FragmentComposeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        etTripName = binding.etTripName;
        tvFromDate = binding.tvFromDate;
        tvToDate = binding.tvToDate;
        btnFromDate = binding.btnFromDate;
        btnToDate = binding.btnToDate;
        tvCities = binding.tvCities;
        seekBar = binding.seekBar;
        sbAdult = binding.sbAdult;
        sbAttractions = binding.sbAttractions;
        sbFamily = binding.sbFamily;
        sbFood = binding.sbFood;
        sbShopping = binding.sbShopping;
        btnCreateTrip = binding.btnCreateTrip;

        autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        actvCollaborators = binding.actvCollaborators;
        tvCollaborators = binding.tvCollaborators;
        tvCollaborators.setText("Collaborators: ");
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, usersList);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findUsers();
        actvCollaborators.setAdapter(adapter);
        actvCollaborators.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addToCollaboratorsList(actvCollaborators.getText().toString());
                return true;
            }
        });
        Places.initialize(getContext(), BuildConfig.MAPS_API_KEY);
        autocompleteSupportFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPlaceSelected(@NonNull @NotNull Place selectedPlace) {
                addToRegions(selectedPlace.getName());
                cityIDs.add(selectedPlace.getId());
                cityNames.add(selectedPlace.getName());
            }
            @Override
            public void onError(@NonNull @NotNull Status status) {
                Log.i(TAG, "Error searching occurred: " + status);
            }
        });
        datePickerDialogFragment = new com.codepath.cityslicker.DatePicker();
        datePickerDialogFragment.setTargetFragment(ComposeFragment.this, 0);
        btnFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialogFragment.show(getParentFragmentManager(), "DATE PICK");
                fromDateBool = true;
                toDateBool = false;
            }
        });
        btnToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialogFragment.show(getParentFragmentManager(), "DATE PICK");
                fromDateBool = false;
                toDateBool = true;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                budget = seekBar.getProgress()+1;
                Toast.makeText(getContext(), "Budget changed to: " + budget, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        sbShopping.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                shoppingPref = sbShopping.getProgress();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        sbFood.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                foodPref = sbFood.getProgress();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        sbFamily.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                familyPref = sbFamily.getProgress();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        sbAdult.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                adultPref = sbAdult.getProgress();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        sbAttractions.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                attractionsPref = sbAttractions.getProgress();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        btnCreateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDate == null || endDate == null) {
                    Toast.makeText(getContext(), "Choose a start and end date!", Toast.LENGTH_SHORT).show();
                } else if (startDate.after(endDate)) {
                    Toast.makeText(getContext(), "The start date cannot be after the end date!", Toast.LENGTH_SHORT).show();
                } else if (cityIDs.isEmpty()) {
                    Toast.makeText(getContext(), "Choose at least one city!", Toast.LENGTH_SHORT).show();
                } else if (etTripName.getText().equals("")) {
                    Toast.makeText(getContext(), "Your trip must have a name!", Toast.LENGTH_SHORT).show();
                } else {
                    createTrip();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void findUsers() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground((users, e) -> {
            if (e == null) {
                for(ParseUser user : users) {
                    Log.i("User List ",(user.getUsername()));
                    usersList.add(user.getUsername());
                }
            } else {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addToCollaboratorsList(String username) {
        if (usersList.contains(username)==false) {
            actvCollaborators.setText("");
            Toast.makeText(getContext(), "This user does not exist!", Toast.LENGTH_LONG).show();
        }
        if (collaborators.contains(username)) {
            actvCollaborators.setText("");
            Toast.makeText(getContext(), "Cannot add the same person!", Toast.LENGTH_SHORT).show();
        } else {
            collaborators.add(username);
            tvCollaborators.setText("Collaborators: "+ String.join(",", collaborators));
            actvCollaborators.setText("");
            usersList.remove(username);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addToRegions(String cityName) {
        regions.add(cityName);
        tvCities.setText("Cities " + String.join(", ", regions));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalender = Calendar.getInstance();
        mCalender.set(Calendar.YEAR,year);
        mCalender.set(Calendar.MONTH,month);
        mCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalender.getTime());
        if (fromDateBool) {
            tvFromDate.setText("From: "+selectedDate);
            startDate = mCalender.getTime();
        } else if (toDateBool) {
            tvToDate.setText("To: "+selectedDate);
            endDate = mCalender.getTime();
        }
    }

    private void resetForm() {
        etTripName.setText("");
        tvCities.setText("Cities: ");
        tvCollaborators.setText("Collaborators: ");
        seekBar.setProgress(0);
        tvFromDate.setText("From: ");
        tvToDate.setText("To: ");
        startDate = null;
        endDate = null;
        budget = 1;
    }

    private void createTrip() {
        trip = new Trip();
        trip.setBudget(budget);
        // the subList removes the last elem of the array because the last elem of the array is " " empty string
        if (collaborators.size() >= 2) {
            trip.setCollaborators(new JSONArray(collaborators.subList(0, collaborators.size()-1)));
        }
        trip.setCityNames(new JSONArray(cityNames));
        trip.setFamilyPreference(sbFamily.getProgress());
        trip.setFoodPreference(sbFood.getProgress());
        trip.setAdultPreference(sbAdult.getProgress());
        trip.setAttractionsPreference(sbAttractions.getProgress());
        trip.setShoppingPreference(sbShopping.getProgress());
        trip.setTripName(etTripName.getText().toString());
        trip.setOwner(ParseUser.getCurrentUser());
        trip.setEndDate(endDate);
        trip.setStartDate(startDate);
        trip.setRegions(new JSONArray(cityIDs));
        trip.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(getContext(), "Created Trip", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Trip Created");
                    tripId = trip.getObjectId();
                    resetForm();
                    Intent intent = new Intent(getContext(), MapsActivity.class);
                    intent.putExtra("tripId", tripId);
                    TripParcelableObject parcel = new TripParcelableObject();
                    parcel.setTrip(trip);
                    intent.putExtra("tripObj", Parcels.wrap(parcel));
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}