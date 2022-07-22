package com.example.easycloset.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.easycloset.Activities.MainActivity;
import com.example.easycloset.Models.Item;
import com.example.easycloset.Models.User;
import com.example.easycloset.Models.Weather;
import com.example.easycloset.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    MainActivity activity;
    TextView tvJacket, tvCoats, tvSweater, tvShirt, tvHodies, tvPants, tvJoggers, tvShorts, tvSneaker, tvBoots, tvSlides, tvItems, tvName;
    int bootsCount, jacketCount, coatsCount, sweaterCount, hoodiesCount, shirtCount, pantsCount, joggerCount, sneakerCount, shortsCount, slidesCount, itemCount = 0;
    TextView tvProfileCity;
    CircleImageView ivProfileImg;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 20;
    public static String TAG = ".CameraActivity";
    Button button;
    private File photoFile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(MainActivity mainActivity) {
        activity = mainActivity;

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvBoots = view.findViewById(R.id.tvBootsCount);
        tvJacket = view.findViewById(R.id.tvJacketCount);
        tvCoats = view.findViewById(R.id.tvCoatsCount);
        tvSweater = view.findViewById(R.id.tvSweaterCount);
        tvShirt = view.findViewById(R.id.tvTshirtCount);
        tvHodies = view.findViewById(R.id.tvHoodiesCount);
        tvPants = view.findViewById(R.id.tvPantsCount);
        tvJoggers = view.findViewById(R.id.tvJoggersCount);
        tvSneaker = view.findViewById(R.id.tvSneakerCount);
        tvShorts = view.findViewById(R.id.tvShortsCount);
        tvSlides = view.findViewById(R.id.tvSlidesCount);
        tvItems = view.findViewById(R.id.itemsCount);
        tvProfileCity = view.findViewById(R.id.tvProfileCity);
        ivProfileImg = view.findViewById(R.id.ibProfileImg);
        tvName = view.findViewById(R.id.tvUsername);

        Weather weather = activity.getHomeFragment().getWeather();
        String city = weather.getCityName() + ", " + weather.getCountryName();
        tvProfileCity.setText(city);

        setText();
        queryPosts();
        User user = (User) User.getCurrentUser();
        if (user != null && user.getImage() != null) {
            Glide.with(this).load(user.getImage().getUrl()).transform(new RoundedCorners(90)).into(ivProfileImg);
        }
        assert user != null;
        String name = user.getKeyFirstName() + ", " + user.getKeyLastName();
        tvName.setText(name);


        ivProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void queryPosts() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.include(Item.KEY_USER);
        query.whereEqualTo(Item.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground((items, e) -> {
            // check for errors
            if (e != null) {
                return;
            }
            reCount();
            itemCount = items.size();
            tvItems.setText("Items " + itemCount);
            for (Item item : items) {
                if (item.getCategory().toLowerCase().equals("jacket")) {
                    jacketCount++;
                } else if (item.getCategory().toLowerCase().equals("sweater")) {
                    sweaterCount++;
                } else if (item.getCategory().toLowerCase().equals("t-shirt")) {
                    shirtCount++;
                } else if (item.getCategory().toLowerCase().equals("coat")) {
                    coatsCount++;
                } else if (item.getCategory().toLowerCase().equals("boots")) {
                    bootsCount++;
                } else if (item.getCategory().toLowerCase().equals("hoodie")) {
                    hoodiesCount++;
                } else if (item.getCategory().toLowerCase().equals("joggers")) {
                    joggerCount++;
                } else if (item.getCategory().toLowerCase().equals("sneakers")) {
                    sneakerCount++;
                } else if (item.getCategory().toLowerCase().equals("shorts")) {
                    shortsCount++;
                } else if (item.getCategory().toLowerCase().equals("pants")) {
                    pantsCount++;

                } else {
                    slidesCount++;
                }

            }
            setText();

            // save received posts to list and notify adapter of new data

        });
    }

    public void setText() {
        tvBoots.setText(String.valueOf(bootsCount));
        tvJacket.setText(String.valueOf(jacketCount));
        tvCoats.setText(String.valueOf(coatsCount));
        tvSweater.setText(String.valueOf(sweaterCount));
        tvShirt.setText(String.valueOf(shirtCount));
        tvHodies.setText(String.valueOf(hoodiesCount));
        tvPants.setText(String.valueOf(pantsCount));
        tvJoggers.setText(String.valueOf(joggerCount));
        tvSneaker.setText(String.valueOf(sneakerCount));
        tvShorts.setText(String.valueOf(shortsCount));
        tvSlides.setText(String.valueOf(slidesCount));
    }

    public void reCount() {
        bootsCount = 0;
        jacketCount = 0;
        coatsCount = 0;
        sweaterCount = 0;
        hoodiesCount = 0;
        shirtCount = 0;
        pantsCount = 0;
        joggerCount = 0;
        sneakerCount = 0;
        shortsCount = 0;
        slidesCount = 0;
    }


    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        String photoFileName = "photo.jpg";
        photoFile = getPhotoFileUri(photoFileName);
        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider.easycloset", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                Glide.with(requireContext()).load(takenImage).transform(new RoundedCorners(90)).into(ivProfileImg);
                User user = (User) User.getCurrentUser();
                user.setImage(new ParseFile(photoFile));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getPhotoFileUri(String photoFileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + photoFileName);
    }

    public void dialogBox() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Take Photo")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        launchCamera();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }
}