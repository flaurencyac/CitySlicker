package com.codepath.cityslicker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.cityslicker.R;
import com.codepath.cityslicker.adapters.FriendAdapter;
import com.codepath.cityslicker.adapters.TripsAdapter;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FriendsFragment extends Fragment {
    private static final String TAG = "FriendsFragment";
    private Context context;

    private RecyclerView rvFriends;
    private List<ParseUser> friends;
    private FriendAdapter adapter;


    public FriendsFragment(Context context, List<ParseUser> users) {
        this.context = context;
        this.friends= users;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFriends = view.findViewById(R.id.rvTrips);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        rvFriends.setLayoutManager(llm);
        adapter = new FriendAdapter(context, friends);
        rvFriends.setAdapter(adapter);
    }
}
