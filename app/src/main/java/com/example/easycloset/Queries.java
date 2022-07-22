package com.example.easycloset;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.easycloset.Activities.ClothesActivity;
import com.example.easycloset.Models.Clothes;
import com.example.easycloset.Models.Item;
import com.example.easycloset.Models.Suggest;
import com.example.easycloset.Models.User;
import com.example.easycloset.Models.Weather;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Headers;

public class Queries {

    private final ImageView outerLayer;
    private final ImageView baseLayer;
    private final ImageView ivBottom;
    private final ImageView feet;
    private final TextView tvOuter;
    private final TextView tvBase;
    private final TextView tvFeet;
    private final TextView tvBottom;
    private final Context context;
    private final Suggest suggest = new Suggest();

    public Queries(ImageView outerLayer, ImageView baseLayer, ImageView ivBottom, ImageView feet, TextView tvOuter, TextView tvBase, TextView tvBottom, TextView tvFeet, Context context) {
        this.outerLayer = outerLayer;
        this.baseLayer = baseLayer;
        this.ivBottom = ivBottom;
        this.feet = feet;
        this.context = context;
        this.tvOuter = tvOuter;
        this.tvBase = tvBase;
        this.tvFeet = tvFeet;
        this.tvBottom = tvBottom;
    }

    public void checkWeather(Weather weather) {
        int temp = Integer.parseInt(weather.getTemp());
        if (temp < 30) {
            multipleQueries("coat", "hoodie", "joggers", "boots");
        } else if (temp < 50) {
            multipleQueries("jacket", "hoodie", "joggers", "sneakers");
        } else if (temp < 60) {
            multipleQueries("hoodie", "sweater", "pants", "sneakers");
        } else if (temp < 80) {
            multipleQueries("sweater", "t-shirt", "pants", "sneakers");
        } else {
            multipleQueries("sweater", "t-shirt", "shorts", "slides");
        }
    }

    public void multipleQueries(String outer, String base, String bottom, String foot) {
        ParseQuery myQuery1 = new ParseQuery(Item.class);
        myQuery1.whereEqualTo(Item.KEY_CATEGORY, outer);

        ParseQuery myQuery2 = new ParseQuery(Item.class);
        myQuery2.whereEqualTo(Item.KEY_CATEGORY, base);

        ParseQuery myQuery3 = new ParseQuery(Item.class);
        myQuery3.whereEqualTo(Item.KEY_CATEGORY, bottom);

        ParseQuery myQuery4 = new ParseQuery(Item.class);
        myQuery4.whereEqualTo(Item.KEY_CATEGORY, foot);

        List<ParseQuery<Item>> queries = new ArrayList<ParseQuery<Item>>();
        queries.add(myQuery1);
        queries.add(myQuery2);
        queries.add(myQuery3);
        queries.add(myQuery4);

        ParseQuery<Item> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground((objects, e) -> {
            if (e != null) {
                return;
            }

            List<Item> outerArray = new ArrayList<>();
            List<Item> baseArray = new ArrayList<>();
            List<Item> bottomArray = new ArrayList<>();
            List<Item> feetArray = new ArrayList<>();

            for (Item object : objects) {
                if (object.getCategory().equals(outer)) {
                    outerArray.add(object);
                } else if (object.getCategory().equals(base)) {
                    baseArray.add(object);
                } else if (object.getCategory().equals(bottom)) {
                    bottomArray.add(object);

                } else if (object.getCategory().equals(foot)) {
                    feetArray.add(object);
                }
            }

            Random random = new Random();

            if (outerArray.size() != 0) {
                Item outerItem = outerArray.get(random.nextInt(outerArray.size()));
                ParseFile image = outerItem.getImage();
                suggest.setOuter(outerItem.getCategory());
                suggest.setOuterColor(outerItem.getColour());
                suggest.setOuterImgUrl(outerItem.getImage().getUrl());
                generateFit(image, outerLayer);
                setText(tvOuter, outerItem.getColour() + " " + outerItem.getCategory());
            } else {
                getItem(outer, outerLayer);
                suggest.setOuter(outer);
            }

            if (baseArray.size() != 0) {
                Item baseItem = baseArray.get(random.nextInt(baseArray.size()));
                ParseFile image = baseItem.getImage();
                suggest.setBase(baseItem.getCategory());
                suggest.setBaseImgUrl(baseItem.getImage().getUrl());
                suggest.setBaseColor(baseItem.getColour());
                generateFit(image, baseLayer);
                setText(tvBase, baseItem.getColour() + " " + baseItem.getCategory());
            } else {
                getItem(base, baseLayer);
                suggest.setBase(base);
            }

            if (bottomArray.size() != 0) {
                Item bottomItem = bottomArray.get(random.nextInt(bottomArray.size()));
                ParseFile image = bottomItem.getImage();
                suggest.setBottom(bottomItem.getCategory());
                suggest.setBottomImgUrl(image.getUrl());
                suggest.setBottomColor(bottomItem.getColour());
                generateFit(image, ivBottom);
                setText(tvBottom, bottomItem.getColour() + " " + bottomItem.getCategory());
            } else {
                getItem(bottom, ivBottom);
                setText(tvBottom, "Not Available");
                suggest.setBottom(bottom);
            }

            if (feetArray.size() != 0) {
                Item feetItem = feetArray.get(random.nextInt(feetArray.size()));
                ParseFile image = feetItem.getImage();
                suggest.setFeet(feetItem.getCategory());
                suggest.setFeetImgUrl(image.getUrl());
                suggest.setFeetColor(feetItem.getColour());
                generateFit(image, feet);
                setText(tvFeet, feetItem.getColour() + " " + feetItem.getCategory());
            } else {
                getItem(foot, feet);
                suggest.setFeet(foot);
            }
            imageClickListener(outerLayer, outer);
            imageClickListener(feet, foot);
            imageClickListener(ivBottom, bottom);
            imageClickListener(baseLayer, base);
        });
    }

    public void generateFit(ParseFile outerItem, ImageView outerLayer) {
        Glide.with(context).load(outerItem.getUrl()).into(outerLayer);
    }

    public void setText(TextView textView, String s) {
        textView.setText(s);
    }

    public void getItem(String item, ImageView outerLayer) {
        List<Clothes> clothes = new ArrayList<>();
        User user = (User) User.getCurrentUser();
        String gender = user.getKeyGender();
        String ApiSearch = "https://serpapi.com/search.json?q=" + gender + "+" + item + "+buy&location=Austin,+Texas,+United+States&hl=en&gl=us&api_key=" + R.string.seprei_api_key;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApiSearch, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("shopping_results");
                    clothes.addAll(Clothes.fromJsonArray(results));
                    Clothes clothes1 = clothes.get(0);
                    Glide.with(context).load(clothes1.getImage()).into(outerLayer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            }
        });
    }

    public void startShoppingActivity(String item) {
        Intent intent = new Intent(context, ClothesActivity.class);
        intent.putExtra("category", item);
        context.startActivity(intent);
    }

    public void imageClickListener(ImageView outerLayer, String outer) {
        outerLayer.setOnClickListener(v -> startShoppingActivity(outer));
    }

    public Suggest getSuggest() {
        return suggest;
    }
}

