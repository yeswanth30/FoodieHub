package com.happymeals.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.LoginActivity;
import com.happymeals.Models.Dish_model;
import com.happymeals.MoreDetailsActivity;
import com.happymeals.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CustomFoodAdapter extends RecyclerView.Adapter<CustomFoodAdapter.ViewHolder> {
    private List<Dish_model> dishes;
    private Context context;
    private DatabaseReference likesRef;
    private DatabaseReference cartRef; // Reference to the cart node
    private SharedPreferences sharedPreferences;
    private String currentUserId;

    public CustomFoodAdapter(Context context, List<Dish_model> dishes) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wrapper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Dish_model dish = dishes.get(position);

        Picasso.get().load(dish.getImageurl()).into(holder.imageView);
        holder.restoname.setText(dish.getDish_name());
//        holder.resto_type.setText(dish.getCategory());
        holder.price.setText(dish.getCost());




        holder.total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MoreDetailsActivity.class);
                intent.putExtra("dish_id", dish.getDish_id());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView restoname, resto_type, price;
        RelativeLayout total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            restoname = itemView.findViewById(R.id.textView1);
            price = itemView.findViewById(R.id.textView2);
            total = itemView.findViewById(R.id.itemView);

        }
    }



    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userid", "");
    }




}
