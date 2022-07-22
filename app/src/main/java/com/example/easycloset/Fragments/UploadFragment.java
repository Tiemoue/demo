package com.example.easycloset.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.easycloset.Activities.MainActivity;
import com.example.easycloset.Models.Item;
import com.example.easycloset.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

public class UploadFragment extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 20;
    public static String TAG = ".CameraActivity";
    private EditText etColour;
    private ImageView ivPicture;
    private File photoFile;
    private MainActivity activity;
    private String category;

    public UploadFragment() {
    }

    public UploadFragment(MainActivity mainActivity) {
        activity = mainActivity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnTakePicture = view.findViewById(R.id.btnTakePicture);
        FloatingActionButton btnUpload = view.findViewById(R.id.floatingUploadAction);
        etColour = view.findViewById(R.id.etColour);
        ivPicture = view.findViewById(R.id.ivPicture);
        Spinner spCategory = view.findViewById(R.id.spCategory);
        ImageButton btnCloset = view.findViewById(R.id.btCloset);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.category, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spCategory.setAdapter(adapter);

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnTakePicture.setOnClickListener(v -> launchCamera());

        btnCloset.setOnClickListener(v -> activity.setFragmentContainer(activity.getClosetFragment()));

        btnUpload.setOnClickListener(v -> {
            String colour = etColour.getText().toString();
            if (colour.isEmpty()) {
                Toast.makeText(getContext(), "Colour cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (photoFile != null && ivPicture.getDrawable() != null && currentUser != null) {
                saveItem(colour, category, photoFile, currentUser);
            } else {
                Toast.makeText(getContext(), "no image found", Toast.LENGTH_SHORT).show();
            }
        });
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
                ivPicture.setImageBitmap(takenImage);
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

    @SuppressLint("NotifyDataSetChanged")
    private void saveItem(String colour, String category, File photoFile, ParseUser currentUser) {
        Item item = new Item();
        item.setColour(colour);
        item.setUser(currentUser);
        item.setCategory(category.toLowerCase());
        item.setImage(new ParseFile(photoFile));
        item.saveInBackground(e -> {
            if (e != null) {
                Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
            } else {
                etColour.setText("");
                ivPicture.setImageResource(0);
                Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                activity.getClosetFragment().getAllItems().add(0, item);
                activity.getClosetFragment().getAdapter().notifyDataSetChanged();
                activity.setFragmentContainer(activity.getClosetFragment());
            }
        });
    }


}