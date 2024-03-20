package com.happymeals.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.Models.Review;
import com.happymeals.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;
    private static DatabaseReference usersRef;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
        this.usersRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView reviewTextView;
        private TextView reviewUserNameTextView;
        private ImageView reviewUserImageView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewTextView = itemView.findViewById(R.id.reviewContentTextView);
            reviewUserNameTextView = itemView.findViewById(R.id.reviewUserNameTextView);
            reviewUserImageView = itemView.findViewById(R.id.reviewUserImageView);
        }

        public void bind(Review review) {
            reviewTextView.setText(review.getReviewText());

            DatabaseReference userRef = usersRef.child(review.getUserid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        String userImageUrl = dataSnapshot.child("imageurl").getValue(String.class);

                        if (userName != null && userImageUrl != null) {
                            reviewUserNameTextView.setText(userName);
                            Picasso.get().load(userImageUrl).into(reviewUserImageView);
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
}
