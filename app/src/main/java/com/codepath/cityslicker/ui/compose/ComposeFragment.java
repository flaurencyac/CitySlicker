package com.codepath.cityslicker.ui.compose;

import android.app.DatePickerDialog;
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
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.databinding.FragmentComposeBinding;
import com.codepath.cityslicker.databinding.FragmentComposeBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ComposeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "ComposeFragment";

    private EditText etTripName;
    private TextView tvCollaborators;
    private ArrayAdapter<String> adapter;
    private ComposeViewModel composeViewModel;
    private FragmentComposeBinding binding;
    private AutoCompleteTextView actvCollaborators;
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private TextView tvCities;
    private TextView tvFromDate;
    private TextView tvToDate;
    private Button btnFromDate;
    private Button btnToDate;
    private SeekBar seekBar;
    private Button btnCreateTrip;

    private HashMap<String, Integer> collaborators = new HashMap<>();
    private static ArrayList<String> usersList = new ArrayList<>();
    private ArrayList<String> regions = new ArrayList<>();
    private String selectedDate;
    private com.codepath.cityslicker.DatePicker datePickerDialogFragment;
    private Boolean fromDate = false;
    private Boolean toDate = false;
    private ArrayList<String> preferences = new ArrayList<>();
    private Integer budget = 1;


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
                fromDate = true;
                toDate = false;
            }
        });
        btnToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialogFragment.show(getParentFragmentManager(), "DATE PICK");
                fromDate = false;
                toDate = true;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                budget = seekBar.getProgress()+1;
                Toast.makeText(getContext(), "Budget changed to: " + budget, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        btnCreateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: create and save a new trip in Parse
                //  navigate to map of first region
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

    private void addToCollaboratorsList(String username) {
        if (usersList.contains(username)==false) {
            actvCollaborators.setText("");
            Toast.makeText(getContext(), "This user does not exist!", Toast.LENGTH_LONG).show();
        }
        if (collaborators.containsKey(username)) {
            actvCollaborators.setText("");
            Toast.makeText(getContext(), "Cannot add the same person!", Toast.LENGTH_SHORT).show();
        } else {
            collaborators.put(username, 1);
            String dictToString = collaborators.keySet().toString();
            tvCollaborators.setText("Collaborators: "+dictToString.substring(3, dictToString.length()-1));
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
        if (fromDate) {
            tvFromDate.setText("From: "+selectedDate);
        } else if (toDate) {
            tvToDate.setText("To: "+selectedDate);
        }
    }

    public void addPreference(String preference) {
        preferences.add(preference);
    }

    public void removePreference(String preference) {
        preferences.remove(preference);
    }

}