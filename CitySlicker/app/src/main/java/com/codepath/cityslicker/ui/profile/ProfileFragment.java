package com.codepath.cityslicker.ui.profile;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.codepath.cityslicker.R;
import com.codepath.cityslicker.databinding.FragmentProfileBinding;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    public final static int PICK_PHOTO_CODE = 1000;

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private TextView tvUpdatePP;
    private TextView tvUsername;
    private ImageView ivProfilePicture;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabItem tabFriends;
    private TabItem tabTrips;

    private Bitmap bitmap;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvUpdatePP = binding.tvUpdatePP;
        tvUsername = binding.tvUsername;
        tabLayout = binding.tabLayout;
        viewPager2 = binding.viewPager;
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