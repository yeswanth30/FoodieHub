package com.happymeals.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.Food_items_activity;
import com.happymeals.Models.RestaurantModel;
import com.happymeals.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class All_resto_adaptor extends RecyclerView.Adapter<All_resto_adaptor.ImageViewHolder> {
    private Context context;
    private List<RestaurantModel> userList;

    public All_resto_adaptor(Context context, List<RestaurantModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resturants, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        RestaurantModel restaurantModel = userList.get(position);
        String imageUrl = restaurantModel.getImageurl();
        Picasso.get().load(imageUrl).into(holder.imageView);
        holder.restoname.setText(restaurantModel.getRestaurant_name());
        holder.resto_type.setText(restaurantModel.getType());
        holder.time.setText(restaurantModel.getDelivery_time());
        holder.relative1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Food_items_activity.class);
                intent.putExtra("restoid", restaurantModel.getRestaurant_id()); // Pass the restaurant ID
                context.startActivity(intent);
            }
        });

        // Fetch food types
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("restaurant").child(restaurantModel.getRestaurant_id()).child("food_types");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> foodItems = new ArrayList<>();
                for (DataSnapshot foodItemSnapshot : dataSnapshot.getChildren()) {
                    String foodItem = foodItemSnapshot.getValue(String.class);
                    foodItems.add(foodItem);
                }

                StringBuilder foodItemsText = new StringBuilder();
                for (String foodItem : foodItems) {
                    foodItemsText.append(foodItem).append(", ");
                }
                // Remove the last comma and space
                if (foodItemsText.length() > 2) {
                    foodItemsText.setLength(foodItemsText.length() - 2);
                }
                holder.food_type.setText(foodItemsText.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView restoname, resto_type, time,food_type;
        RelativeLayout relative1;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.resto_image);
            resto_type = itemView.findViewById(R.id.resto_type);
            restoname = itemView.findViewById(R.id.resto_name);
            time = itemView.findViewById(R.id.time);
            food_type = itemView.findViewById(R.id.food_type);
            relative1 = itemView.findViewById(R.id.relative1);
        }
    }
}
