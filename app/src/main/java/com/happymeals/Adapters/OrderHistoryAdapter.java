package com.happymeals.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.Models.Dish;
import com.happymeals.Models.OrderHistorydish;
import com.happymeals.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder> {
    private Context context;
    private List<OrderHistorydish> orderHistories;
    private DatabaseReference cartRef;

    private SharedPreferences sharedPreferences;
    private String currentUserId;
    private DatabaseReference paymentsRef;



    public OrderHistoryAdapter(Context context, List<OrderHistorydish> orderHistories, DatabaseReference cartRef,DatabaseReference paymentsRef) {
        this.context = context;
        this.orderHistories = orderHistories;
        this.cartRef = cartRef;
        sharedPreferences = context.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("userid", "");
        this.paymentsRef = paymentsRef;

    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {
        OrderHistorydish orderHistory = orderHistories.get(position);
        holder.orderIdTextView.setText("Order ID: " + orderHistory.getUniqueid());

        String orderId = orderHistory.getOrderId();
        fetchTotalPrice(orderId, holder.totalPrice);



        String timestamp = orderHistory.getTimestamp();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        Date date;
        try {
            date = sdf.parse(timestamp);
            SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dateText = dateOnlyFormat.format(date);
            SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("HH:mm:ss");
            String timeText = timeOnlyFormat.format(date);

            holder.first_text_view.setText("Date:" + " "+dateText);
            holder.second_text_view.setText("Time:" + " "+timeText);
        } catch (ParseException e) {
            e.printStackTrace();
        }




        OrderAdapter orderAdapter = new OrderAdapter(context, orderHistory.getDishes());
        holder.dishesRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        holder.dishesRecyclerView.setAdapter(orderAdapter);

        holder.repeat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create AlertDialog to confirm repeating
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Repeat Order");
                builder.setMessage("Are you sure you want to repeat this order?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Repeat order
                        List<Dish> dishes = orderHistory.getDishes();
                        for (Dish dish : dishes) {
                            addDishToCart(context, dish, cartRef);
                        }
                        Toast.makeText(context, "All dishes added to cart", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing, just dismiss the dialog
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderHistories.size();
    }

    public static class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView,first_text_view,second_text_view,totalPrice;
        RecyclerView dishesRecyclerView;
        ImageView repeat_button;

        public OrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.order_id);
            dishesRecyclerView = itemView.findViewById(R.id.dishes_recycler_view);
            repeat_button = itemView.findViewById(R.id.repeat_button);
            first_text_view = itemView.findViewById(R.id.first_text_view);
            second_text_view = itemView.findViewById(R.id.second_text_view);
            totalPrice = itemView.findViewById(R.id.totalPrice);
        }
    }

    private void addDishToCart(Context context, Dish dish, DatabaseReference cartRef) {
        if (cartRef == null) {
            Log.e("AddToCart", "Cart reference is null");
            return;
        }

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean dishExists = false;
                String cartId = null;
                for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                    if (cartSnapshot.hasChild(dish.getDish_id())) {
                        dishExists = true;
                        cartId = cartSnapshot.getKey();
                        break;
                    }
                }

                if (dishExists && cartId != null) {
                    long quantity = dataSnapshot.child(cartId).child(dish.getDish_id()).child("quantity").getValue(Long.class);
                    cartRef.child(cartId).child(dish.getDish_id()).child("quantity").setValue(quantity + 1);
                } else {
                    String newCartId = cartRef.push().getKey();
                    cartRef.child(newCartId).child(dish.getDish_id()).setValue(dish);
                    cartRef.child(newCartId).child(dish.getDish_id()).child("quantity").setValue(dish.getQuantity());
                    cartRef.child(newCartId).child(dish.getDish_id()).child("userid").setValue(currentUserId);
                    cartRef.child(newCartId).child(dish.getDish_id()).child("cart_id").setValue(newCartId);
                    cartRef.child(newCartId).child(dish.getDish_id()).child("status").setValue(0);
                    cartRef.child(newCartId).child(dish.getDish_id()).child("cost").setValue(dish.getCost());
                }
                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }
    private void fetchTotalPrice(String orderId, TextView totalPriceTextView) {
        paymentsRef.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String totalPrice = snapshot.child("total_price").getValue(String.class);
                        if (!TextUtils.isEmpty(totalPrice)) {
                            totalPriceTextView.setText("Total Price: " + totalPrice);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }
}
