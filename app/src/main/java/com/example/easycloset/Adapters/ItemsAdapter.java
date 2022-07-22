package com.example.easycloset.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.easycloset.Activities.ItemDetailActivity;
import com.example.easycloset.Models.Item;
import com.example.easycloset.R;
import com.parse.ParseFile;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private final Context context;
    private final List<Item> items;

    public ItemsAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Item> item) {
        items.addAll(item);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvType;
        private final ImageView ivClothesImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            ivClothesImage = itemView.findViewById(R.id.ivClotheImage);
            itemView.setOnClickListener(this);
        }

        public void bind(Item item) {
            tvType.setText(String.format("%s %s", item.getColour(), item.getCategory()));
            ParseFile image = item.getImage();
            Glide.with(context).load(image.getUrl()).transform(new RoundedCorners(90)).into(ivClothesImage);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(Item.class.getSimpleName(), items.get(position));
                context.startActivity(intent);
            }
        }
    }
}