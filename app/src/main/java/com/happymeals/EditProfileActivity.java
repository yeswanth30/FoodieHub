package com.happymeals;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {

    EditText nameEditText, phoneEditText, usernameEditText, passwordEditText;
    TextView saveButton, emailEditText; // Change from EditText to TextView for email
    ImageView profileImageView, back;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    String userid;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        nameEditText = findViewById(R.id.editNameEditText);
        phoneEditText = findViewById(R.id.editPhoneEditText);
        emailEditText = findViewById(R.id.editEmailEditText); // Change EditText to TextView
        usernameEditText = findViewById(R.id.editUsernameEditText);
        passwordEditText = findViewById(R.id.editPasswordEditText);
        saveButton = findViewById(R.id.saveButton);
        profileImageView = findViewById(R.id.profileImageView);

        profileImageView.setOnClickListener(v -> openFileChooser());

        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String phone = sharedPreferences.getString("phone", "");
        String email = sharedPreferences.getString("email", "");
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        userid = sharedPreferences.getString("userid", "");
        String imageUrl = sharedPreferences.getString("imageurl", ""); // Get the profile image URL

        nameEditText.setText(name);
        phoneEditText.setText(phone);
        emailEditText.setText(email); // Set email as TextView
        usernameEditText.setText(username);
        passwordEditText.setText(password);
        if (!imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl)
                    .placeholder(R.drawable.authorrr)
                    .error(R.drawable.authorrr)
                    .into(profileImageView);
        }

        // Disable editing for email field
        emailEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display toast message indicating email cannot be changed
                Toast.makeText(EditProfileActivity.this, "Email cannot be changed", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> saveProfileDetails());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profileImageView);
        }
    }

    private void saveProfileDetails() {
        String editedName = nameEditText.getText().toString();
        String editedPhone = phoneEditText.getText().toString();
        String editedUsername = usernameEditText.getText().toString();
        String editedPassword = passwordEditText.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", editedName);
        editor.putString("phone", editedPhone);
        editor.putString("username", editedUsername);
        editor.putString("password", editedPassword);
        editor.apply();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        databaseReference.child("name").setValue(editedName);
        databaseReference.child("phone").setValue(editedPhone);
        databaseReference.child("username").setValue(editedUsername);
        databaseReference.child("password").setValue(editedPassword);

        if (imageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("images")
                    .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            editor.putString("imageurl", imageUrl);
                            editor.apply();
                            databaseReference.child("imageurl").setValue(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                    });
        }

        Intent intent = new Intent();
        intent.putExtra("name", editedName);
        intent.putExtra("phone", editedPhone);
        intent.putExtra("username", editedUsername);
        intent.putExtra("password", editedPassword);
        setResult(RESULT_OK, intent);
        finish();
    }

    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri);
    }
}
