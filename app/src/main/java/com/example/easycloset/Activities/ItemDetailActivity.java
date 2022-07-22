package com.example.easycloset.Activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.easycloset.Models.Item;
import com.example.easycloset.R;
import com.example.swipebutton_library.SwipeButton;
import com.parse.ParseFile;

import java.util.Date;

public class ItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        ImageView ivItem = findViewById(R.id.ivItem);
        TextView tvCategory = findViewById(R.id.tvItemCategory);
        TextView tvColour = findViewById(R.id.tvItemColor);
        TextView tvDate = findViewById(R.id.tvDateAdded);
        Item item = getIntent().getParcelableExtra(Item.class.getSimpleName());

        Date createdAt = item.getCreatedAt();
        String time = Item.calculateTimeAgo(createdAt);

        tvCategory.setText(item.getCategory());
        tvColour.setText(item.getColour());
        tvDate.setText(time);
        ParseFile image = item.getImage();
        Glide.with(this).load(image.getUrl()).into(ivItem);

        SwipeButton swipeButton = findViewById(R.id.swipe_btn_1);
        swipeButton.setOnActiveListener(() -> {
            swipeButton.setBackgroundResource(R.drawable.ic_baseline_check_circle_24);
            item.deleteInBackground(e -> {
                if (e == null) {
                    Toast.makeText(ItemDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }
}