package com.example.goldencarrot.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EntrantProfileView extends AppCompatActivity {

    EditText nameInput;
    EditText emailInput;
    EditText phoneInput;
    EditText locationInput;
    private Button deleteAccountButton, disableNotificationsSwitch;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_profile_view);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Link UI elements
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        locationInput = findViewById(R.id.locationInput);
        Button submitButton = findViewById(R.id.submitButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);
        disableNotificationsSwitch = findViewById(R.id.disableNotificationsSwitch);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String phone = phoneInput.getText().toString().trim();
                String location = locationInput.getText().toString().trim();

                // Validate required fields
                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(EntrantProfileView.this, "Name and Email are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Phone number validation
                if (!phone.isEmpty() && !isValidPhoneNumber(phone)) {
                    Toast.makeText(EntrantProfileView.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save data to Firestore
                saveEntrantData(name, email, phone, location);
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
    }

    boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10}");
    }

    void saveEntrantData(String name, String email, String phone, String location) {
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("phone", phone);
        user.put("location", location);

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EntrantProfileView.this, "Information saved successfully!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EntrantProfileView.this, "Error saving information", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void deleteAccount() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Remove user data from Firestore
            db.collection("users").document(userId).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Delete user from Authentication
                            mAuth.getCurrentUser().delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EntrantProfileView.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                            finish(); // Close the activity
                                        } else {
                                            Toast.makeText(EntrantProfileView.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EntrantProfileView.this, "Error deleting account", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
