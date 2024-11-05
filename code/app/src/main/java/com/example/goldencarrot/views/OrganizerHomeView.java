package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.event.EventRecyclerArrayAdapter;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OrganizerHomeView extends AppCompatActivity {

    private Button manageProfileButton;
    private TextView usernameTextView;
    private RecyclerView recyclerView;
    private EventRecyclerArrayAdapter eventAdapter;

    // Firestore
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_home_view);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialized");

        // Initialize the views from layout file
        manageProfileButton = findViewById(R.id.button_manage_profile);
        usernameTextView = findViewById(R.id.organizer_user_name_textView);

        // Set user name
        loadUserData();

        // Event lists and adapter Inititalization
        recyclerView = findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Set adapters to listview

        // Placeholder for event recycler for images
        // Create a list of Event objects for the RecyclerView

        // Set an OnClickListener for the button
        manageProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ManageProfileActivity
                Intent intent = new Intent(OrganizerHomeView.this, OrganizerManageProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    // Load user Data
    private void loadUserData() {
        String deviceId = getDeviceId(OrganizerHomeView.this);

        firestore.collection("users").document(deviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Manually construct UserImpl with data from Firestore to handle Optional fields
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String userType = documentSnapshot.getString("userType");
                        String phoneNumber = documentSnapshot.getString("phoneNumber"); // Firestore stores as String

                        // Phone number to optional string
                        Optional<String> optionalPhoneNumber = (phoneNumber != null && !phoneNumber.isEmpty())
                                ? Optional.of(phoneNumber)
                                : Optional.empty();
                        try {
                            UserImpl user = new UserImpl(email, userType, name, optionalPhoneNumber);
                            if (user.getName() != null) {
                                usernameTextView.setText(user.getName());
                                Log.d(TAG, "Username loaded: " + user.getName());

                                // LOADING DUMMY EVENTS
                                List<Event> eventList = Arrays.asList(
                                        new Event(user,"Movie Night", "Cinema", new Date(), "Join us for a night of fun movies!", R.drawable.movie),
                                        new Event(user, "Food Festival", "Downtown", new Date(), "Enjoy delicious cuisines from around the world.", R.drawable.dimsum),
                                        new Event(user, "Live Concert", "Stadium", new Date(), "Experience live music with popular bands.", R.drawable.profilepic1),
                                        new Event(user, "Art Exhibition", "Art Gallery", new Date(), "Explore the latest art pieces by renowned artists.", R.drawable.movie)
                                );
                                // Initialize adaptr with createdeventlist
                                eventAdapter = new EventRecyclerArrayAdapter(OrganizerHomeView.this, eventList);
                                recyclerView.setAdapter(eventAdapter);



                            } else {
                                Log.w(TAG, "Username field is missing in the document");
                                usernameTextView.setText("Error: Username not found");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error creating UserImpl object: " + e.getMessage(), e);
                            usernameTextView.setText("Error loading user data");
                        }
                    } else {
                        Log.e(TAG, "Document does not exist");
                        usernameTextView.setText("Error: User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data", e);
                    usernameTextView.setText("Error fetching user data");

                });

    }

    /**
     * Retrieves the Android device ID.
     *
     * @param context The application context.
     * @return The device ID as a string.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
