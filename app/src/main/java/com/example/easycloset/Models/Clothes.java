package com.example.easycloset.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Clothes {


    private String title;
    private String price;
    private String link;
    private String image;
    private String merchant;

    public Clothes() {
    }

    public Clothes(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString("title");
        price = jsonObject.getString("price");
        link = jsonObject.getString("link");
        image = jsonObject.getString("thumbnail");
        merchant = jsonObject.getString("source");
    }

    public static List<Clothes> fromJsonArray(JSONArray clothesJsonArray) throws JSONException {
        List<Clothes> clothing = new ArrayList<>();
        for (int i = 0; i < clothesJsonArray.length(); i++) {
            clothing.add(new Clothes(clothesJsonArray.getJSONObject(i)));
        }
        return clothing;
    }

    public Clothes addOne(JSONArray clothesArray) throws JSONException {
        Clothes clothes = new Clothes();
        for (int i = 0; i < 1; i++) {
            clothes = new Clothes(clothesArray.getJSONObject(i));
        }
        return clothes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }
}
