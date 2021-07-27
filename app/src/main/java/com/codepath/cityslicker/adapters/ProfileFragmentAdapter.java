package com.codepath.cityslicker.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.codepath.cityslicker.fragments.FriendsFragment;
import com.codepath.cityslicker.fragments.TripsFragment;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.google.android.libraries.places.api.model.Place;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.CertPathTrustManagerParameters;

public class ProfileFragmentAdapter extends FragmentStateAdapter {
    Context context;
    List<Trip> trips;
    List<ParseUser> users;

    public ProfileFragmentAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle,
                                  Context context, List<Trip> trips, List<ParseUser> users) {
        super(fragmentManager, lifecycle);
        this.context = context;
        this.users = users;
        this.trips = trips;
    }


    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
       switch (position) {
           case 1:
               return new FriendsFragment(context, users);
           default:
               return new TripsFragment(context, trips);
       }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
