package com.codepath.cityslicker.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.codepath.cityslicker.MainActivity;
import com.codepath.cityslicker.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;


public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private Context context;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Switch switchPasswordVisibility;
    private Boolean showPassword;
    private Button btnSignup;
    private KonfettiView konfettiView;
    private Drawable plane;
    private Shape.DrawableShape planeShape;
    private ImageView ivGlobe;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        showPassword = false;
        btnSignup = findViewById(R.id.btnSignup);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        switchPasswordVisibility = findViewById(R.id.switchPasswordVisibility);
        btnLogin = findViewById(R.id.btnLogin);
        ivGlobe  = findViewById(R.id.ivGlobe);
        konfettiView = findViewById(R.id.viewKonfetti);

        //RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //rotate.setDuration(1000000);
        //rotate.setInterpolator(new LinearInterpolator());

        Animation aniRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        ivGlobe.startAnimation(aniRotate);

        plane = ContextCompat.getDrawable(context, R.drawable.ic_baseline_flight_24);
        planeShape  = new Shape.DrawableShape(plane, true);
        konfettiView.build()
                .addColors(Color.TRANSPARENT, Color.WHITE, Color.argb( 100, 0,191,255))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, planeShape)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 3000L);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                login(username, password);
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                user.setUsername(username);
                user.setPassword(password);
                user.put("username", username);
                user.put("password", password);
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            login(username, password);
                        } else {
                            Log.e(TAG, "Unable to sign up user", e);
                        }
                    }
                });
            }
        });
        switchPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick password visibility switch");
                if (showPassword == true) {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPassword = false;
                } else {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPassword = true;
                }
            }
        });

    }

    private void login(String username, String password) {
        Log.i(TAG, "Attempting to login user " +username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login"+e, e);
                    Toast.makeText(LoginActivity.this, "Unable to log in", Toast.LENGTH_SHORT).show();
                } else {


                    goMainActivity();
                }
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
