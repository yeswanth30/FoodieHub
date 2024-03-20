
package com.happymeals.Adapters;
 import android.content.Context;
        import android.content.Intent;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;


        import com.happymeals.Models.Sub_cat_model;
        import com.happymeals.R;
 import com.happymeals.Subcat_activity;
 import com.squareup.picasso.Picasso;

        import java.util.ArrayList;
        import java.util.List;

public class Sub_cat_Adapter extends RecyclerView.Adapter<Sub_cat_Adapter.ImageViewHolder> {
    private Context context;
    private List<Sub_cat_model> userList;

    public Sub_cat_Adapter(Context context, List<Sub_cat_model> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public Sub_cat_Adapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_images, parent, false);
        return new Sub_cat_Adapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Sub_cat_Adapter.ImageViewHolder holder, int position) {
        Sub_cat_model restaurantModel = userList.get(position);
        String imageUrl = restaurantModel.getImageurl();
        Picasso.get().load(imageUrl).into(holder.imageView);
        holder.resto_type.setText(restaurantModel.getDish_category());

        holder.relative1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Subcat_activity.class);
                intent.putExtra("dishcatid", restaurantModel.getDish_category_id());
                context.startActivity(intent);
            }
        });

        // Fetch food types

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView restoname, resto_type, time,food_type;
        RelativeLayout relative1;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.postImageView);
            resto_type = itemView.findViewById(R.id.name);
//
            relative1 = itemView.findViewById(R.id.relative1);
        }
    }
}
