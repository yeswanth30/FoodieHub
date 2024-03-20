package com.happymeals;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.Adapters.CardAdapter;
import com.happymeals.Models.AddressModel;
import com.happymeals.Models.CardModel;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethod_Activity extends AppCompatActivity {
     ImageView back;
     RecyclerView recyclerView;
     private CardAdapter cardAdapter;
     private List<CardModel> cardList;
     private DatabaseReference cardsRef;
     private String userId;


     @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paymentmethod_activity);

        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentMethod_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new SingleItemLinearLayoutManager(this));


        cardList = new ArrayList<>();
        cardAdapter = new CardAdapter(cardList);
        recyclerView.setAdapter(cardAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "");

        cardsRef = FirebaseDatabase.getInstance().getReference().child("cards");

         cardsRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 cardList.clear();
                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                     String cardUserId = snapshot.child("userid").getValue(String.class);
                     // Check if card belongs to the current user
                     if (cardUserId != null && cardUserId.equals(userId)) {
                         String cardNumber = snapshot.child("card_number").getValue(String.class);
                         String expiryDate = snapshot.child("expiry_date").getValue(String.class);
                         String cvv = snapshot.child("cvv").getValue(String.class);
                         cardList.add(new CardModel(cardNumber, expiryDate, cvv, cardUserId));
                     }
                 }
                 cardAdapter.notifyDataSetChanged();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                 Toast.makeText(PaymentMethod_Activity.this, "Failed to fetch cards: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
             }
         });


     }
}