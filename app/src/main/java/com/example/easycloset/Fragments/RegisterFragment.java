package com.example.easycloset.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.easycloset.Activities.MainActivity;
import com.example.easycloset.Models.User;
import com.example.easycloset.R;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RegisterFragment extends Fragment {
    private EditText etUsername;
    private EditText etPassword;
    private EditText etFirstName;
    private EditText etLastName;
    private String genderChoice;
    private EditText etRePassword;


    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnSignUp = view.findViewById(R.id.btnRegister);
        etUsername = view.findViewById(R.id.etSignupUsername);
        etPassword = view.findViewById(R.id.etSignupPassword);
        etFirstName = view.findViewById(R.id.etFirstname);
        etLastName = view.findViewById(R.id.etSignUpLastname);
        etRePassword = view.findViewById(R.id.etSignupRePassword);


        NiceSpinner niceSpinner = view.findViewById(R.id.nice_spinner);
        List<String> dataset = new ArrayList<>(Arrays.asList("Select a Gender", "Male", "Female", "Other"));
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                // This example uses String, but your type can be any
                genderChoice = parent.getItemAtPosition(position).toString();
            }

        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etFirstName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "First name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etLastName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Last name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().isEmpty() || etUsername.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Invalid Username or Password ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(etPassword.getText().toString()).equals(etRePassword.getText().toString())) {
                    Toast.makeText(getContext(), "Password not a match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (genderChoice == null) {
                    Toast.makeText(getContext(), "Select a gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                createAccount(etFirstName, etLastName, etPassword, etUsername, genderChoice);
            }
        });
    }


    private void goToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void createAccount(EditText firstname, EditText lastname, EditText password, EditText username, String gender) {
        User user = new User();
        user.setUsername(String.valueOf(username.getText()));
        user.setPassword(String.valueOf(password.getText()));
        user.setKeyFirstName(String.valueOf(firstname.getText()));
        user.setKeyLastName(String.valueOf(lastname.getText()));
        user.setKeyGender(gender);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    goToMainActivity();
                } else {
                    Log.e("here", e.toString());
                }
            }
        });

    }
}