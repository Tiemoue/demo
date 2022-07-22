package com.example.easycloset.Fragments;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.easycloset.Activities.ClothesActivity;
import com.example.easycloset.Activities.MainActivity;
import com.example.easycloset.Models.Suggest;
import com.example.easycloset.Models.Weather;
import com.example.easycloset.Queries;
import com.example.easycloset.R;
import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class SuggestFragment extends Fragment {

    private MainActivity activity;
    private ImageView baseLayer, outerLayer, feet, pants;
    private TextView tvOuter, tvBase, tvFeet, tvBottom;
    private ProgressDialog progressDialog;
    private Suggest suggestions = new Suggest();
    private boolean shouldFetch = false;
    private Queries queries;

    @Override
    public void onResume() {
        super.onResume();
        if (shouldFetch) {
            fetchData();
        }
    }

    public SuggestFragment() {
        // Required empty public constructor
    }

    public SuggestFragment(MainActivity mainActivity) {
        activity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_suggest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        verifyStoragePermission(activity);

        Weather suggestWeather = activity.getHomeFragment().getWeather();

        TextView tvTemp = view.findViewById(R.id.tvWeatherTemp);
        tvTemp.setText(String.format("%s℉", suggestWeather.getTemp()));
        TextView location = view.findViewById(R.id.location);
        location.setText(suggestWeather.getCityName());
        TextView main = view.findViewById(R.id.main);
        main.setText(suggestWeather.getCast());

        baseLayer = view.findViewById(R.id.ivBaseLayer);
        outerLayer = view.findViewById(R.id.ivOuterLayer);
        feet = view.findViewById(R.id.ivFeet);
        pants = view.findViewById(R.id.ivPants);
        tvOuter = view.findViewById(R.id.tvOuterLayer);
        tvBase = view.findViewById(R.id.tvBaseLayer);

        tvFeet = view.findViewById(R.id.tvFeet);
        tvBottom = view.findViewById(R.id.tvBottomLayer);
        progressDialog = new ProgressDialog(getContext());


        FloatingActionButton btnShare = view.findViewById(R.id.floatingShareAction);
        queries = new Queries(outerLayer, baseLayer, pants, feet, tvOuter, tvBase, tvBottom, tvFeet, getContext());
        if (!(shouldFetch)) {
            queries.checkWeather(suggestWeather);
            shouldFetch = true;
            setSuggestions(queries.getSuggest());
        }

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenShot(activity.getWindow().getDecorView());
            }
        });

    }


    public void setSuggestions(Suggest suggestions) {
        this.suggestions = suggestions;
    }

    public void fetchData() {
        openShoppingActivity();
        if (suggestions.getOuterColor() != null) {
            Glide.with(requireContext()).load(suggestions.getOuterImgUrl()).into(outerLayer);
            queries.setText(tvOuter, suggestions.getOuterColor() + " " + suggestions.getOuter());
        } else {
            queries.getItem(suggestions.getOuter(), outerLayer);
        }

        if (suggestions.getBaseColor() != null) {
            Glide.with(requireContext()).load(suggestions.getBaseImgUrl()).into(baseLayer);
            queries.setText(tvBase, suggestions.getBaseColor() + " " + suggestions.getBase());
        } else {
            queries.getItem(suggestions.getBase(), baseLayer);

        }

        if (suggestions.getBottomColor() != null) {
            Glide.with(requireContext()).load(suggestions.getBottomImgUrl()).into(pants);
            queries.setText(tvBottom, suggestions.getBottomColor() + " " + suggestions.getBottom());
        } else {
            queries.getItem(suggestions.getBottom(), pants);

        }

        if (suggestions.getFeetColor() != null) {
            Glide.with(requireContext()).load(suggestions.getFeetImgUrl()).into(feet);
            queries.setText(tvFeet, suggestions.getFeetColor() + " " + suggestions.getFeet());
        } else {
            queries.getItem(suggestions.getFeet(), feet);

        }
    }

    public void makeBtn(ImageView outerLayer, String outer) {
        outerLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startClothes(outer);
            }
        });
    }

    public void startClothes(String item) {
        Intent intent = new Intent(requireContext(), ClothesActivity.class);
        intent.putExtra("category", item);
        requireContext().startActivity(intent);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static void verifyStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public void openShoppingActivity() {
        queries.imageClickListener(feet, suggestions.getFeet());
        queries.imageClickListener(outerLayer, suggestions.getOuter());
        queries.imageClickListener(baseLayer, suggestions.getBase());
        queries.imageClickListener(pants, suggestions.getBottom());
    }

    public void setupFacebookShare(Bitmap bitmap) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        ShareDialog shareDialog = new ShareDialog(this);

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setCaption("TESTING")
                .build();

        if (ShareDialog.canShow(SharePhotoContent.class)) {
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            ShareDialog.show(activity, content);
        }
    }

    private void takeScreenShot(View view) {
        Date date = new Date();
        CharSequence format = android.text.format.DateFormat.format("MM-dd-yyyy_hh:mm:ss", date);
        try {
            File mainDir = new File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FilShare");
            if (!mainDir.exists()) {
                boolean mkdir = mainDir.mkdir();
            }
            String path = mainDir + "/" + "TrendOceans" + "-" + format + ".jpeg";
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            File imageFile = new File(path);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(activity, "Took a screen shot", Toast.LENGTH_SHORT).show();
            ; // Your image file
            String filePath = imageFile.getPath();
            Bitmap bitmap2 = BitmapFactory.decodeFile(filePath);
            setupFacebookShare(bitmap2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shareScreenShot(File imageFile) {
        Uri uri = FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider.easycloset", imageFile);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Recommedations by EasyCloset");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        Intent chooser = Intent.createChooser(intent, "Share File");

        List<ResolveInfo> resInfoList = getContext().getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getContext().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivity(chooser);
    }
}