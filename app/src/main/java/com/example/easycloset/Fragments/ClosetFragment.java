package com.example.easycloset.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.easycloset.Activities.MainActivity;
import com.example.easycloset.Adapters.ItemsAdapter;
import com.example.easycloset.Models.Item;
import com.example.easycloset.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ClosetFragment extends Fragment {

    private ItemsAdapter adapter;
    private List<Item> allItems;
    private MainActivity activity;
    private ProgressDialog progressDialog;


    public ClosetFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        queryPosts();
    }

    public ClosetFragment(MainActivity mainActivity) {
        activity = mainActivity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_closet, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton btnAddClothes = view.findViewById(R.id.floatingAction);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        RecyclerView rvItems = view.findViewById(R.id.rvItems);
        rvItems.setLayoutManager(gridLayoutManager);
        allItems = new ArrayList<>();
        adapter = new ItemsAdapter(getContext(), allItems);
        rvItems.setAdapter(adapter);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Updating Closet...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        queryPosts();
        btnAddClothes.setOnClickListener(v -> activity.setFragmentContainer(activity.getUploadFragment()));
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_filter);

        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if (checkedIds.size() > 0) {
                    Chip chip = view.findViewById(checkedIds.get(0));
                    String category = chip.getText().toString().toLowerCase();
                    queries(category);
                } else {
                    queryPosts();
                }
            }
        });
    }

    public ItemsAdapter getAdapter() {
        return adapter;
    }

    public List<Item> getAllItems() {
        return allItems;
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        // include data referred by user key
//        query.include(Item.KEY_USER);
//        query.whereEqualTo(Item.KEY_USER, ParseUser.getCurrentUser());
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground((items, e) -> {
            // check for errors
            if (e != null) {
                return;
            }
            // save received posts to list and notify adapter of new data
            allItems.clear();
            progressDialog.dismiss();
            allItems.addAll(items);
            adapter.notifyDataSetChanged();
        });
    }

    public void queries(String category) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        // include data referred by user key
//        query.include(Item.KEY_USER);
        query.whereEqualTo(Item.KEY_CATEGORY, category);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground((items, e) -> {
            // check for errors
            if (e != null) {
                return;
            }
            // save received posts to list and notify adapter of new data
            allItems.clear();
            progressDialog.dismiss();
            allItems.addAll(items);
            adapter.notifyDataSetChanged();
        });
    }
}