package com.example.easycloset.Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.parse.Parse.getApplicationContext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.easycloset.Activities.MainActivity;
import com.example.easycloset.Models.Weather;
import com.example.easycloset.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import okhttp3.Headers;


public class HomeFragment extends Fragment {

    private Weather weather;
    private TextView tvCity, tvCountry, tvTemp, tvForecast, tvHumidity, tvMinTemp, tvMaxTemp, tvSunrise, tvSunset;
    private ProgressDialog progressDialog;
    private double latitude;
    private double longitude;
    private MainActivity activity;
    private RelativeLayout searchbar;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    public HomeFragment() {
    }

    public HomeFragment(MainActivity mainActivity) {
        activity = mainActivity;

    }

    @Override
    public void onResume() {
        super.onResume();
        populateHomeFragment();
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Fetching Weather...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        tvCity = view.findViewById(R.id.tvCity);
        tvCountry = view.findViewById(R.id.tvCountry);
        tvTemp = view.findViewById(R.id.tvTemp);
        tvForecast = view.findViewById(R.id.tvForecast);
        tvHumidity = view.findViewById(R.id.tvHumidity);
        tvMinTemp = view.findViewById(R.id.tvMinTemp);
        tvMaxTemp = view.findViewById(R.id.tvMaxTemp);
        tvSunrise = view.findViewById(R.id.tvSunrises);
        tvSunset = view.findViewById(R.id.tvSunsets);
        view.findViewById(R.id.homeScreen);
        String googleKey = String.valueOf(R.string.google_maps_api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), googleKey);
        }

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(requireContext());
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME)).setTypeFilter(TypeFilter.CITIES);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i("here", "Place: " + place.getName() + ", " + place.getId());
                String location = place.getName();
                goToSearchFragment(location);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("here", "An error occurred: " + status);
            }
        });
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Weather getWeather() {
        return weather;
    }

    public void populateHomeFragment() {
        AsyncHttpClient client = new AsyncHttpClient();
        String apiByLat = "https://api.openweathermap.org/data/2.5/weather?lat=" + getLatitude() + "&lon=" + getLongitude() + "&units=imperial&appid=" + R.string.weather_Api_key;

        client.get(apiByLat, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    progressDialog.dismiss();
                    weather = new Weather(jsonObject);
                    setWeather(weather);
                    tvCity.setText(weather.getCityName());
                    tvCountry.setText(weather.getCountryName());
                    tvForecast.setText(weather.getCast());
                    tvTemp.setText(String.format("%s℉", weather.getTemp()));
                    tvHumidity.setText(weather.getHumidity());
                    tvMinTemp.setText(weather.getTempMin());
                    tvMaxTemp.setText(weather.getTempMax());
                    tvSunrise.setText(weather.getSunrise());
                    tvSunset.setText(weather.getSunset());

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = null;
                if (data != null) {
                    place = Autocomplete.getPlaceFromIntent(data);
                }
                Log.i("here", "Place: " + place.getName() + ", " + place.getId());
                String location = place.getName();
                goToSearchFragment(location);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = null;
                if (data != null) {
                    status = Autocomplete.getStatusFromIntent(data);
                }
                Log.i("here", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void goToSearchFragment(String place) {
        activity.getSearchFragment().setLocation(place);
        activity.setFragmentContainer(activity.getSearchFragment());
    }
}