package com.happymeals;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.happymeals.Adapters.OrderHistoryAdapter;
import com.happymeals.Models.Cart_Model;
import com.happymeals.Models.Dish;

import com.happymeals.Models.OrderHistorydish;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class orderhistory_Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderHistoryAdapter orderHistoryAdapter;
    private List<OrderHistorydish> orderHistories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderhistory_activity);

        DatabaseReference paymentsRef = FirebaseDatabase.getInstance().getReference().child("payments");



        recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new SingleItemLinearLayoutManager(this));


        orderHistories = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(this, orderHistories, FirebaseDatabase.getInstance().getReference().child("cart"), paymentsRef);
        recyclerView.setAdapter(orderHistoryAdapter);

        fetchOrderHistories();
    }

    private void fetchOrderHistories() {
        String userId = getSharedPreferences("user_details", MODE_PRIVATE).getString("userid", "");

        Query query = FirebaseDatabase.getInstance().getReference().child("orders");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<OrderHistorydish> orderHistories = new ArrayList<>(); // Create a temporary list

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    String orderId = orderSnapshot.getKey();
                    String orderUserId = orderSnapshot.child("userId").getValue(String.class);
                    String uniqueid = orderSnapshot.child("uniqueid").getValue(String.class);
                    int orderStatus = orderSnapshot.child("status").getValue(Integer.class);
                    String timestamp = orderSnapshot.child("timestamp").getValue(String.class);




                    // Check if the order belongs to the current user and has status 1
                    if (orderUserId != null && orderUserId.equals(userId) && orderStatus == 1) {
                        List<Dish> dishes = new ArrayList<>();
                        for (DataSnapshot dishSnapshot : orderSnapshot.child("dishes").getChildren()) {
                            String cartid = dishSnapshot.child("dish_name").getValue(String.class);
                            String image = dishSnapshot.child("imageurl").getValue(String.class);
                            long quantity = dishSnapshot.child("quantity").getValue(long.class);
                            String dishid = dishSnapshot.child("dish_id").getValue(String.class);
                            String price = dishSnapshot.child("cost").getValue(String.class);

                            Dish cartModel = new Dish();
                            cartModel.setDish_name(cartid);
                            cartModel.setQuantity(quantity);
                            cartModel.setImageurl(image);
                            cartModel.setDish_id(dishid);
                            cartModel.setCost(price);

                            dishes.add(cartModel);
                        }
                        orderHistories.add(new OrderHistorydish(orderId, dishes,uniqueid,timestamp));
                    }
                }

                // Reverse the order of the list to display the latest orders at the top
                Collections.reverse(orderHistories);

                // Clear the existing list and add the fetched order histories
                // This assumes that orderHistories is a class-level variable
                orderhistory_Activity.this.orderHistories.clear();
                orderhistory_Activity.this.orderHistories.addAll(orderHistories);

                // Notify the adapter of the changes
                orderHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

}
