package com.happymeals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.Adapters.AddressAdapter;
import com.happymeals.Models.AddressModel;

import java.util.ArrayList;
import java.util.List;

public class Address_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private List<AddressModel> addressList;
    private DatabaseReference databaseReference;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        back = findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Address_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("Address");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(addressList, new AddressAdapter.OnRadioButtonClickListener() {
            @Override
            public void onRadioButtonClick(AddressModel addressModel) {
                setDefaultAddress(addressModel);
            }
        });
        recyclerView.setAdapter(addressAdapter);

        fetchAddresses();


        TextView buttonAddNewAddress = findViewById(R.id.saveButton);
        buttonAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Address_Activity.this, EnterAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchAddresses() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userid", "");

        databaseReference.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addressList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AddressModel addressModel = snapshot.getValue(AddressModel.class);
                    addressList.add(addressModel);
                }
                addressAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private void setDefaultAddress(AddressModel selectedAddress) {
        for (AddressModel address : addressList) {
            if (address.getAddressId().equals(selectedAddress.getAddressId())) {
                address.setDefaultAddress("yes");
            } else {
                address.setDefaultAddress("no");
            }
            databaseReference.child(address.getAddressId()).setValue(address);
        }
    }
}
