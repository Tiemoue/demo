package com.example.easycloset.Application;

import android.app.Application;

import com.example.easycloset.Models.Item;
import com.example.easycloset.Models.Post;
import com.example.easycloset.Models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseUser.registerSubclass(User.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Item.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Ip3Oh4XMzgSUw2HLU5ZcpIBUgoCJU7MXlv8z09qY")
                .clientKey("KaVpIZjMLg9CWtFcfY5FmwQruN2mDcw7nAZJA8lw")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
