package com.happymeals.Adapters;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.happymeals.Models.Cart_Model;
import com.happymeals.MoreDetailsActivity;
import com.happymeals.Order_Fragment;
import com.happymeals.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Cart_adapter extends RecyclerView.Adapter<Cart_adapter.ViewHolder> {
    private List<Cart_Model> product;
    private Context context;
    private DatabaseReference cartRef;

    public Cart_adapter(Context context, List<Cart_Model> product) {
        this.context = context;
        this.product = product;
        cartRef = FirebaseDatabase.getInstance().getReference().child("cart");
    }

    public void refreshAdapterData() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (product.isEmpty()) {
            showAddItemsDialog();
            return;
        }

        Cart_Model destination = product.get(position);
        Picasso.get().load(destination.getImageurl()).into(holder.imageView);
        holder.name.setText(destination.getDish_name());
        holder.price.setText(destination.getCost());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click
                Intent intent = new Intent(context, MoreDetailsActivity.class);
                intent.putExtra("dish_id", destination.getDish_id());
                context.startActivity(intent);
            }
        });


        holder.tvQuantity.setText(String.valueOf(destination.getQuantity()));

        holder.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long newQuantity = destination.getQuantity() + 1;
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                cartRef.child(destination.getCartId()).child(destination.getDish_id()).child("quantity").setValue(newQuantity);
                  reloadFragment();
            }
        });


        holder.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long newQuantity = destination.getQuantity() - 1;
                if (newQuantity >= 1) {
                    holder.tvQuantity.setText(String.valueOf(newQuantity));
                    cartRef.child(destination.getCartId()).child(destination.getDish_id()).child("quantity").setValue(newQuantity);
                    // Refresh the fragment
                    reloadFragment();
                } else {
                    Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cartRef.child(destination.getCartId()).removeValue();
                                product.remove(destination);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                reloadFragment();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

//        holder.linear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, Moredetails_Activity.class);
//                intent.putExtra("productid", destination.getProductId());
//                context.startActivity(intent);
//            }
//        });



    }

    private void reloadFragment() {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new Order_Fragment());
        fragmentTransaction.commit();
    }


    private void showAddItemsDialog() {
        new AlertDialog.Builder(context)
                .setMessage("Your cart is empty. Please add items to proceed.")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return product.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, tvQuantity, price;
        LinearLayout linear;
        TextView btnIncrease, btnDecrease,total;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.leftIcon);
            name = itemView.findViewById(R.id.productname);
            price = itemView.findViewById(R.id.price);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            linear = itemView.findViewById(R.id.linear);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            delete = itemView.findViewById(R.id.delete);

        }
    }
}
