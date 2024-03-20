package com.happymeals.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.happymeals.Models.CardModel;
import com.happymeals.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<CardModel> cardList;

    public CardAdapter(List<CardModel> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardModel card = cardList.get(position);
        holder.cardNumberTextView.setText(card.getCardNumber());
        holder.expiryDateTextView.setText(card.getExpiryDate());
        holder.cvvTextView.setText(card.getCvv());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardNumberTextView, expiryDateTextView, cvvTextView;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNumberTextView = itemView.findViewById(R.id.cardNumberTextView);
            expiryDateTextView = itemView.findViewById(R.id.expiryDateTextView);
            cvvTextView = itemView.findViewById(R.id.cvvTextView);
        }
    }
}
