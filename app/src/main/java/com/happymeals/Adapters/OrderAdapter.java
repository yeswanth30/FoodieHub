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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.Models.Dish;
import com.happymeals.MoreDetailsActivity;
import com.happymeals.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Dish> dishes;
    String userid;

    public OrderAdapter(Context context, List<Dish> dishes) {
        this.context = context;
        this.dishes = dishes;
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        userid = sharedPreferences.getString("userid", "");

    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Dish dish = dishes.get(position);

        Log.e("checkingadapter", "inside" + dish.getDish_name());
        holder.dishNameTextView.setText(dish.getDish_name());
        holder.quantityTextView.setText("Quantity: " + dish.getQuantity());
        Picasso.get().load(dish.getImageurl()).into(holder.dishImageView);

        holder.ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog(dish);
            }
        });


        holder.reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviewDialog(dish);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView dishNameTextView, quantityTextView;
        ImageView dishImageView;
        RatingBar ratingBar;
        Button reviewButton,ratingButton;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            dishNameTextView = itemView.findViewById(R.id.dish_name);
            quantityTextView = itemView.findViewById(R.id.quantity);
            dishImageView = itemView.findViewById(R.id.dish_image);
            ratingButton = itemView.findViewById(R.id.btn_rating);
            reviewButton = itemView.findViewById(R.id.btn_review);
        }
    }

//    private void saveRatingToDatabase(Dish dish, float rating) {
//        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings");
//
//        String ratingId = ratingsRef.push().getKey();
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault());
//        String formattedTimestamp = sdf.format(new Date());
//
//        Map<String, Object> ratingMap = new HashMap<>();
//        ratingMap.put("ratingId", ratingId);
//        ratingMap.put("dishId", dish.getDish_id()); // Include the dish ID
//        ratingMap.put("userid", userid);
//        ratingMap.put("rating", String.valueOf(rating));
//        ratingMap.put("timestamp", formattedTimestamp);
//
//        ratingsRef.child(ratingId).setValue(ratingMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(context, "Rating added successfully", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context, "Failed to add rating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
    private void showReviewDialog(Dish dish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_review, null);
        EditText editTextReview = dialogView.findViewById(R.id.edit_review);

        SharedPreferences sharedPreferences = context.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userid", "");

        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");

        // Query to retrieve reviews for the specific dish and logged-in user
        Query userQuery = reviewsRef.orderByChild("userid").equalTo(userId);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder reviewsStringBuilder = new StringBuilder();
                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    String reviewDishId = reviewSnapshot.child("dishId").getValue(String.class);
                    if (reviewDishId != null && reviewDishId.equals(dish.getDish_id())) {
                        // If the review is for the particular dish, append it to the StringBuilder
                        String reviewText = reviewSnapshot.child("reviewText").getValue(String.class);
                        reviewsStringBuilder.append(reviewText).append("\n");
                    }
                }

                // Set the existing reviews text in the EditText
                editTextReview.setText(reviewsStringBuilder.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
                Toast.makeText(context, "Failed to fetch reviews: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(dialogView);
        builder.setTitle("Write a Review");
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String review = editTextReview.getText().toString().trim();
                saveReviewToDatabase(dish, review, userId);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void saveReviewToDatabase(Dish dish, String review, String userid) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");

        // Fetch reviews for the current user and the particular dish from Firebase
        reviewsRef.orderByChild("userid").equalTo(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean reviewExists = false;
                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    String existingDishId = reviewSnapshot.child("dishId").getValue(String.class);
                    if (existingDishId != null && existingDishId.equals(dish.getDish_id())) {
                        // If an existing review is found for the current user and the particular dish
                        reviewExists = true;
                        String reviewId = reviewSnapshot.getKey();
                        updateReviewInDatabase(reviewsRef.child(reviewId), review);
                        break;
                    }
                }
                if (!reviewExists) {
                    // If no existing review is found, create a new one
                    createNewReviewInDatabase(reviewsRef, dish, review, userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
                Toast.makeText(context, "Failed to check existing reviews: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewReviewInDatabase(DatabaseReference reviewsRef, Dish dish, String review, String userid) {
        String reviewId = reviewsRef.push().getKey();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault());
        String formattedTimestamp = sdf.format(new Date());

        Map<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("reviewId", reviewId);
        reviewMap.put("dishId", dish.getDish_id());
        reviewMap.put("userid", userid);
        reviewMap.put("reviewText", review);
        reviewMap.put("timestamp", formattedTimestamp);

        reviewsRef.child(reviewId).setValue(reviewMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Review added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to add review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateReviewInDatabase(DatabaseReference reviewRef, String review) {
        reviewRef.child("reviewText").setValue(review)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Review updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to update review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //    private void showRatingDialog(Dish dish) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rating, null);
//        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
//        builder.setView(dialogView);
//        builder.setTitle("Rate this dish");
//        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                float rating = ratingBar.getRating();
//                saveRatingToDatabase(dish, rating);
//            }
//        });
//        builder.setNegativeButton("Cancel", null);
//        builder.show();
//    }


    private void showRatingDialog(Dish dish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rating, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        builder.setView(dialogView);
        builder.setTitle("Rate this dish");

        // Fetch the user's existing rating for the dish, if any
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings");
        Query userRatingQuery = ratingsRef.orderByChild("userid").equalTo(userid);
        userRatingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                    String existingDishId = ratingSnapshot.child("dishId").getValue(String.class);
                    if (existingDishId != null && existingDishId.equals(dish.getDish_id())) {
                        // If the user has already rated this dish, set the rating bar to the existing rating
                        float existingRating = Float.parseFloat(ratingSnapshot.child("rating").getValue(String.class));
                        ratingBar.setRating(existingRating);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
                Toast.makeText(context, "Failed to fetch ratings: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                float rating = ratingBar.getRating();
                saveRatingToDatabase(dish, rating);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveRatingToDatabase(Dish dish, float rating) {
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings");

        // Fetch ratings for the current user and the particular dish from Firebase
        ratingsRef.orderByChild("userid").equalTo(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean ratingExists = false;
                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                    String existingDishId = ratingSnapshot.child("dishId").getValue(String.class);
                    if (existingDishId != null && existingDishId.equals(dish.getDish_id())) {
                        // If an existing rating is found for the current user and the particular dish
                        ratingExists = true;
                        String ratingId = ratingSnapshot.getKey();
                        updateRatingInDatabase(ratingsRef.child(ratingId), rating);
                        break;
                    }
                }
                if (!ratingExists) {
                    // If no existing rating is found, create a new one
                    createNewRatingInDatabase(ratingsRef, dish, rating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
                Toast.makeText(context, "Failed to check existing ratings: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewRatingInDatabase(DatabaseReference ratingsRef, Dish dish, float rating) {
        String ratingId = ratingsRef.push().getKey();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault());
        String formattedTimestamp = sdf.format(new Date());

        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("ratingId", ratingId);
        ratingMap.put("dishId", dish.getDish_id());
        ratingMap.put("userid", userid);
        ratingMap.put("rating", String.valueOf(rating));
        ratingMap.put("timestamp", formattedTimestamp);

        ratingsRef.child(ratingId).setValue(ratingMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Rating added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to add rating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateRatingInDatabase(DatabaseReference ratingRef, float rating) {
        ratingRef.child("rating").setValue(String.valueOf(rating))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Rating updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to update rating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }











}
