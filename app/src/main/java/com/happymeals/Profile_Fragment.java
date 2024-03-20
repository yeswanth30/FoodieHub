package com.happymeals;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class Profile_Fragment extends Fragment {

    RelativeLayout thirdd,firsttt,fourthh,seconddd,sixth,seventh;

    ImageView leftImageView;
    TextView Usernametextview,Emailtextview,logout;

    String userid,email,imageurl,username;
    Dialog dialog; // Declare dialog as a member variable


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        thirdd = view.findViewById(R.id.thirdd);
        fourthh = view.findViewById(R.id.fourthh);
        firsttt = view.findViewById(R.id.firsttt);
        leftImageView = view.findViewById(R.id.leftImageView);
        Usernametextview = view.findViewById(R.id.Usernametextview);
        Emailtextview = view.findViewById(R.id.Emailtextview);
        logout=view.findViewById(R.id.checkoutbutton);
        seconddd=view.findViewById(R.id.seconddd);
        sixth=view.findViewById(R.id.sixth);
        seventh=view.findViewById(R.id.seventh);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_details", MODE_PRIVATE);
        userid = sharedPreferences.getString("userid", "");
        email = sharedPreferences.getString("email", "");
        username = sharedPreferences.getString("name", "");
        imageurl = sharedPreferences.getString("imageurl", "");

        leftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_details", MODE_PRIVATE);
                String imageUrl = sharedPreferences.getString("imageurl", "");
                showFullImageDialog(imageUrl);
            }
        });



        Picasso.get().load(imageurl)
                .placeholder(R.drawable.authorrr)
                .error(R.drawable.authorrr)
                .into(leftImageView);
        Usernametextview.setText(username);
        Emailtextview.setText(email);

        logout.setOnClickListener(v -> logoutUser());

        seconddd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), PaymentMethod_Activity.class);
                startActivity(intent);
            }
        });

        sixth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), Aboutus_Activity.class);
                startActivity(intent);
            }
        });

        fourthh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), Address_Activity.class);
                startActivity(intent);
            }
        });

        seventh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), Supportcenter_Activity.class);
                startActivity(intent);
            }
        });

        thirdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), orderhistory_Activity.class);
                startActivity(intent);
            }
        });

        firsttt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().overridePendingTransition(R.anim.rotate_animation, R.anim.rotate_animation);
                Intent intent = new Intent(getContext(), Profile_Activity.class);


                startActivity(intent);
            }
        });

    }



    private void logoutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> logout());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void logout() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show();

        startActivity(intent);
        requireActivity().finish();
    }


    private void showFullImageDialog(String imageUrl) {
        dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_full_image);

        ImageView fullImageView = dialog.findViewById(R.id.fullImageView);
        Picasso.get().load(imageUrl).into(fullImageView);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation1;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

}}
