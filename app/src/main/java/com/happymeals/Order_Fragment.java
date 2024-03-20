package com.happymeals;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.happymeals.Adapters.Cart_adapter;
import com.happymeals.Models.Cart_Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Order_Fragment extends Fragment {
    TextView checkoutbutton, fulltotal;
    RecyclerView recyclerView;
    List<Cart_Model> cartModels = new ArrayList<>();
    Cart_adapter adapter;
    String userid, username;
    int total;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new SingleItemLinearLayoutManager(getContext()));

        fulltotal = view.findViewById(R.id.fulltotal);

        loadCartItems(); // Load cart items initially

        checkoutbutton = view.findViewById(R.id.checkoutbutton);
        checkoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });
    }

    private void checkout() {
        if (cartModels.isEmpty()) {
            // Cart is empty, show custom dialog
            showEmptyCartDialog();
        }else {
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("orders").push();
            String timestamp = getCurrentTimestamp();
            String orderId = ordersRef.getKey();

            String uniqueorderid = generateorderid();

            ordersRef.child("userId").setValue(userid);

            ordersRef.child("timestamp").setValue(timestamp);
            ordersRef.child("orderId").setValue(orderId);
            ordersRef.child("status").setValue(0);
            ordersRef.child("uniqueid").setValue(uniqueorderid);

            for (Cart_Model cartModel : cartModels) {
                String dishId = cartModel.getDish_id();
                long quantity = cartModel.getQuantity();

                String price = cartModel.getCost();
                String name = cartModel.getDish_name();
                String image = cartModel.getImageurl();
                String cartId = cartModel.getCartId();
                String restid = cartModel.getRestaurant_id();
                String stamp = getCurrentTimestamp();

                Map<String, Object> dishDetails = new HashMap<>();
                dishDetails.put("dish_id", dishId);
                dishDetails.put("quantity", quantity);
                dishDetails.put("cost", price);
                dishDetails.put("dish_name", name);
                dishDetails.put("imageurl", image);
                dishDetails.put("cartId", cartId);
                dishDetails.put("restaurant_id", restid);
                dishDetails.put("timestamp1", stamp);
                dishDetails.put("userid", userid);
                dishDetails.put("username", username);

                ordersRef.child("dishes").child(dishId).setValue(dishDetails);
            }

            cartModels.clear();
            adapter.notifyDataSetChanged();
            clearCart();

            Intent intent = new Intent(getContext(), Checkout_Activity.class);
            Bundle bundle = new Bundle();
            bundle.putDouble("totalPrice", total);
            bundle.putString("orderId", orderId);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    private void clearCart() {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("cart");
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot dishSnapshot : cartSnapshot.getChildren()) {
                        String userId = dishSnapshot.child("userid").getValue(String.class);

                        if (userId != null && userId.equals(userid)) {
                            dishSnapshot.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void loadCartItems() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_details", MODE_PRIVATE);
        userid = sharedPreferences.getString("userid", "");
        username = sharedPreferences.getString("name", "");

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart");
        adapter = new Cart_adapter(getContext(), cartModels);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total = 0;
                cartModels.clear();
                for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot dishSnapshot : cartSnapshot.getChildren()) {
                        String userId = dishSnapshot.child("userid").getValue(String.class);
                        Integer statusInteger = dishSnapshot.child("status").getValue(Integer.class);

                        if (userId != null && userId.equals(userid)) {
                            String dish_id = dishSnapshot.child("dish_id").getValue(String.class);
                            String imageurl = dishSnapshot.child("imageurl").getValue(String.class);
                            String cost = dishSnapshot.child("cost").getValue(String.class);
                            long quantity = dishSnapshot.child("quantity").getValue(long.class);
                            String owner_id = dishSnapshot.child("owner_id").getValue(String.class);
                            String dish_name = dishSnapshot.child("dish_name").getValue(String.class);
                            String dishtype = dishSnapshot.child("dishtype").getValue(String.class);
                            String restaurantid = dishSnapshot.child("restaurant_id").getValue(String.class);
                            String cartid = dishSnapshot.child("cart_id").getValue(String.class);

                            int costing = parseCost(cost);
                            total += costing * (int) quantity;

                            Cart_Model cartModel = new Cart_Model(dish_id, imageurl, cost, quantity, owner_id, dish_name, dishtype, restaurantid, cartid);
                            cartModels.add(cartModel);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                fulltotal.setText("Rs. " + String.valueOf(total + 1));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private int parseCost(String costString) {
        if (costString == null) {
            return 0;
        }
        String numericString = costString.replaceAll("[^\\d]", "");
        return Integer.parseInt(numericString);
    }


    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.refreshAdapterData();
        }
    }

    private void showEmptyCartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(R.layout.dialog_empty_cart);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();

        // Set text color of the positive button
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                if (positiveButton != null) {
                    positiveButton.setTextColor(Color.parseColor("#F15B5D"));
                }
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.SlideInDiagonalDialogAnimation;

        dialog.show();
    }

    private String generateorderid() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 7; i++) {
            char c = characters.charAt(random.nextInt(characters.length()));
            sb.append(c);
        }
        return "#" + sb.toString();
    }


}
