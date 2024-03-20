package com.happymeals.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.happymeals.EditAddressActivity;
import com.happymeals.Models.AddressModel;
import com.happymeals.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private List<AddressModel> addressList;
    private Context context;
    private OnRadioButtonClickListener radioButtonClickListener;



    public AddressAdapter(List<AddressModel> addressList, OnRadioButtonClickListener radioButtonClickListener) {
        this.addressList = addressList;
        this.radioButtonClickListener = radioButtonClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressModel addressModel = addressList.get(position);
        holder.textViewAddress.setText(addressModel.getAddress() + ", " + addressModel.getArea());
        holder.textView2.setText(addressModel.getCity() + " , "+ addressModel.getCountry());
        holder.textView4.setText(addressModel.getPhone() + " , "+addressModel.getPincode());

        if (addressModel.getDefaultAddress() != null && addressModel.getDefaultAddress().equals("yes")) {
            holder.radioButtonDefault.setChecked(true);
        } else {
            holder.radioButtonDefault.setChecked(false);
        }

        holder.radioButtonDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonClickListener.onRadioButtonClick(addressModel);
            }
        });

        holder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressModel != null && v.getContext() != null) {
                    Intent intent = new Intent(v.getContext(), EditAddressActivity.class);
                    intent.putExtra("addressModel", addressModel);
                    v.getContext().startActivity(intent);
                } else {
                    Log.e("AddressAdapter", "AddressModel or context is null");
                }
            }
        });




        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAddress(addressModel);
            }
        });
    }


    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAddress,textView2,textView3,textView4;
        RadioButton radioButtonDefault;
        ImageView buttonDelete,buttonEdit;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            radioButtonDefault = itemView.findViewById(R.id.radioButtonDefault);
            textView2 = itemView.findViewById(R.id.textView2);
            textView4 = itemView.findViewById(R.id.textView4);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonEdit = itemView.findViewById(R.id.editing);

        }
    }

    public interface OnRadioButtonClickListener {
        void onRadioButtonClick(AddressModel addressModel);
    }

    private void showDeleteConfirmationDialog(AddressModel addressModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Address");
        builder.setMessage("Are you sure you want to delete this address?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAddress(addressModel);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteAddress(AddressModel addressModel) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Address");
        databaseReference.child(addressModel.getAddressId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       // Toast.makeText(context, "Address deleted successfully", Toast.LENGTH_SHORT).show();
                        addressList.remove(addressModel);
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Toast.makeText(context, "Failed to delete address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
