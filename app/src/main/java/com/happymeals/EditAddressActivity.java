package com.happymeals;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.happymeals.Models.AddressModel;

public class EditAddressActivity extends AppCompatActivity {

    private EditText editTextAddress, editTextCity, editTextCountry, editTextPincode, editTextArea,edittextphone;

    ImageView back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        editTextAddress = findViewById(R.id.editTextAddress);
        editTextCity = findViewById(R.id.editTextCity);
        editTextCountry = findViewById(R.id.editTextCountry);
        editTextPincode = findViewById(R.id.editTextPincode);
        editTextArea = findViewById(R.id.editTextArea);
        edittextphone = findViewById(R.id.edittextphone);

        back = findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAddressActivity.this, Address_Activity.class);
                startActivity(intent);
            }
        });

        AddressModel addressModel = getIntent().getParcelableExtra("addressModel");

        editTextAddress.setText(addressModel.getAddress());
        editTextCity.setText(addressModel.getCity());
        editTextCountry.setText(addressModel.getCountry());
        editTextPincode.setText(addressModel.getPincode());
        editTextArea.setText(addressModel.getArea());
        edittextphone.setText(addressModel.getPhone());

        TextView buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Address").child(addressModel.getAddressId());
                databaseReference.child("address").setValue(editTextAddress.getText().toString());
                databaseReference.child("city").setValue(editTextCity.getText().toString());
                databaseReference.child("country").setValue(editTextCountry.getText().toString());
                databaseReference.child("pincode").setValue(editTextPincode.getText().toString());
                databaseReference.child("area").setValue(editTextArea.getText().toString());
                databaseReference.child("phone").setValue(edittextphone.getText().toString());

                finish();
            }
        });
    }
}
