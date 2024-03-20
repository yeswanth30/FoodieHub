package com.happymeals;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.happymeals.AddressAddedDialog;
import com.happymeals.Models.AddressModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EnterAddressActivity extends AppCompatActivity {

    private EditText editTextAddress, editTextCity, editTextCountry, editTextArea, editTextPincode,editTextphone;
    private TextView buttonSaveAddress;
    private DatabaseReference databaseReference;
    private ImageView back;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_address_activity);

        editTextAddress = findViewById(R.id.editTextAddress);
        editTextCity = findViewById(R.id.editTextCity);
        editTextCountry = findViewById(R.id.editTextCountry);
        editTextArea = findViewById(R.id.editTextArea);
        editTextPincode = findViewById(R.id.editTextPincode);
        buttonSaveAddress = findViewById(R.id.buttonSaveAddress);
        editTextphone = findViewById(R.id.editTextphone);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterAddressActivity.this, Address_Activity.class);
                startActivity(intent);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Address");

        buttonSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAddressToFirebase();
            }
        });
    }

    private void saveAddressToFirebase() {
        String address = editTextAddress.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();
        String area = editTextArea.getText().toString().trim();
        String pincode = editTextPincode.getText().toString().trim();
        String phone = editTextphone.getText().toString().trim();

        if (address.isEmpty() || city.isEmpty() || country.isEmpty() || area.isEmpty() || pincode.isEmpty() || phone.isEmpty()     ) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            // All fields are filled, proceed to save the address
            String userId = getCurrentUserId();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss MM-dd-yyyy", Locale.getDefault());
            String timestamp = sdf.format(new Date());

            String addressId = databaseReference.push().getKey();
            AddressModel userAddress = new AddressModel(addressId, userId, address, city, country, area, pincode,phone, timestamp);

            if (addressId != null) {
                databaseReference.child(addressId).setValue(userAddress);

                // Show dialog box indicating address added
                AddressAddedDialog dialog = new AddressAddedDialog(EnterAddressActivity.this);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // Navigate to the Address_Activity after dialog is dismissed
                        startActivity(new Intent(EnterAddressActivity.this, Address_Activity.class));
                        finish();
                    }
                });
                dialog.show();
            } else {
                Toast.makeText(this, "Failed to save address", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        return sharedPreferences.getString("userid", "");
    }
}