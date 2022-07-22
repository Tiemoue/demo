package com.example.easycloset.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easycloset.R;
import com.parse.ParseUser;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private final Timer timer = new Timer();
    private boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (active) {
                    if (ParseUser.getCurrentUser() == null) {
                        Intent intent = new Intent(SplashScreenActivity.this, FirstActivity.class);
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }
            }
        }, 3000);
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}