package com.codepath.cityslicker.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.adapters.ProfileFragmentAdapter;
import com.codepath.cityslicker.adapters.TripsAdapter;
import com.codepath.cityslicker.databinding.FragmentProfileBinding;
import com.codepath.cityslicker.models.Trip;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    public final static int PICK_PHOTO_CODE = 1000;
    private static final int NUM_PAGES = 2;

    private Context context;
    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private TextView tvUpdatePP;
    private TextView tvUsername;
    private ImageView ivProfilePicture;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProfileFragmentAdapter fragmentAdapter;

    private List<Trip> trips = new ArrayList<>();
    private List<ParseUser> friends = new ArrayList<>();

    private Bitmap bitmap;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getContext();
        tvUpdatePP = binding.tvUpdatePP;
        tvUsername = binding.tvUsername;
        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;
        ivProfilePicture = binding.ivProfilePicture;

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        ParseFile profilePicture = (ParseFile) ParseUser.getCurrentUser().get("profilePicture");
        if (profilePicture != null) {
            Glide.with(getContext()).load(profilePicture.getUrl()).circleCrop().into(ivProfilePicture);
        } else {
            Glide.with(getContext()).load(R.drawable.ic_baseline_account_circle_24).circleCrop().into(ivProfilePicture);
        }
        tvUpdatePP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

        FragmentManager fm = getActivity().getSupportFragmentManager();
        getAllTrips(fm);

        tabLayout.addTab(tabLayout.newTab().setText("Trips"));
        tabLayout.addTab(tabLayout.newTab().setText("Friends"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    private void getAllTrips(FragmentManager fm) {
        ArrayList<Trip> objects = (ArrayList<Trip>) ParseUser.getCurrentUser().get("trips");
        if (objects != null) {
            trips.addAll(objects);
            Collections.reverse(trips);
        }
        if (ParseUser.getCurrentUser().get("sharedTrips") != null) {
            ArrayList<String> tripIds = new ArrayList<String>();
            tripIds = (ArrayList<String>) ParseUser.getCurrentUser().get("sharedTrips");
            for (String id : tripIds) {
                ParseQuery<Trip> query = ParseQuery.getQuery("Trip");
                query.getInBackground(id, (object, e) -> {
                    if (e == null) {
                        trips.add(object);
                    }
                });
            }
        }
        getAllFriends(fm);
    }

    private void getAllFriends(FragmentManager fm) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.addDescendingOrder("createdAt");
        query.findInBackground((users, e) -> {
            if (e == null) {
                friends.addAll(users);
                fragmentAdapter = new ProfileFragmentAdapter(fm, getLifecycle(), context, trips, friends);
                viewPager.setAdapter(fragmentAdapter);
            }
        });
    }

    public void onPickPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            bitmap = loadFromUri(photoUri);
            Glide.with(getContext()).asBitmap().load(bitmap).circleCrop().into(ivProfilePicture);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
            byte[] byteArray = stream.toByteArray();
            ParseFile newProfilePicture = new ParseFile("profile.png", byteArray);
            ParseUser user = ParseUser.getCurrentUser();
            user.put("profilePicture", newProfilePicture);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.i(TAG, "Successfully uploaded image");
                        Toast.makeText(getContext(), "Profile pic uploaded!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}