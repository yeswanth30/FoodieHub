package com.happymeals;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public class AddressAddedDialog extends Dialog {
    private Button okButton;


    public AddressAddedDialog(@NonNull Context context) {
        super(context);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_address_added);

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_animation);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout layout = findViewById(R.id.dialog_layout);
        layout.startAnimation(anim);
        // Set up the "OK" button click listener
        okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Dismiss the dialog
                // Navigate to the Address_Activity
                getContext().startActivity(new Intent(getContext(), Address_Activity.class));
            }
        });
    }
}
