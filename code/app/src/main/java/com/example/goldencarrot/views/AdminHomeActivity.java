package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.goldencarrot.R;
import com.example.goldencarrot.controller.RanBackground;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserArrayAdapter;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Displays admin home view
 */
public class AdminHomeActivity extends AppCompatActivity {
    private Button viewAllEventsButton, viewAllUsersButton, viewAllImagesButton;
    private ListView userList, eventList, imageList;
    private FirebaseFirestore firestore;

    // Profile data
    private ArrayList<User> dataUserList;
    private ArrayAdapter<User> userArrayAdapter;
    private UserRepository userRepository;
    private ArrayList<DocumentSnapshot> userListFromDb;

    // Event data
    private ArrayList<String> dataEventsList;
    private CollectionReference eventsCollection;
    private ArrayAdapter<String> eventsAdapter;
    private ArrayList<DocumentSnapshot> eventDocuments;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Apply RNG Background
        ConstraintLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.setBackground(RanBackground.getRandomBackground(this));

        viewAllEventsButton = findViewById(R.id.adminAllEventsButton);
        viewAllUsersButton = findViewById(R.id.adminAllUsersButton);
        viewAllImagesButton = findViewById(R.id.adminAllImagesButton);

        // Display all profiles in sublist
        dataUserList = new ArrayList<>();
        userRepository = new UserRepository();
        userList = findViewById(R.id.admin_home_profiles_list);
        displayProfiles();

        // open specific profile
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent;
                String userToViewId = userListFromDb.get(position).getId();
                intent = new Intent(AdminHomeActivity.this, AdminProfileView.class);
                Log.i(TAG, "clicked on " + userToViewId);
                intent.putExtra("currentUserId", userToViewId);
                startActivity(intent);
            }
        });

        // Display all events in sublist
        eventList = findViewById(R.id.admin_home_events_list);
        dataEventsList = new ArrayList<>();
        displayEvents();

        // Set an item click listener to open EventDetailsActivity
        eventList.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected event document
            DocumentSnapshot selectedDocument = eventDocuments.get(position);
            String documentId = selectedDocument.getId();
            Intent intent = new Intent(AdminHomeActivity.this, EventDetailsAdminActivity.class);
            intent.putExtra("eventId", documentId);
            startActivity(intent);
        });

        // buttons to navigate to independent lists
        viewAllEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open BrowseEventsActivity when the events button is clicked
                Intent intent = new Intent(AdminHomeActivity.this, BrowseEventsActivity.class);
                startActivity(intent);
            }
        });

        viewAllUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // opens all users list
                Intent intent = new Intent(AdminHomeActivity.this, AdminAllProfilesView.class);
                startActivity(intent);
            }
        });

        viewAllImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAllPosterUrls();
            }
        });
    }
    private void fetchAllPosterUrls() {
        // Reference to Firebase Storage folder named "posters"
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("posters");

        storageRef.listAll()
                .addOnSuccessListener(listResult -> {
                    // List to store poster URLs
                    List<String> posterUrls = new ArrayList<>();

                    // Loop through items in the "posters" folder
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            posterUrls.add(uri.toString());

                            // Ensure URLs are processed before displaying them
                            if (posterUrls.size() == listResult.getItems().size()) {
                                displayPosterUrls(posterUrls);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch poster URLs", e);
                    Toast.makeText(AdminHomeActivity.this, "Failed to load posters.", Toast.LENGTH_SHORT).show();
                });
    }
    private void displayPosterUrls(List<String> posterUrls) {
        // Intent to navigate to AdminPosterGalleryActivity
        Intent intent = new Intent(this, AdminPosterGalleryActivity.class);
        intent.putStringArrayListExtra("posterUrls", new ArrayList<>(posterUrls));
        startActivity(intent);
    }

    public void displayProfiles() {
        userRepository.getAllUsersFromFirestore(new UserRepository.FirestoreCallbackAllUsers() {
            @Override
            public void onSuccess(List<DocumentSnapshot> listOfUsers) {
                // cache list from firebase
                Log.i(TAG, "got all users!");
                userListFromDb = new ArrayList<>(listOfUsers);
                // add users from firebase to dataUserList
                getUsersFromFirestore(listOfUsers);
                // set data list in adapter
                userArrayAdapter = new UserArrayAdapter(AdminHomeActivity.this, dataUserList);
                userList.setAdapter(userArrayAdapter);
            }
            @Override
            public void onFailure(Exception e) {
                // handle errors
                Log.i(TAG, "failed to get list of users");
            }
        });
    }
    public void getUsersFromFirestore(List<DocumentSnapshot> listOfUsers) {
        // convert all documents into users
        for (int i = 0; i < listOfUsers.size(); i++) {
            try {
                DocumentSnapshot userFromDb = listOfUsers.get(i);
                User newUser = new UserImpl(userFromDb.getString("email"),
                        userFromDb.getString("userType"),
                        userFromDb.getString("name"), Optional.ofNullable(userFromDb.getString("phoneNumber")),
                        userFromDb.getBoolean("administratorNotification"),
                        userFromDb.getBoolean("organizerNotification"),
                        userFromDb.getString("userProfileImage")
                );
                // add user to user data list
                dataUserList.add(newUser);
                Log.i(TAG, "Successfully added " + userFromDb.getString("username"));
            } catch (Exception e) {
                Log.e(TAG, "Invalid user type, user not added");
            }
        }
    }

    private void displayEvents() {
        // Initialize ArrayAdapter
        eventsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataEventsList);
        eventList.setAdapter(eventsAdapter);

        // Initialize list for event documents
        eventDocuments = new ArrayList<>();

        // Fetch events from Firestore
        loadEventsFromFirestore();
    }
    private void loadEventsFromFirestore() {
        FirebaseFirestore.getInstance().collection("events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataEventsList.clear();
                        eventDocuments.clear(); // Clear previous documents in case of refresh
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String eventName = document.getString("eventName");
                                dataEventsList.add(eventName);
                                eventDocuments.add(document); // Store the document snapshot for later access
                            }
                            eventsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
