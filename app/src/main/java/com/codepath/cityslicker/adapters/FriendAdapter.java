package com.codepath.cityslicker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.models.Trip;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    Context context;
    List<ParseUser> friends;

    public FriendAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.friends = users;
    }


    @NonNull
    @NotNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new FriendAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FriendAdapter.ViewHolder holder, int position) {
        ParseUser user = friends.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFriendName;
        ImageView ivImage;

        public ViewHolder (View itemView) {
            super(itemView);
            tvFriendName = itemView.findViewById(R.id.tvFriendName);
            ivImage = itemView.findViewById(R.id.ivImage);
        }

        public void bind(ParseUser user) {
            tvFriendName.setText(user.getUsername());
            ParseFile image = user.getParseFile("profilePicture");
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
        }
    }
}
