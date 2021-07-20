package com.codepath.cityslicker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.codepath.cityslicker.activities.LoginActivity;
import com.codepath.cityslicker.ui.compose.ComposeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.codepath.cityslicker.databinding.ActivityMainBinding;
import com.parse.ParseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String HEALTH_PREFERENCE = "health";
    private static final String SHOPPING_PREFERENCE = "shopping";
    private static final String RESTAURANT_PREFERENCE = "restaurants";
    private static final String ADULT_PREFERENCE = "adult";
    private static final String FAMILY_PREFERENCE = "family";
    private static final String ATTRACTIONS_PREFERENCE = "attractions";
    private ActivityMainBinding binding;
    public static ArrayList<String> preferences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_explore, R.id.navigation_quick_compose, R.id.navigation_profile).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Log.i(TAG, "logout");
            ParseUser.logOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.cbRestaurants:
                if (checked) {
                    Toast.makeText(this, "Added Restaurants to preferences", Toast.LENGTH_SHORT).show();
                    preferences.add(RESTAURANT_PREFERENCE);
                } else {
                    Toast.makeText(this, "Removed Restaurants from preferences", Toast.LENGTH_SHORT).show();
                    preferences.remove(RESTAURANT_PREFERENCE);
                }
                break;
            case R.id.cbHealth:
                if (checked) {
                    Toast.makeText(this, "Added Health to preferences", Toast.LENGTH_SHORT).show();
                    preferences.add(HEALTH_PREFERENCE);
                } else {
                    Toast.makeText(this, "Removed Health from preferences", Toast.LENGTH_SHORT).show();
                    preferences.remove(HEALTH_PREFERENCE);
                }
                break;
            case R.id.cbAdult:
                if (checked) {
                    Toast.makeText(this, "Added Adult to preferences", Toast.LENGTH_SHORT).show();
                    preferences.add(ADULT_PREFERENCE);
                } else {
                    Toast.makeText(this, "Removed Adult from preferences", Toast.LENGTH_SHORT).show();
                    preferences.remove(ADULT_PREFERENCE);
                }
                break;
            case R.id.cbFamilyFriendly:
                if (checked) {
                    Toast.makeText(this, "Added Family Friendly to preferences", Toast.LENGTH_SHORT).show();
                    preferences.add(FAMILY_PREFERENCE);
                } else {
                    Toast.makeText(this, "Removed Family Friendly from preferences", Toast.LENGTH_SHORT).show();
                    preferences.remove(FAMILY_PREFERENCE);
                }
                break;
            case R.id.cbAttractions:
                if (checked) {
                    Toast.makeText(this, "Added Tourist Attractions to preferences", Toast.LENGTH_SHORT).show();
                    preferences.add(ATTRACTIONS_PREFERENCE);
                } else {
                    Toast.makeText(this, "Removed Tourist Attractions from preferences", Toast.LENGTH_SHORT).show();
                    preferences.remove(ATTRACTIONS_PREFERENCE);
                }
                break;
            case R.id.cbShopping:
                if (checked) {
                    Toast.makeText(this, "Added Shopping to preferences", Toast.LENGTH_SHORT).show();
                    preferences.add(SHOPPING_PREFERENCE);
                } else {
                    Toast.makeText(this, "Removed Shopping from preferences", Toast.LENGTH_SHORT).show();
                    preferences.remove(SHOPPING_PREFERENCE);
                }
                break;
        }
    }

}