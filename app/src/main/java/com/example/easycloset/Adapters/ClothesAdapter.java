package com.example.easycloset.Adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easycloset.Models.Clothes;
import com.example.easycloset.R;

import java.util.List;

public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ViewHolder> {

    Context context;
    List<Clothes> clothes;

    public ClothesAdapter(Context context, List<Clothes> clothes) {
        this.context = context;
        this.clothes = clothes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View clothingView = LayoutInflater.from(context).inflate(R.layout.item_clothes, parent, false);
        return new ViewHolder(clothingView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Clothes clothing = clothes.get(position);
        holder.bind(clothing);
    }

    @Override
    public int getItemCount() {
        return clothes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDescription;
        ImageView ivClothImg;
        TextView tvMerchant;
        Button btnBuy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTittle);
            tvDescription = itemView.findViewById(R.id.tvOverview);
            ivClothImg = itemView.findViewById(R.id.ivPoster);
            tvMerchant = itemView.findViewById(R.id.tvMerchant);
            btnBuy = itemView.findViewById(R.id.buyBtn);

        }

        public void bind(Clothes clothes) {
            tvTitle.setText(clothes.getTitle());
            tvDescription.setText(clothes.getPrice());
            tvMerchant.setText(clothes.getMerchant());
            String imageUrl;
            imageUrl = clothes.getImage();
            Glide.with(context).load(imageUrl).into(ivClothImg);

            btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(clothes.getLink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            });
        }

    }

}


