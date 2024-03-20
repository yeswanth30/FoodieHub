package com.happymeals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.Adapters.Food_adaptor;
import com.happymeals.Models.Dish_model;
import com.happymeals.Models.Image_model;

import java.util.ArrayList;
import java.util.List;

public class Food_items_activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Food_adaptor adapter;
    private List<Dish_model> dishList;
    private List<Dish_model> originalDishList; // Original list to maintain all dishes
    private DatabaseReference dishesReference;
    private DatabaseReference imagesReference;
    private String restaurantId;
    private String userid;
    private ImageView back;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_items_activty);

        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Food_items_activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dishList = new ArrayList<>();
        originalDishList = new ArrayList<>();
        adapter = new Food_adaptor(this, dishList);
        recyclerView.setAdapter(adapter);

        searchEditText = findViewById(R.id.searchtext123);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchDishes(s.toString().toLowerCase());
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        userid = sharedPreferences.getString("userid", "");

        restaurantId = getIntent().getStringExtra("restoid");
        if (restaurantId != null && !restaurantId.isEmpty()) {
            fetchDishes(restaurantId);
        }
    }

    private void fetchDishes(String restaurantId) {
        dishesReference = FirebaseDatabase.getInstance().getReference("dishes");
        dishesReference.orderByChild("restaurant_id").equalTo(restaurantId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dishList.clear();
                        originalDishList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Dish_model dish = snapshot.getValue(Dish_model.class);
                            if (dish != null) {
                                Boolean isLiked = snapshot.child("likedUsers").child(userid).exists();
                                dish.setLikedByCurrentUser(isLiked);
                                fetchImageForDish(dish);
                                originalDishList.add(dish); // Add to the original list
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
    }

    private void fetchImageForDish(Dish_model dish) {
        imagesReference = FirebaseDatabase.getInstance().getReference("dishes_images");
        imagesReference.orderByChild("dish_id").equalTo(dish.getDish_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Image_model image = snapshot.getValue(Image_model.class);
                                if (image != null) {
                                    dish.setImageurl(image.getImageurl());
                                }
                            }
                        }
                        dishList.add(dish);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
    }

    private void searchDishes(String searchText) {
        dishList.clear();
        for (Dish_model dish : originalDishList) {
            if (dish.getDish_name().toLowerCase().contains(searchText)) {
                dishList.add(dish);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
