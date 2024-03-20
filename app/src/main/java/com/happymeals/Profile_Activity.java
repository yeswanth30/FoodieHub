package com.happymeals;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Profile_Activity extends AppCompatActivity {

    TextView logout, Nametextview, Phonetextview, Emailtextview, Usernametextview, passwordtextview,Nametextview1;
    ImageView leftImageView, edit,back;
    ProgressBar progressBar;
    private static final int EDIT_PROFILE_REQUEST_CODE = 1;

    Dialog dialog; // Declare dialog as a member variable


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        edit = findViewById(R.id.edit);
        Nametextview1 = findViewById(R.id.Nametextview1);
        Nametextview = findViewById(R.id.Nametextview);
        Phonetextview = findViewById(R.id.Phonetextview);
        Emailtextview = findViewById(R.id.Emailtextview);
        Usernametextview = findViewById(R.id.Usernametextview);
        passwordtextview = findViewById(R.id.passwordtextview);
        leftImageView = findViewById(R.id.leftImageView);
        progressBar = findViewById(R.id.progressBar);
        back = findViewById(R.id.back);

       // overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);



        leftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String imageUrl = sharedPreferences.getString("imageurl", "");
                showFullImageDialog(imageUrl);
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

//        logout.setOnClickListener(v -> logoutUser());

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(Profile_Activity.this, EditProfileActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
        });

        loadProfile(); // Load profile details when the activity is created
    }

    private void loadProfile() {
        progressBar.setVisibility(View.VISIBLE); // Show progress bar while loading
        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String Name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");
        String username = sharedPreferences.getString("username", "");
        String phone = sharedPreferences.getString("phone", "");
        String password = sharedPreferences.getString("password", "");
        String imageurl = sharedPreferences.getString("imageurl", "");

        Nametextview.setText(Name);
        Nametextview1.setText(Name);
        Phonetextview.setText(phone);
        Emailtextview.setText(email);
        Usernametextview.setText(username);
        passwordtextview.setText(password);

        if (imageurl != null && !imageurl.isEmpty()) {
            Picasso.get().load(imageurl)
                    .placeholder(R.drawable.authorrr) // Placeholder image while loading
                    .error(R.drawable.authorrr) // Image to show if loading fails
                    .into(leftImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Hide progress bar when image successfully loaded
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            // Hide progress bar when image loading fails
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            // Hide progress bar if no image URL is available
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Handle the updated data received from EditProfileActivity
            loadProfile(); // Reload profile details including image
        }
    }

    private void logoutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> logout());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear all preferences, including the switch state
        editor.clear();
        editor.apply();

        // Navigate to the login page
        Intent intent = new Intent(this, LoginActivity.class);
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();

        startActivity(intent);
        finish();
    }

    private void showFullImageDialog(String imageUrl) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_full_image);

        ImageView fullImageView = dialog.findViewById(R.id.fullImageView);
        Picasso.get().load(imageUrl).into(fullImageView);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation1;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}