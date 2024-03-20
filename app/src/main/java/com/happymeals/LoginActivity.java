package com.happymeals;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText Emailaddress, loginpassword;
    TextView loginbutton, registertext, errorText,rightTextView637,errorText2;
    SharedPreferences sharedPreferences;
    String passwordFromDB;

    TextView googlebutton,applebutton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        Emailaddress = findViewById(R.id.Emailaddress);
        loginpassword = findViewById(R.id.password);
        loginbutton = findViewById(R.id.loginbutton);
        registertext = findViewById(R.id.newuser);
        errorText = findViewById(R.id.errorText);
        errorText2 = findViewById(R.id.errorText2);

        ImageView togglePassword = findViewById(R.id.togglePassword);

        Emailaddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    Emailaddress.setBackgroundResource(R.drawable.rectangle_signup);
                    errorText.setVisibility(View.GONE);
                    errorText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        loginpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    loginpassword.setBackgroundResource(R.drawable.rectangle_signup);
                    errorText2.setVisibility(View.GONE);
                    errorText2.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

//        rightTextView637.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//                startActivity(intent);
//            }
//        });

        togglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputType = (loginpassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) ?
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD :
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;

                loginpassword.setInputType(inputType);
                loginpassword.setSelection(loginpassword.getText().length());

                togglePassword.setImageResource(
                        (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) ?
                                R.drawable.visible :
                                R.drawable.hide
                );
            }
        });

        registertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail() | !validatePassword()) {

                } else {
                    checkUser();
                }
            }
        });
    }


    public Boolean validateEmail() {
        String val = Emailaddress.getText().toString().trim();

        if (val.isEmpty()) {
            errorText.setText("Email cannot be empty");
            errorText.setVisibility(View.VISIBLE);
            Emailaddress.setBackgroundResource(R.drawable.edittext_border_red);

            return false;
        } else if (!val.endsWith("@gmail.com")) {
            errorText.setText("Please enter a valid email address ");
            errorText.setVisibility(View.VISIBLE);
            Emailaddress.setBackgroundResource(R.drawable.edittext_border_red);
            return false;
        } else {
            errorText.setVisibility(View.GONE);
            Emailaddress.setBackgroundResource(R.drawable.edit_text_border);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginpassword.getText().toString();
        if (val.isEmpty()) {
            errorText2.setText("Password cannot be empty");
            errorText2.setVisibility(View.VISIBLE);
            loginpassword.setBackgroundResource(R.drawable.edittext_border_red);
            return false;
        } else {
            errorText2.setVisibility(View.GONE);
            loginpassword.setBackgroundResource(R.drawable.edit_text_border);
            return true;
        }
    }

    public void checkUser() {
        String userEmail = Emailaddress.getText().toString().trim();
        String userPassword = loginpassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean emailFound = false;
                boolean passwordMatched = false;
                boolean isDeactivated = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String emailFromDB = userSnapshot.child("email").getValue(String.class);
                    String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                    int statusFromDB = userSnapshot.child("status").getValue(Integer.class);

                    if (emailFromDB != null && emailFromDB.equals(userEmail)) {
                        emailFound = true;

                        if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                            passwordMatched = true;

                            if (statusFromDB == 0) {
                                // User is active
                                String nameFromDB = userSnapshot.child("name").getValue(String.class);
                                String userFromDB = userSnapshot.child("username").getValue(String.class);
                                String phnonefromdb = userSnapshot.child("phone").getValue(String.class);

                                String imageFromDB = userSnapshot.child("imageurl").getValue(String.class);
                                String useridFromDB = userSnapshot.getKey(); // Get the user's unique ID
                                String timestampFromDB = userSnapshot.child("timestamp").getValue(String.class);

                                SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("name", nameFromDB);
                                editor.putString("email", userEmail);
                                editor.putString("password", userPassword);
                                editor.putString("phone", phnonefromdb);
                                editor.putString("username", userFromDB);
                                editor.putString("imageurl", imageFromDB);
                                editor.putString("userid", useridFromDB);
                                editor.putString("timestamp", timestampFromDB);
                                editor.apply();

                                Emailaddress.setText("");
                                loginpassword.setText("");

                                Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("fullname", nameFromDB);
                                intent.putExtra("username", userFromDB);
                                startActivity(intent);

                                SharedPreferences loginSharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                                SharedPreferences.Editor loginEditor = loginSharedPreferences.edit();
                                loginEditor.putBoolean("isLoggedIn", true);
                                loginEditor.apply();

                                break;
                            } else {
                                // User is deactivated
                                isDeactivated = true;
                            }
                        }
                    }
                }

                if (!emailFound) {
                    errorText.setText("Email doesn't exist");
                    errorText.setVisibility(View.VISIBLE);
                    Emailaddress.setBackgroundResource(R.drawable.edittext_border_red);
                    loginpassword.setBackgroundResource(R.drawable.edit_text_border);
                } else if (!passwordMatched) {
                    errorText2.setText("Incorrect Password");
                    errorText2.setVisibility(View.VISIBLE);
                    loginpassword.setBackgroundResource(R.drawable.edittext_border_red);
                    Emailaddress.setBackgroundResource(R.drawable.edit_text_border);
                } else if (isDeactivated) {
                    Toast.makeText(LoginActivity.this, "Your account is deactivated By Admin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LoginActivity", "Database Error: " + error.getMessage());
            }
        });
    }

}