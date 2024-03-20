package com.happymeals;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.Adapters.CustomFoodAdapter;
import com.happymeals.Adapters.Food_adaptor;
import com.happymeals.Adapters.ImagePagerAdapter;
import com.happymeals.Adapters.Image_adapter;
import com.happymeals.Adapters.ReviewAdapter;
import com.happymeals.Models.Dish_model;
import com.happymeals.Models.Image_model;
import com.happymeals.Models.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MoreDetailsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference cartRef;
    private String currentUserId;
    private Dish_model dishModel;
    private boolean isLiked = false;
    private ImageView like,back;

    private Image_adapter adapter;
    private List<Image_model> imagelist;
    RecyclerView recyclerView;
    private DatabaseReference imagesReference;
    RecyclerView ImagesrecyclerView;
    private CustomFoodAdapter adapter1;
    private DatabaseReference dishesReference;
    private List<Dish_model> dishList;

    private ReviewAdapter adapter11;
    private List<Review> reviewList = new ArrayList<>();
    private DatabaseReference reviewsReference;

    private String dishId;
    RecyclerView reviewRecyclerView;

    TextView noreviews,ratings,totalratings;
    private ViewPager2 viewPager;
    private ImagePagerAdapter pagerAdapter;

    String imageUrl;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moredetails_activity);

        like = findViewById(R.id.like);
        noreviews = findViewById(R.id.noreviews);
        ratings = findViewById(R.id.ratings);
        totalratings = findViewById(R.id.totalratings);
        back = findViewById(R.id.back);

         imageUrl = getIntent().getStringExtra("image_url");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        currentUserId = getSharedPreferences("user_details", MODE_PRIVATE).getString("userid", "");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("dishes");
        cartRef = FirebaseDatabase.getInstance().getReference().child("cart");


         reviewRecyclerView = findViewById(R.id.recycler_view);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter11 = new ReviewAdapter(reviewList);
        reviewRecyclerView.setAdapter(adapter11);




        ImagesrecyclerView = findViewById(R.id.ImagesrecyclerView);
//        ImagesrecyclerView.setHasFixedSize(true);
        ImagesrecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        dishList = new ArrayList<>();
        adapter1 = new CustomFoodAdapter(this, dishList);
        ImagesrecyclerView.setAdapter(adapter1);

        fetchDishes();



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             dishId = extras.getString("dish_id");

            DatabaseReference userCartRef = cartRef.child(currentUserId);
            userCartRef.child(dishId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TextView add = findViewById(R.id.add);
                    if (dataSnapshot.exists()) {
                        add.setText("Update");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle onCancelled
                }
            });


            databaseReference.child(dishId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dishModel = dataSnapshot.getValue(Dish_model.class);
                        if (dishModel != null) {
                            displayDishDetails(dishModel);
                           // displayDishImages(dishId);
                        }
                    } else {
                        // Handle if dish data doesn't exist
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle onCancelled
                }
            });

            databaseReference.child(dishId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dishModel = dataSnapshot.getValue(Dish_model.class);
                        if (dishModel != null) {
                            displayDishDetails(dishModel);
                            checkLikedStatus(dishId);
                        }
                    } else {
                        // Handle if dish data doesn't exist
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle onCancelled
                }
            });

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dishModel != null) {
                        updateLikeStatus(dishId, !isLiked);
                    } else {
                        Toast.makeText(MoreDetailsActivity.this, "Error: Dish object is null", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            TextView add = findViewById(R.id.add);

           add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long status = dishModel.getStatus();

                    if (status == 0) {
                        showAddToCartDialog(dishModel, 0); // Status 0 for success
                        addDishToCart(dishModel);
                    } else if (status == 1) {
                        showDisabledByOwnerDialog();
                    } else if (status == 2) {
                        showOutOfStockDialog();
                    }
                }
            });

            fetchReviewsFromFirebase(dishId);
            fetchRatingsFromFirebase(dishId);

            fetchImagesFromFirebase(dishId);



        }
    }

    private void displayDishDetails(Dish_model dishModel) {
        TextView dishNameTextView = findViewById(R.id.headingtextView);
        TextView categoryTextView = findViewById(R.id.textView3);
        TextView cookTimeTextView = findViewById(R.id.textView);
        TextView costTextView = findViewById(R.id.textView5);
        TextView dishTypeTextView = findViewById(R.id.textView4);
        TextView informationTextView = findViewById(R.id.popu22);
        TextView ingredientsTextView = findViewById(R.id.textView11);

        dishNameTextView.setText(dishModel.getDish_name());
        categoryTextView.setText(dishModel.getCategory());
        cookTimeTextView.setText(dishModel.getCooktime());
        costTextView.setText(dishModel.getCost());
        dishTypeTextView.setText(dishModel.getDishtype());
        informationTextView.setText(dishModel.getInformation());
        ingredientsTextView.setText(dishModel.getIngredients());
    }

    private void checkLikedStatus(String dishId) {
        DatabaseReference likedUsersRef = databaseReference.child(dishId).child("likedUsers").child(currentUserId);
        likedUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isLiked = dataSnapshot.exists();
                updateLikeButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void updateLikeButton() {
        if (isLiked) {
            like.setImageResource(R.drawable.fullhear);
        } else {
            like.setImageResource(R.drawable.plainhear);
        }
    }

    private void updateLikeStatus(String dishId, boolean likeStatus) {
        DatabaseReference likedUsersRef = databaseReference.child(dishId).child("likedUsers").child(currentUserId);
        if (likeStatus) {
            likedUsersRef.setValue(true);
        } else {
            likedUsersRef.removeValue();
        }
        isLiked = likeStatus;
        updateLikeButton();
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
                            Toast.makeText(MoreDetailsActivity.this, "This dish is currently disabled by the owner", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(MoreDetailsActivity.this, "This dish is currently out of stock", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Toast.makeText(MoreDetailsActivity.this, "Failed to add dish to cart: Dish not found", Toast.LENGTH_SHORT).show();
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

                    Log.d("ImageUrl", "Image URL: " + dish.getImageurl());

                    cartRef.child(newCartId).child(dish.getDish_id()).setValue(dish);

                    cartRef.child(newCartId).child(dish.getDish_id()).child("imageurl").setValue(imageUrl);
                    cartRef.child(newCartId).child(dish.getDish_id()).child("quantity").setValue(1);
                    cartRef.child(newCartId).child(dish.getDish_id()).child("userid").setValue(currentUserId);
                    cartRef.child(newCartId).child(dish.getDish_id()).child("cart_id").setValue(newCartId);
                }
                // Toast.makeText(MoreDetailsActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }


    private void fetchDishes() {
        dishesReference = FirebaseDatabase.getInstance().getReference("dishes");
        dishesReference.orderByChild("dish_popular").equalTo("Yes")
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
                                    // Set the image URL for the Dish_model
                                    dish.setImageurl(image.getImageurl());
                                }
                            }
                            // Add the Dish_model with the updated image URL to the list
                            dishList.add(dish);
                            adapter1.notifyDataSetChanged();
                        } else {
                            // Handle case where no images found for the dish
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
    }


//    private void displayDishImages(String dishId) {
//        DatabaseReference dishImageRef = FirebaseDatabase.getInstance().getReference().child("dishes_images");
//        dishImageRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                imagelist = new ArrayList<>(); // Initialize imagelist
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    // Check if the current node contains the given dish ID
//                    if (snapshot.hasChild("dish_id") && snapshot.child("dish_id").getValue(String.class).equals(dishId)) {
//                        String imageUrl = snapshot.child("imageurl").getValue(String.class);
//                        if (imageUrl != null) {
//                            Image_model imageModel = new Image_model();
//                            imageModel.setImageurl(imageUrl);
//                            imagelist.add(imageModel);
//                        }
//                    }
//                }
//                //displayImages(imagelist); // Notify the adapter that the data has changed
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle onCancelled
//                Toast.makeText(MoreDetailsActivity.this, "Failed to retrieve dish images.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void displayImages(List<Image_model> imageList) {
//        recyclerView = findViewById(R.id.Imagesrecycler);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        adapter = new Image_adapter(this, imageList);
//        recyclerView.setAdapter(adapter);
//    }



    private void fetchReviewsFromFirebase(String dishId) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
        Query query = reviewsRef.orderByChild("dishId").equalTo(dishId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    reviewList.add(review);
                }
                if (reviewList.isEmpty()) {
                    noreviews.setVisibility(View.VISIBLE);
                    Log.d("ReviewActivity", "No reviews yet for dish " + dishId);
                } else {
                    noreviews.setVisibility(View.GONE);

                    adapter11.notifyDataSetChanged();
                    Log.d("ReviewActivity", "Data loaded successfully. Number of reviews for dish " + dishId + ": " + reviewList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MoreDetailsActivity.this, "Failed to fetch reviews: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ReviewActivity", "Failed to fetch reviews: " + error.getMessage());
            }
        });
    }
    private void fetchRatingsFromFirebase(String dishId) {
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings");
        Query query = ratingsRef.orderByChild("dishId").equalTo(dishId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalRatings = 0.0f;
                int numRatings = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String ratingStr = dataSnapshot.child("rating").getValue(String.class);
                    if (ratingStr != null) {
                        float rating = Float.parseFloat(ratingStr);
                        totalRatings += rating;
                        numRatings++;
                    }
                }

                float averageRating = numRatings > 0 ? totalRatings / numRatings : 0.0f;

                String totalRatingsText = String.valueOf(numRatings);
                String averageRatingText = String.format(Locale.getDefault(), "%.1f", averageRating);
                ratings.setText(averageRatingText);
                totalratings.setText(totalRatingsText + " ratings");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MoreDetailsActivity.this, "Failed to fetch ratings: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RatingActivity", "Failed to fetch ratings: " + error.getMessage());
            }
        });
    }

    private void displayDishImages(String dishId) {
        DatabaseReference dishImagesRef = FirebaseDatabase.getInstance().getReference().child("dishes_images");
        dishImagesRef.orderByChild("dish_id").equalTo(dishId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> imageUrls = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imageUrl = snapshot.child("imageurl").getValue(String.class);
                    if (imageUrl != null) {
                        imageUrls.add(imageUrl);
                    }
                }
                if (!imageUrls.isEmpty()) {
                    setupViewPager(imageUrls);
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

//    private void setupViewPager(List<String> imageUrls) {
//        ViewPager2 viewPager = findViewById(R.id.viewPager);
//        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(this, imageUrls);
//        viewPager.setAdapter(pagerAdapter);
//    }


    private void fetchImagesFromFirebase(String dishId) {
        DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference().child("dishes_images");
        imagesRef.orderByChild("dish_id").equalTo(dishId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> imageUrls = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imageUrl = snapshot.child("imageurl").getValue(String.class);
                    if (imageUrl != null) {
                        imageUrls.add(imageUrl);
                    }
                }
                if (!imageUrls.isEmpty()) {
                    setupViewPager(imageUrls);
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setupViewPager(List<String> imageUrls) {
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new ImagePagerAdapter(MoreDetailsActivity.this, imageUrls);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
    }


    private void showAddToCartDialog(Dish_model dish, int status) {
        Log.d("MyApp", "showAddToCartDialog called");

        // Inflate the custom layout based on the status
        View customView = null;
        String title = "";
        String contentText = "";

        switch (status) {
            case 0:
                customView = LayoutInflater.from(this).inflate(R.layout.custom_success_dialog, null);
                title = "Success";
                contentText = "Item added to cart";
                break;
            case 1:
                customView = LayoutInflater.from(this).inflate(R.layout.custom_error_dialog, null);
                title = "Error";
                contentText = "This dish is currently disabled by the owner";
                break;
            case 2:
                customView = LayoutInflater.from(this).inflate(R.layout.custom_error_dialog1, null);
                title = "Error";
                contentText = "This dish is currently out of stock";
                break;
            default:
                // Do nothing or handle unknown status
                break;
        }
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(MoreDetailsActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setCustomView(customView);
        sweetAlertDialog.setTitleText(title)
                .setContentText(contentText)
                .setConfirmButtonBackgroundColor(android.R.color.transparent)
                .setConfirmText("")
                .showCancelButton(false);

        android.widget.Button confirmButton = sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM);
        if (confirmButton != null) {
            confirmButton.setTextColor(ContextCompat.getColor(MoreDetailsActivity.this, R.color.pink));
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
        View customView = LayoutInflater.from(MoreDetailsActivity.this).inflate(R.layout.custom_error_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MoreDetailsActivity.this);
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
        View customView = LayoutInflater.from(MoreDetailsActivity.this).inflate(R.layout.custom_error_dialog1, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MoreDetailsActivity.this);
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


