package com.happymeals;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.happymeals.Adapters.NewFoodAdapter;
import com.happymeals.Models.Dish_model;
import com.happymeals.Models.Image_model;

import java.util.ArrayList;
import java.util.List;

public class Subcat_activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewFoodAdapter adapter;
    private List<Dish_model> dishList;
    private DatabaseReference dishesReference;
    private DatabaseReference imagesReference;
    private String restaurantId;

    ImageView back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subcat_activity);

        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dishList = new ArrayList<>();
        adapter = new NewFoodAdapter(this, dishList);
        recyclerView.setAdapter(adapter);

        restaurantId = getIntent().getStringExtra("dishcatid");

        if (restaurantId != null && !restaurantId.isEmpty()) {
            fetchDishes(restaurantId);
        }
    }

    private void fetchDishes(String restaurantId) {
        dishesReference = FirebaseDatabase.getInstance().getReference("dishes");
        dishesReference.orderByChild("dish_category_id").equalTo(restaurantId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dishList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Dish_model dish = snapshot.getValue(Dish_model.class);
                            if (dish != null) {
                                fetchImageForDish(dish);
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
}
