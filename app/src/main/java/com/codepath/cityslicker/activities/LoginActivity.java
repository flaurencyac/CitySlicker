package com.codepath.cityslicker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.cityslicker.MainActivity;
import com.codepath.cityslicker.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;


public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Switch switchPasswordVisibility;
    private Boolean showPassword;
    private Button btnSignup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        showPassword = false;
        btnSignup = findViewById(R.id.btnSignup);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        switchPasswordVisibility = findViewById(R.id.switchPasswordVisibility);
        btnLogin = findViewById(R.id.btnLogin);
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
                    Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
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
