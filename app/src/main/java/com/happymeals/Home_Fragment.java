package com.happymeals;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.Adapters.All_resto_adaptor;
import com.happymeals.Adapters.Sub_cat_Adapter;
import com.happymeals.Models.AddressModel;
import com.happymeals.Models.RestaurantModel;
import com.happymeals.Models.Sub_cat_model;

import java.util.ArrayList;
import java.util.List;

public class Home_Fragment extends Fragment {

    private RecyclerView recyclerView, ImagesrecyclerView;
    private All_resto_adaptor adapter;
    private Sub_cat_Adapter subCatAdapter;
    private List<Sub_cat_model> sublist;
    private List<RestaurantModel> userList;
    private DatabaseReference databaseReference;
    private EditText searchEditText;
    TextView locationtextview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        locationtextview = view.findViewById(R.id.locationtextview);
        recyclerView = view.findViewById(R.id.recycler_view);
        ImagesrecyclerView = view.findViewById(R.id.ImagesrecyclerView);
        recyclerView.setHasFixedSize(true);
       // recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new SingleItemLinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        adapter = new All_resto_adaptor(getContext(), userList);
        recyclerView.setAdapter(adapter);

        ImagesrecyclerView.setHasFixedSize(true);
        ImagesrecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        sublist = new ArrayList<>();
        subCatAdapter = new Sub_cat_Adapter(getContext(), sublist);
        ImagesrecyclerView.setAdapter(subCatAdapter);

        searchEditText = view.findViewById(R.id.searchtext123);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                retrieveRestaurants(s.toString().toLowerCase());
            }
        });

        retrieveRestaurants("");

        fetchDefaultAddressForCurrentUser();


        return view;
    }

    private void retrieveRestaurants(final String searchText) {
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurant");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RestaurantModel restaurant = snapshot.getValue(RestaurantModel.class);
                    if (restaurant != null && (searchText.isEmpty() || restaurant.getRestaurant_name().toLowerCase().contains(searchText) || restaurant.getType().toLowerCase().contains(searchText))) {
                        userList.add(restaurant);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

        DatabaseReference subcategoryRef = FirebaseDatabase.getInstance().getReference("dishcategory");
        subcategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sublist.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Sub_cat_model subCategory = snapshot.getValue(Sub_cat_model.class);
                    // Filter subcategories if needed based on some condition
                    if (subCategory != null) {
                        sublist.add(subCategory);
                    }
                }
                subCatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }


    private void fetchDefaultAddressForCurrentUser() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_details", MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userid", "");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Address");

        databaseReference.orderByChild("userId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AddressModel addressModel = snapshot.getValue(AddressModel.class);
                    if (addressModel != null && addressModel.getDefaultAddress() != null && addressModel.getDefaultAddress().equals("yes")) {
                        String addressDetails =  addressModel.getAddress() + "," +
                                addressModel.getArea() + "\n" +
                                addressModel.getCity() + "," +
                                addressModel.getCountry() + "," +
                                addressModel.getPincode();
                        locationtextview.setText(addressDetails);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

}
