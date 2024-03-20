package com.happymeals.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.LoginActivity;
import com.happymeals.MainActivity;
import com.happymeals.Models.Dish_model;
import com.happymeals.MoreDetailsActivity;
import com.happymeals.R;
import com.squareup.picasso.Picasso;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NewFoodAdapter extends RecyclerView.Adapter<NewFoodAdapter.ViewHolder> {
    private List<Dish_model> dishes;
    private Context context;
    private DatabaseReference likesRef;
    private DatabaseReference cartRef;
    private SharedPreferences sharedPreferences;
    private String currentUserId;

    public NewFoodAdapter(Context context, List<Dish_model> dishes) {
        this.context = context;
        this.dishes = dishes;
        likesRef = FirebaseDatabase.getInstance().getReference().child("likedUsers");
        cartRef = FirebaseDatabase.getInstance().getReference().child("cart");
        sharedPreferences = context.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("userid", "");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food12, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Dish_model dish = dishes.get(position);

        Picasso.get().load(dish.getImageurl()).into(holder.imageView);
        holder.restoname.setText(dish.getDish_name());
        holder.resto_type.setText(dish.getCategory());
        holder.price.setText(dish.getCost());

//        boolean isLikedByCurrentUser = dish.isLikedByCurrentUser();
//        if (isLikedByCurrentUser) {
//            holder.like.setImageResource(R.drawable.fullhear);
//        } else {
//            holder.like.setImageResource(R.drawable.plainhear);
//        }
//
//        holder.like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isLoggedIn()) {
//                    boolean isLiked = dish.isLikedByCurrentUser();
//                    if (isLiked) {
//                        holder.like.setImageResource(R.drawable.plainhear);
//                        updateLikeStatus(dish.getDish_id(), false);
//                    } else {
//                        holder.like.setImageResource(R.drawable.fullhear);
//                        updateLikeStatus(dish.getDish_id(), true);
//                    }
//                    dish.setLikedByCurrentUser(!isLiked);
//                } else {
//                    showLoginDialog();
//                }
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click
                Intent intent = new Intent(context, MoreDetailsActivity.class);
                intent.putExtra("dish_id", dish.getDish_id());
                intent.putExtra("image_url", dish.getImageurl());
                context.startActivity(intent);
            }
        });

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long status = dish.getStatus();

                if (status == 0) {
                    showAddToCartDialog(dish, 0); // Status 0 for success
                    addDishToCart(dish);
                } else if (status == 1) {
                    showDisabledByOwnerDialog();
                } else if (status == 2) {
                    showOutOfStockDialog();
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, like;
        TextView restoname, resto_type, price;
        TextView add;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.leftIcon);
            resto_type = itemView.findViewById(R.id.resto_type);
            restoname = itemView.findViewById(R.id.productname);
            price = itemView.findViewById(R.id.price);
           // like = itemView.findViewById(R.id.like);
            add = itemView.findViewById(R.id.add);
        }
    }

    private void updateLikeStatus(String dishId, boolean isLiked) {
        DatabaseReference dishRef = FirebaseDatabase.getInstance().getReference().child("dishes").child(dishId);
        String userId = getCurrentUserId();

        if (isLiked) {
            dishRef.child("likedUsers").child(userId).setValue(true);
        } else {
            dishRef.child("likedUsers").child(userId).removeValue();
        }

        for (Dish_model dish : dishes) {
            if (dish.getDish_id().equals(dishId)) {
                dish.setLikedByCurrentUser(isLiked);
                notifyDataSetChanged();
                Log.d("NewFoodAdapter", "Dish: " + dish.getDish_name() + ", Liked by current user: " + isLiked);
                return;
            }
        }

        Log.e("NewFoodAdapter", "Dish not found with ID: " + dishId);
    }

    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userid", "");
    }

    private void addDishToCart(Dish_model dish) {
        DatabaseReference dishRef = FirebaseDatabase.getInstance().getReference().child("dishes").child(dish.getDish_id());

        dishRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long status = dataSnapshot.child("status").getValue(Long.class);
                    switch ((int) status) {
                        case 0:
                            addToCart(dish);
                            break;
                        case 1:
                            Toast.makeText(context, "This dish is currently disabled by the owner", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(context, "This dish is currently out of stock", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                   Toast.makeText(context, "Failed to add dish to cart: Dish not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void addToCart(Dish_model dish) {
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
                    cartRef.child(newCartId).child(dish.getDish_id()).child("quantity").setValue(1);
                    cartRef.child(newCartId).child(dish.getDish_id()).child("userid").setValue(currentUserId);
                    cartRef.child(newCartId).child(dish.getDish_id()).child("cart_id").setValue(newCartId);
                }
               // Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Please log in to perform this action")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



    private void showAddToCartDialog(Dish_model dish, int status) {
        Log.d("MyApp", "showAddToCartDialog called");

        View customView = null;
        String title = "";
        String contentText = "";

        switch (status) {
            case 0:
                customView = LayoutInflater.from(context).inflate(R.layout.custom_success_dialog, null);
                title = "Success";
                contentText = "Item added to cart";
                break;
            case 1:
                customView = LayoutInflater.from(context).inflate(R.layout.custom_error_dialog, null);
                title = "Error";
                contentText = "This dish is currently disabled by the owner";
                break;
            case 2:
                customView = LayoutInflater.from(context).inflate(R.layout.custom_error_dialog1, null);
                title = "Error";
                contentText = "This dish is currently out of stock";
                break;
            default:
                break;
        }
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setCustomView(customView);
        sweetAlertDialog.setTitleText(title)
                .setContentText(contentText)
                .setConfirmButtonBackgroundColor(android.R.color.transparent)
                .setConfirmText("")
                .showCancelButton(false);

        android.widget.Button confirmButton = sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM);
        if (confirmButton != null) {
            confirmButton.setTextColor(ContextCompat.getColor(context, R.color.pink));
        }
        sweetAlertDialog.show();

        TextView customTextView = customView.findViewById(R.id.customTextView);

        customTextView.setText("OK");

        customTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });




    }
    private void showDisabledByOwnerDialog() {
        View customView = LayoutInflater.from(context).inflate(R.layout.custom_error_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setTextColor(Color.parseColor("#F15B5D"));
        }
    }


    private void showOutOfStockDialog() {
        View customView = LayoutInflater.from(context).inflate(R.layout.custom_error_dialog1, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setTextColor(Color.parseColor("#F15B5D"));
        }
    }


}
