package com.happymeals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.happymeals.Models.SigninModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.happymeals.LoginActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class SigninActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword, signupphone;
    TextView loginRedirectText,signupButton,nameErrorText,emailErrorText,usernameErrorText,passwordErrorText,phoneErrorText,emailErrorText12;
    ImageView imageView, uploadimage;
    Uri filePath;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 22;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);

        signupName = findViewById(R.id.name);
        signupEmail = findViewById(R.id.email);
        signupUsername = findViewById(R.id.username);
        signupPassword = findViewById(R.id.password);
        signupphone = findViewById(R.id.phone);
        signupButton = findViewById(R.id.signupbutton);
        nameErrorText = findViewById(R.id.nameErrorText);
        emailErrorText = findViewById(R.id.emailErrorText);
        passwordErrorText=findViewById(R.id.passwordErrorText);
        phoneErrorText=findViewById(R.id.phoneErrorText);
        emailErrorText12=findViewById(R.id.emailErrorText);
        usernameErrorText=findViewById(R.id.usernameErrorText);
        loginRedirectText=findViewById(R.id.redirect);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");




        imageView = findViewById(R.id.imgView);
        uploadimage = findViewById(R.id.uploadimage);
        ImageView togglePassword = findViewById(R.id.togglePassword);



        togglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputType = (signupPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) ?
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD :
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;

                signupPassword.setInputType(inputType);
                signupPassword.setSelection(signupPassword.getText().length());

                togglePassword.setImageResource(
                        (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) ?
                                R.drawable.visible :
                                R.drawable.hide
                );
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // btnUpload.setVisibility(View.GONE);

        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectImage();
            }
        });



        signupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    signupName.setBackgroundResource(R.drawable.rectangle_signup);
                    nameErrorText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        signupEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    signupEmail.setBackgroundResource(R.drawable.rectangle_signup);
                    emailErrorText.setVisibility(View.GONE);
                    emailErrorText12.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        signupUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    signupUsername.setBackgroundResource(R.drawable.rectangle_signup);
                    usernameErrorText.setVisibility(View.GONE);
                    usernameErrorText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        signupPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    signupPassword.setBackgroundResource(R.drawable.rectangle_signup);
                    passwordErrorText.setVisibility(View.GONE);
                    passwordErrorText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        signupphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    signupphone.setBackgroundResource(R.drawable.rectangle_signup);
                    phoneErrorText.setVisibility(View.GONE);
                    phoneErrorText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean[] hasError = {false};

                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();
                String phone = signupphone.getText().toString();

                hasError[0] = false;

                if (name.isEmpty()) {
                    signupName.setBackgroundResource(R.drawable.edittext_border_red);
                    nameErrorText.setVisibility(View.VISIBLE);
                    nameErrorText.setText("Please enter your name");
                    hasError[0] = true;
                } else {
                    signupName.setBackgroundResource(R.drawable.rectangle_signup);
                    nameErrorText.setVisibility(View.GONE);
                }

                if (email.isEmpty()) {
                    signupEmail.setBackgroundResource(R.drawable.edittext_border_red);
                    emailErrorText.setVisibility(View.VISIBLE);
                    emailErrorText.setText("Please enter your email");
                    hasError[0] = true;
                } else if (!isValidEmail(email)) {
                    signupEmail.setBackgroundResource(R.drawable.edittext_border_red);
                    emailErrorText.setVisibility(View.VISIBLE);
                    emailErrorText.setText("Invalid email address. Please use @gmail.com");
                    hasError[0] = true;
                } else {
                    signupEmail.setBackgroundResource(R.drawable.rectangle_signup);
                    emailErrorText.setVisibility(View.GONE);
                }

                if (username.isEmpty()) {
                    signupUsername.setBackgroundResource(R.drawable.edittext_border_red);
                    usernameErrorText.setVisibility(View.VISIBLE);
                    usernameErrorText.setText("Please enter your username");
                    hasError[0] = true;
                } else {
                    signupUsername.setBackgroundResource(R.drawable.rectangle_signup);
                    usernameErrorText.setVisibility(View.GONE);
                }

                if (password.isEmpty()) {
                    signupPassword.setBackgroundResource(R.drawable.edittext_border_red);
                    passwordErrorText.setVisibility(View.VISIBLE);
                    passwordErrorText.setText("Please enter your password");
                    hasError[0] = true;
                } else {
                    signupPassword.setBackgroundResource(R.drawable.rectangle_signup);
                    passwordErrorText.setVisibility(View.GONE);
                }

                if (phone.isEmpty()) {
                    signupphone.setBackgroundResource(R.drawable.edittext_border_red);
                    phoneErrorText.setVisibility(View.VISIBLE);
                    phoneErrorText.setText("Please enter your phone number");
                    hasError[0] = true;
                } else {
                    signupphone.setBackgroundResource(R.drawable.rectangle_signup);
                    phoneErrorText.setVisibility(View.GONE);
                }

                if (hasError[0]) {
                    return;
                }

                if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                    return;
                }

                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            emailErrorText.setVisibility(View.VISIBLE);
                            emailErrorText.setText("Email already taken");
                            hasError[0] = true;
                        } else {
                            emailErrorText.setVisibility(View.GONE);
                            if (!hasError[0]) {
                                uploadImage(username);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });



        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isValidEmail(String email) {
        return email.endsWith("@gmail.com");
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.length() <= 10;
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadimage.setImageBitmap(bitmap);
                uploadimage.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String username) {

        if (filePath == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                String imageurl = downloadUri.toString();
                                saveDataToDatabase(username, imageurl);
                                Toast.makeText(SigninActivity.this, "Details successfully stored in Firebase", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SigninActivity.this, "Failed to store details in Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void saveDataToDatabase(String username, String imageUrl) {
        String name = signupName.getText().toString();
        String email = signupEmail.getText().toString();
        String password = signupPassword.getText().toString();
        String phone = signupphone.getText().toString();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        DatabaseReference userRef = reference.push();
        String userId = userRef.getKey();

        SigninModel helperClass = new SigninModel(name, email, username, password, phone, imageUrl, timestamp, userId);
        userRef.setValue(helperClass);

        signupName.setText("");
        signupEmail.setText("");
        signupUsername.setText("");
        signupPassword.setText("");
        signupphone.setText("");

        Intent intent = new Intent(SigninActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
