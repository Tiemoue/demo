package com.example.easycloset.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.easycloset.Adapters.ClothesAdapter;
import com.example.easycloset.Models.Clothes;
import com.example.easycloset.Models.User;
import com.example.easycloset.R;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ClothesActivity extends AppCompatActivity {
    public static final String TAG = ".ClothesActivity";
    List<Clothes> clothesList;
    ClothesAdapter clothesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes);
        RecyclerView rvClothes = findViewById(R.id.rvClothes);
        clothesList = new ArrayList<>();
        clothesAdapter = new ClothesAdapter(this, clothesList);
        rvClothes.setAdapter(clothesAdapter);
        rvClothes.setLayoutManager(new LinearLayoutManager(this));
        User user = (User) User.getCurrentUser();
        String gender = user.getKeyGender();
        String item = getIntent().getStringExtra("category");
        fetchShoppingData(gender, item);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        ImageButton backBtn = findViewById(R.id.btBack);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void fetchShoppingData(String gender, String item) {
        String ApiSearch = "https://serpapi.com/search.json?q=" + gender + "+" + item + "+buy&location=Austin,+Texas,+United+States&hl=en&gl=us&api_key=c378c610f455bddbc77ecc5457fc71d6637de8ef39c83c38e903a30212135552";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApiSearch, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("shopping_results");
                    clothesList.addAll(Clothes.fromJsonArray(results));
                    clothesAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.miLogout) {
            logOutUser();
        }
        return true;
    }

    private void logOutUser() {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Intent intent = new Intent(ClothesActivity.this, FirstActivity.class);
        startActivity(intent);
        finish();
    }
}
