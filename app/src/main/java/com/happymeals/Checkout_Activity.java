package com.happymeals;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.happymeals.Models.AddressModel;
import com.happymeals.Models.Card;
import com.happymeals.Models.Coupon;
import com.happymeals.Models.Payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Checkout_Activity extends AppCompatActivity {

    TextView leftTextView, rightTextView,totalPrice;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;


    EditText cardNumber, expiryDate, cvv;


    ImageView back;

    TextView proceed,couponcode,subtotal,discountpercentage;
     String userId;

    double totalAmount;

    String orderId,cartId;
    ImageView couponcode1;

    Button cardsave;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity);

        leftTextView = findViewById(R.id.leftTextView);
        totalPrice = findViewById(R.id.totalPrice);
        rightTextView = findViewById(R.id.rightTextView);
        cardNumber = findViewById(R.id.card_number);
        expiryDate = findViewById(R.id.exparedate);
        proceed = findViewById(R.id.proceed);
        cardsave = findViewById(R.id.cardsave);
        cvv = findViewById(R.id.cvv);
        cardNumber.addTextChangedListener(new CardNumberTextWatcher());

        back = findViewById(R.id.back);
        couponcode = findViewById(R.id.couponcode);
        couponcode1 = findViewById(R.id.couponcode1);
        subtotal = findViewById(R.id.subtotal);
        discountpercentage=findViewById(R.id.discountpercentage);

       // Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Log.d("Checkout_Activityddd", "Extra: " + key + " = " + extras.get(key));
            }
        } else {
            Log.d("Checkout_Activityddd", "No extras found in the intent");
        }

        totalAmount = extras.getDouble("totalPrice", 0) + 1;
        Log.d("Checkout_Activityddd", "Total Amount: " + totalAmount);

         orderId = getIntent().getStringExtra("orderId");
        cartId = getIntent().getStringExtra("cartId");





        subtotal.setText(String.format(Locale.getDefault(), "Rs.%.2f", totalAmount));

        discountpercentage.setText("Rs.0.00");
        totalPrice.setText(String.format(Locale.getDefault(), "Rs.%.2f", totalAmount));

        couponcode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show dialog for entering coupon code
                showCouponCodeDialog();
            }
        });




        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Checkout_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Checkout_Activity.this, Address_Activity.class);
                startActivity(intent);
            }
        });
        setupPaymentProcess();

//        proceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                setupPaymentProcess();
//            }
//        });

        cardsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateCardDetails()){
                    saveCardDetails();
            }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userid", "");

        databaseReference = FirebaseDatabase.getInstance().getReference("Address");

        databaseReference.orderByChild("userId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AddressModel addressModel = snapshot.getValue(AddressModel.class);
                    if (addressModel != null && addressModel.getDefaultAddress() != null && addressModel.getDefaultAddress().equals("yes")) {
                        String addressDetails =  addressModel.getAddress() + "," +
                               addressModel.getArea() + "\n" +
                                 addressModel.getCity() + "," +
                                 addressModel.getCountry() + "," +
                                  addressModel.getPincode();
                        leftTextView.setText(addressDetails);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        return sharedPreferences.getString("userid", "");
    }

    private void setupPaymentProcess() {
        findViewById(R.id.proceed).setOnClickListener(v -> {

                String enteredCouponCode = couponcode.getText().toString().trim();
                savePaymentDetails(enteredCouponCode);

                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("orders").child(orderId);
                orderRef.child("status").setValue(1)
                        .addOnSuccessListener(aVoid -> {

                        }).addOnFailureListener(e -> {
                            Toast.makeText(Checkout_Activity.this, "Failed to update order status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

        });
    }


    private class CardNumberTextWatcher implements TextWatcher {
        private static final int CARD_NUMBER_GROUP_SIZE = 4;
        private static final char CARD_NUMBER_SEPARATOR = ' ';
        private int previousLength = 0;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            previousLength = s.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // No implementation needed
        }

        @Override
        public void afterTextChanged(Editable s) {
            String input = s.toString().replaceAll(String.valueOf(CARD_NUMBER_SEPARATOR), "");

            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                formatted.append(input.charAt(i));
                if ((i + 1) % CARD_NUMBER_GROUP_SIZE == 0 && i != input.length() - 1) {
                    formatted.append(CARD_NUMBER_SEPARATOR);
                }
            }

            cardNumber.removeTextChangedListener(this);
            cardNumber.setText(formatted.toString());
            cardNumber.setSelection(formatted.length());
            cardNumber.addTextChangedListener(this);

            // Check if the card number exceeds 16 digits
            String cardNumberValue = formatted.toString().replaceAll("\\s", ""); // Remove spaces
            if (cardNumberValue.length() > 16) {
                // Show a warning dialog
                showWarningDialog("Card number must be 16 digits");
            }

            // Auto-fix and move to the next field for the expiry date
            autoFixAndMoveToNextExpiryDate();
        }

        private void autoFixAndMoveToNextExpiryDate() {
            int length = expiryDate.getText().toString().trim().length();

            if (previousLength <= length && length < 5) {
                String expiryDateValue = expiryDate.getText().toString().trim();
                if (length == 1 && !expiryDateValue.matches("0[1-9]|1[0-2]")) {
                    String autoFixStr = "0" + expiryDateValue;
                    if (autoFixStr.length() == 2) {
                        autoFixStr = autoFixStr + "/";
                    }
                    expiryDate.setText(autoFixStr);
                    expiryDate.setSelection(autoFixStr.length());
                } else if (length == 2 && !expiryDateValue.matches("(0[1-9]|1[0-2])/[0-9]")) {
                    String autoFixStr = expiryDateValue + "/";
                    expiryDate.setText(autoFixStr);
                    expiryDate.setSelection(autoFixStr.length());
                }
            } else if (length == 5) {
                cvv.requestFocus(); // auto move to the next edittext
            }
        }
    }


    private boolean validateCardDetails() {
        String cardNumberValue = cardNumber.getText().toString().trim().replaceAll("\\s", ""); // Remove spaces
        String expiryDateValue = expiryDate.getText().toString().trim();
        String cvvValue = cvv.getText().toString().trim();

        if (cardNumberValue.length() != 16) {
            cardNumber.setError("Card number must be 16 digits");
            cardNumber.requestFocus();
            return false;
        }

        if (expiryDateValue.length() != 5) {
            expiryDate.setError("Expiry date must be in MM/YY format");
            expiryDate.requestFocus();
            return false;
        }

        if (cvvValue.length() != 3) {
            cvv.setError("CVV must be 3 digits");
            cvv.requestFocus();
            return false;
        }

        return true;
    }


    private void showWarningDialog(String message) {
        new SweetAlertDialog(Checkout_Activity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning")
                .setContentText(message)
                .setConfirmText("OK")
                .show();
    }
    private void saveCardDetails() {
        String cardNumberValue = cardNumber.getText().toString().trim();
        String expiryDateValue = expiryDate.getText().toString().trim();
        String cvvValue = cvv.getText().toString().trim();
        String totalprice = totalPrice.getText().toString().trim();
        // Assuming you have a method to retrieve the current user ID

        // Assuming you have a "cards" node in your Firebase database
        DatabaseReference cardsRef = FirebaseDatabase.getInstance().getReference("cards");

        // Perform a query to check for existing cards with the same card number, expiry date, and CVV
        Query query = cardsRef.orderByChild("card_number").equalTo(cardNumberValue);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean cardExists = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Card existingCard = snapshot.getValue(Card.class);
                    if (existingCard != null && existingCard.getExpiry_date().equals(expiryDateValue)
                            && existingCard.getCvv().equals(cvvValue)) {
                        cardExists = true;
                        break;
                    }
                }

                if (cardExists) {
                    // Card already exists, no need to save again
                    Toast.makeText(Checkout_Activity.this, "Card already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a new Card object
                    Card card = new Card();
                    card.setUserid(userId); // Set the userId
                    card.setCard_number(cardNumberValue);
                    card.setExpiry_date(expiryDateValue);
                    card.setTotal_price(totalprice);
                    card.setCvv(cvvValue);

                    // Generate a unique key for the card
                    String cardId = cardsRef.push().getKey();

                    // Save the card object to the database
                    cardsRef.child(cardId).setValue(card)
                            .addOnSuccessListener(aVoid -> {
                                // Card details saved successfully
                                Toast.makeText(Checkout_Activity.this, "Card details saved successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Failed to save card details
                                Toast.makeText(Checkout_Activity.this, "Failed to save card details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }



    private void savePaymentDetails(String enteredCouponCode) {
        String cardNumberValue = cardNumber.getText().toString().trim();
        String expiryDateValue = expiryDate.getText().toString().trim();
        String cvvValue = cvv.getText().toString().trim();
        String totalprice = totalPrice.getText().toString().trim();

        // Check if any of the fields are empty
        if (cardNumberValue.isEmpty() || expiryDateValue.isEmpty() || cvvValue.isEmpty()) {
            // Show toast message to fill all details
            Toast.makeText(Checkout_Activity.this, "Please fill all card details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get a reference to the "cards" node in Firebase
        DatabaseReference cardsRef = FirebaseDatabase.getInstance().getReference("cards");

        // Query to find the card with the given card number
        Query query = cardsRef.orderByChild("card_number").equalTo(cardNumberValue);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String cardId = dataSnapshot.getChildren().iterator().next().getKey();

                    // Create a new payment object
                    Payment payment = new Payment();
                    payment.setUserid(userId);
                    payment.setCard_id(cardId);
                    payment.setTotal_price(totalprice);
                    payment.setCoupon_code(enteredCouponCode); // Store the entered coupon code
                    payment.setOrderId(orderId);
                    payment.setTime(getCurrentTime());

                    DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payments");
                    String paymentId = paymentRef.push().getKey();
                    payment.setPaymentId(paymentId);

                    // Save the payment to Firebase
                    paymentRef.child(paymentId).setValue(payment)
                            .addOnSuccessListener(aVoid -> {
                                // Payment added successfully
                                PaymentDialogFragment dialog = new PaymentDialogFragment();
                                dialog.show(getSupportFragmentManager(), "PaymentDialogFragment");
                            })
                            .addOnFailureListener(e -> {
                                // Failed to add payment, handle error if needed
                                Toast.makeText(Checkout_Activity.this, "Failed to add payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
                else {
                    // Card with the given card number not found, proceed without displaying a message
                    savePaymentWithoutCard(enteredCouponCode, totalprice);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(Checkout_Activity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy - HH-mm", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void  savePaymentWithoutCard(String enteredCouponCode, String totalprice) {
        // Create a new payment object
        Payment payment = new Payment();
        payment.setUserid(userId);
        payment.setTotal_price(totalprice);
        payment.setCoupon_code(enteredCouponCode);
        payment.setOrderId(orderId);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy - HH-mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        payment.setTime(currentTime);

        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payments");
        String paymentId = paymentRef.push().getKey();
        payment.setPaymentId(paymentId);

        // Save the payment to Firebase
        paymentRef.child(paymentId).setValue(payment)
                .addOnSuccessListener(aVoid -> {
                    // Payment added successfully
                    PaymentDialogFragment dialog = new PaymentDialogFragment();
                    dialog.show(getSupportFragmentManager(), "PaymentDialogFragment");
                })
                .addOnFailureListener(e -> {
                    // Failed to add payment, handle error if needed
                    Toast.makeText(Checkout_Activity.this, "Failed to add payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void showCouponCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Coupon Code");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String couponCode = input.getText().toString().trim();
                applyCouponCode(couponCode);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }



    private void applyCouponCode(String enteredCouponCode) {
        DatabaseReference paymentsRef = FirebaseDatabase.getInstance().getReference("payments");

        String userId = getSharedPreferences("user_details", MODE_PRIVATE).getString("userid", "");

        paymentsRef.orderByChild("coupon_code").equalTo(enteredCouponCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean couponUsedByCurrentUser = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String paymentUserId = snapshot.child("userid").getValue(String.class);
                    if (paymentUserId != null && paymentUserId.equals(userId)) {
                        couponUsedByCurrentUser = true;
                        break;
                    }
                }

                if (couponUsedByCurrentUser) {
                    Toast.makeText(Checkout_Activity.this, "Coupon code already used by the current user", Toast.LENGTH_SHORT).show();
                    totalPrice.setText(String.format(Locale.getDefault(), "Rs.%.2f", totalAmount));
                    discountpercentage.setText("Rs.0.00");
                    couponcode.setText("");
                } else {
                    DatabaseReference couponsRef = FirebaseDatabase.getInstance().getReference("coupons");

                    couponsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean couponApplied = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                DataSnapshot couponDataSnapshot = snapshot.child("coupon_code");

                                String couponCode = couponDataSnapshot.getValue(String.class);
                                if (couponCode != null && couponCode.equals(enteredCouponCode)) {
                                    Coupon coupon = snapshot.getValue(Coupon.class);
                                    if (coupon != null) {
                                        applyDiscount(coupon, totalAmount);

                                        couponApplied = true;
                                        break;
                                    }
                                }
                            }

                            if (!couponApplied) {
                                Toast.makeText(Checkout_Activity.this, "Invalid coupon code", Toast.LENGTH_SHORT).show();
                                totalPrice.setText(String.format(Locale.getDefault(), "Rs.%.2f", totalAmount));
                                discountpercentage.setText("Rs.0.00");
                                couponcode.setText("");
                            } else {
                                // Set the entered coupon code to the TextView if it's valid
                                couponcode.setText(enteredCouponCode);
                               // couponcode.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                            Toast.makeText(Checkout_Activity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(Checkout_Activity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void applyDiscount(Coupon coupon, double totalAmount) {
        double discountPercentage = Double.parseDouble(coupon.getCoupon_percentage());
        double discountAmount = (totalAmount * discountPercentage) / 100;
        double couponLimit = Double.parseDouble(coupon.getCoupon_limit());

        if (discountAmount > couponLimit) {
            discountAmount = couponLimit;
        }

        double discountedTotalAmount = totalAmount - discountAmount;

        totalPrice.setText(String.format(Locale.getDefault(), "Rs.%.2f", discountedTotalAmount));

        discountpercentage.setText(String.format(Locale.getDefault(), "Rs.%.2f", discountAmount));
    }






}
