package com.example.easycloset.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.easycloset.Activities.MainActivity;
import com.example.easycloset.Models.Weather;
import com.example.easycloset.Queries;
import com.example.easycloset.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class SearchFragment extends Fragment {

    private MainActivity activity;
    private String location;
    private TextView temp;
    private TextView des;
    private ImageView baseLayer, outerLayer, feet, pants;
    private TextView tvOuter, tvBase, tvFeet, tvBottom;

    public SearchFragment() {
        // Required empty public constructor
    }

    public SearchFragment(MainActivity mainActivity) {
        this.activity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView city = view.findViewById(R.id.tvSearchLocation);
        temp = view.findViewById(R.id.tvSearchWeather);
        des = view.findViewById(R.id.searchDes);
        city.setText(location);
        fetchWeatherData();

        baseLayer = view.findViewById(R.id.ivSearchBaseLayer);
        outerLayer = view.findViewById(R.id.ivSearchOuterLayer);
        feet = view.findViewById(R.id.ivSearchFeet);
        pants = view.findViewById(R.id.ivSearchPants);
        tvOuter = view.findViewById(R.id.tvSearchOuterLayer);
        tvBase = view.findViewById(R.id.tvSearchBaseLayer);
        tvFeet = view.findViewById(R.id.tvSearchFeet);
        tvBottom = view.findViewById(R.id.tvSearchBottomLayer);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void query(Weather weather) {
        Queries queries = new Queries(outerLayer, baseLayer, pants, feet, tvOuter, tvBase, tvBottom, tvFeet, getContext());
        queries.checkWeather(weather);
    }

    public void fetchWeatherData() {
        AsyncHttpClient client = new AsyncHttpClient();
        String apiByCity = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&units=imperial&appid=" + R.string.weather_Api_key;
        client.get(apiByCity, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    Weather weather = new Weather(jsonObject);
                    temp.setText(String.format("%s℉", weather.getTemp()));
                    des.setText(weather.getCast());
                    query(weather);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("response from server", "error");
            }
        });
    }
}