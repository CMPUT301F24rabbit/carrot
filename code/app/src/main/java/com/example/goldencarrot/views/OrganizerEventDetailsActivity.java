package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.goldencarrot.R;
import com.example.goldencarrot.controller.RanBackground;
import com.example.goldencarrot.controller.WaitListController;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

/**
 * Activity that displays details of an event organized by the user.
 * It fetches event information from Firestore and displays it on the UI.
 * The organizer can also view the waitlisted, accepted, and declined entrants.
 */
public class OrganizerEventDetailsActivity extends AppCompatActivity {

    // Firestore and Event Repository initialization
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private EventRepository eventRepository;
    private String deviceID;
    private String eventId;
    private String facilityName, email, phoneNumber;
    private WaitListRepository waitListRepository;
    private WaitListController waitListController;
    private WaitList waitList;
    private static final int UPDATE_POSTER_REQUEST = 2;
    private Uri newPosterUri;


    // UI Components
    private ImageView eventPosterView;
    private TextView eventNameTextView, eventDateTextView, eventLocationTextView,
            eventDetailsTextView, facilityNameTextView, facilityContactInfoTextView;
    private PopupWindow entrantsPopup;
    private Button selectLotteryButton;
    private ImageView qrCodeImageView;
    private Button generateQRCodeButton;

    /**
     * This method is triggered when the activity is created.
     * It initializes the UI components, sets up Firestore and EventRepository,
     * and retrieves the event ID to load the event details.
     * It also sets up listeners for various buttons and actions in the UI.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Firestore initialization
        firestore = FirebaseFirestore.getInstance();
        eventRepository = new EventRepository();
        waitListRepository = new WaitListRepository();
        List<User> usersWithStatus = new ArrayList<>();

        // Apply RNG Background
        RelativeLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.setBackground(RanBackground.getRandomBackground(this));


        // Get eventID from Intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            loadEventDetails(eventId); // Load event details based on eventId
        } else {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show();
        }

        // UI Initialization
        eventPosterView = findViewById(R.id.eventPosterImageView);
        eventNameTextView = findViewById(R.id.event_DetailNameTitleView);
        eventDateTextView = findViewById(R.id.event_DetailDateView);
        eventLocationTextView = findViewById(R.id.event_DetailLocationView);
        eventDetailsTextView = findViewById(R.id.event_DetailDetailsView);
        facilityNameTextView = findViewById(R.id.event_DetailFacilityName);
        facilityContactInfoTextView = findViewById(R.id.event_DetailContactInfo);

        deviceID = getDeviceId(this);

        // QR code button and image view
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        generateQRCodeButton = findViewById(R.id.generateQRCodeButton);
        Button updatePosterButton = findViewById(R.id.UploadEventPoster);
        updatePosterButton.setOnClickListener(v -> selectNewPosterImage());


        // Set up back button
        Button backButton = findViewById(R.id.back_DetailButton);
        backButton.setOnClickListener(view -> {
            openEntrantHomeView();
        });

        // Hide delete button for organizer
        Button deleteEventBtn = findViewById(R.id.delete_DetailEventBtn);
        deleteEventBtn.setVisibility(View.INVISIBLE);

        // Entrants button: opens a popup showing Entrant options
        Button entrantsButton = findViewById(R.id.button_DetailViewEventLists);
        entrantsButton.setOnClickListener(v -> showEntrantsPopup());

        // Select Lottery Button: triggers the lottery dialog
        selectLotteryButton = findViewById(R.id.button_SelectLotteryUsers);
        // TODO: Implement lottery selection dialog where the organizer can choose
        // the number of users to approve for the event
        // selectLotteryButton.setOnClickListener(v -> showLotteryDialog());

        // Fetch and display QR Code if it exists
        fetchAndDisplayQRCode();

        // Set onClickListener for the Generate QR Code button
        generateQRCodeButton.setOnClickListener(view -> {
            if (eventId == null) {
                Toast.makeText(this, "Please create an event first", Toast.LENGTH_SHORT).show();
                return;
            }
            generateQRCode();
        });

        waitListRepository.getWaitListByEventId(eventId, new WaitListRepository.WaitListCallback() {
            @Override
            public void onSuccess(WaitList waitList) {
                Log.d("OrganizerEventDetails", "Found waitlist with the same" +
                        "event id");

                // Initialize WaitList Controller
                waitListController = new WaitListController(waitList);

            }

            @Override
            public void onFailure(Exception e) {
                // Event is not associated with a waitlist
                Toast.makeText(OrganizerEventDetailsActivity.this,
                        "No such waitlist with the same event Id", Toast.LENGTH_SHORT).show();

                Log.d("OrganizerEventDetails", "Event is not associated with a waitlist" +
                        "delete event in firebase");

                openEntrantHomeView();
            }
        });

        selectLotteryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an EditText for number input
                EditText numberInput = new EditText(view.getContext());
                numberInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER); // Ensures only numbers can be entered

                // Create the dialog
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Pick a Number")
                        .setMessage("Enter the number of lottery winners:")
                        .setView(numberInput) // Add the EditText to the dialog
                        .setPositiveButton("OK", (dialog, which) -> {
                            String input = numberInput.getText().toString();
                            if (!input.isEmpty()) {
                                try {
                                    int pickedNumberToSample = Integer.parseInt(input);
                                    Log.d("LotteryPicker", "Picked number: "
                                            + pickedNumberToSample);

                                    selectLottery(pickedNumberToSample);

                                } catch (NumberFormatException e) {
                                    // Handle invalid input
                                    Log.e("LotteryPicker", "Invalid number entered");
                                }
                            } else {
                                Log.e("LotteryPicker", "No number entered");
                            }
                            openEntrantHomeView();
                        })
                        .setNegativeButton("Cancel", null) // No action on cancel
                        .show();
            }
        });
    }
    /**
     * This method allows the organizer to select a new event poster image.
     * It opens the file picker to choose an image from the device storage.
     */
    private void selectNewPosterImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, UPDATE_POSTER_REQUEST);
    }

    /**
     * This method handles the result of the image selection activity.
     * It updates the preview of the event poster and uploads the selected poster to Firebase Storage.
     *
     * @param requestCode The request code used to identify the activity result.
     * @param resultCode The result code indicating the success or failure of the activity.
     * @param data The data containing the URI of the selected image.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_POSTER_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            newPosterUri = data.getData();
            eventPosterView.setImageURI(newPosterUri); // Update the preview
            uploadUpdatedPoster();
        }
    }

    /**
     * This method uploads the selected event poster to Firebase Storage.
     * It deletes the old poster from storage and uploads the new one, updating the Firestore event document with the new poster URL.
     */
    private void uploadUpdatedPoster() {
        if (newPosterUri == null || eventId == null) {
            Toast.makeText(this, "No poster selected or event ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Define paths
        String oldPosterPath = "posters/" + eventId + "_poster.jpg"; // Old poster path
        String newPosterPath = "posters/" + eventId + "_updated_poster.jpg"; // New poster path

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference oldPosterRef = storageRef.child(oldPosterPath);
        StorageReference newPosterRef = storageRef.child(newPosterPath);

        // Delete old poster
        oldPosterRef.delete().addOnSuccessListener(aVoid -> {
            Log.d("PosterUpdate", "Old poster deleted successfully.");
        }).addOnFailureListener(e -> {
            Log.w("PosterUpdate", "Old poster not found or couldn't be deleted.", e);
        });

        // Upload new poster
        newPosterRef.putFile(newPosterUri)
                .addOnSuccessListener(taskSnapshot -> newPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String updatedPosterUrl = uri.toString();

                    // Update Firestore document with new poster URL
                    firestore.collection("events").document(eventId)
                            .update("posterUrl", updatedPosterUrl)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Poster updated successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update poster in Firestore.", Toast.LENGTH_SHORT).show());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload new poster.", Toast.LENGTH_SHORT).show());
    }

    /**
     * This method retrieves the device ID of the current device.
     *
     * @param context The context of the current activity.
     * @return The device ID as a string.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * This method loads the event details from Firestore based on the provided event ID.
     * It listens for changes to the event document and updates the UI accordingly.
     *
     * @param eventId The ID of the event to load.
     */
    private void loadEventDetails(String eventId) {
        DocumentReference eventRef = firestore.collection("events").document(eventId);
        listenerRegistration = eventRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error fetching event details", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                String organizerId = snapshot.getString("organizerId");

                if (organizerId != null && organizerId.equals(deviceID)) {
                    String eventName = snapshot.getString("eventName");
                    String eventDetails = snapshot.getString("eventDetails");
                    String location = snapshot.getString("location");
                    String date = snapshot.getString("date");
                    String posterUrl = snapshot.getString("posterUrl"); // Retrieve the poster URL

                    getFacilityInfo(organizerId);
                    eventNameTextView.setText(eventName);
                    eventDateTextView.setText("Date: " + date);
                    eventLocationTextView.setText("Location: " + location);
                    eventDetailsTextView.setText(eventDetails);

                    // Load the poster image from Firebase Storage using Glide
                    if (posterUrl != null && !posterUrl.isEmpty()) {
                        Picasso.get()
                                .load(posterUrl)
                                .placeholder(R.drawable.poster_placeholder) // Default placeholder image
                                .error(R.drawable.poster_error) // Error placeholder
                                .into(eventPosterView);
                    } else {
                        eventPosterView.setImageResource(R.drawable.poster_placeholder);
                    }
                } else {
                    Toast.makeText(this, "Access denied: You are not authorized to view this event", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                openEntrantHomeView();
            }
        });
    }

    /**
     * Generates a QR code for the created event, encoding the event's details such as name,
     * location, date, and description. Displays the QR code in an ImageView.
     */
    private void generateQRCode() {
        if (eventId == null) {
            Toast.makeText(this, "Please create an event first or ensure it has an ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if a QR code for this event already exists
        firestore.collection("QRData")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // QR code already exists
                            Toast.makeText(this, "QR Code already exists for this event.", Toast.LENGTH_SHORT).show();

                            // Retrieve existing QR content
                            String existingQrContent = task.getResult().getDocuments().get(0).getString("qrContent");

                            // Generate the QR code bitmap for display
                            displayQRCode(existingQrContent);
                        } else {
                            // No existing QR code, generate a new one
                            createAndSaveQRCode(firestore);
                        }
                    } else {
                        Toast.makeText(this, "Error checking for existing QR Code: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * This method creates and saves a new QR code for the event.
     * It generates a unique QR code content, stores it in Firestore, and then displays it.
     *
     * @param firestore The Firestore instance used for saving the QR data.
     */
    private void createAndSaveQRCode(FirebaseFirestore firestore) {
        // Generate new QR content
        String qrContent = "goldencarrot://eventDetails?eventId=" + eventId;

        // Save QR content to Firestore
        Map<String, Object> qrData = new HashMap<>();
        qrData.put("eventId", eventId);
        qrData.put("qrContent", qrContent);

        firestore.collection("QRData")
                .add(qrData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "QR Code data saved to Firestore", Toast.LENGTH_SHORT).show();
                    // Display the newly generated QR code
                    displayQRCode(qrContent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * This method displays the generated QR code in the QR Code ImageView.
     *
     * @param qrContent The content of the QR code to display.
     */
    private void displayQRCode(String qrContent) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 400, 400);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method fetches and displays the QR code for the event from Firestore if it exists.
     * If no QR code is found, it notifies the user and allows them to generate a new one.
     */
    private void fetchAndDisplayQRCode() {
        if (eventId == null) {
            Toast.makeText(this, "No event ID provided.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query Firestore for an existing QR code
        firestore.collection("QRData")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // QR code exists
                        String qrContent = task.getResult().getDocuments().get(0).getString("qrContent");
                        if (qrContent != null) {
                            displayQRCode(qrContent);
                            Toast.makeText(this, "QR Code loaded successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No QR Code data found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No existing QR Code. You can generate one.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching QR Code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    /**
     * This method shows a popup with buttons for viewing the different types of entrants for the event.
     * It provides options to view waitlisted, chosen, declined, and accepted entrants.
     */
    private void showEntrantsPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_event_lists, null);

        entrantsPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        entrantsPopup.showAtLocation(findViewById(R.id.button_DetailViewEventLists), Gravity.CENTER, 0, 0);

        Button waitlistedButton = popupView.findViewById(R.id.button_EventDetailWaitlistedEntrants);
        Button chosenButton = popupView.findViewById(R.id.button_EventDetailChosenEntrants);
        Button declinedButton = popupView.findViewById(R.id.button_EventDetailRejectedEntrants);
        Button acceptedButton = popupView.findViewById(R.id.button_EventDetailAcceptedEntrants);

        waitlistedButton.setOnClickListener(v -> openEntrantsView(UserUtils.WAITING_STATUS));
        chosenButton.setOnClickListener(v -> openEntrantsView(UserUtils.CHOSEN_STATUS));
        declinedButton.setOnClickListener(v -> openEntrantsView(UserUtils.CANCELLED_STATUS));
        acceptedButton.setOnClickListener(v -> openEntrantsView(UserUtils.ACCEPTED_STATUS));
    }

    /**
     * This method opens a new activity to view the entrants of a specific status.
     * It passes the status (e.g., WAITING, CHOSEN) and the event ID to the next screen.
     *
     * @param status The status of entrants to be displayed (e.g., WAITING, CHOSEN).
     */
    private void openEntrantsView(String status) {
        Intent intent = new Intent(OrganizerEventDetailsActivity.this, OrganizerWaitlistView.class);
        intent.putExtra("entrantStatus", status);
        intent.putExtra("eventId", eventId);
        entrantsPopup.dismiss();
        startActivity(intent);
    }

    /**
     * This method opens the entrant home view activity for the organizer.
     */
    private void openEntrantHomeView(){
        Intent intent = new Intent(OrganizerEventDetailsActivity.this,
                OrganizerHomeView.class);
        startActivity(intent);
    }

    /**
     * This method selects random winners from the waitlist for the event based on the specified count.
     * It updates the status of the selected winners and updates the waitlist in the database.
     *
     * @param count The number of random winners to select.
     */
    private void selectLottery(int count){
        try {
            // select random winners from waitlist object
            waitListController.selectRandomWinnersAndUpdateStatus(count);
            // Update the Waitlist document in waitlist DB
            waitListRepository.updateWaitListInDatabase(waitListController.getWaitList());

            Toast.makeText(OrganizerEventDetailsActivity.this,
                    "Successfully picked winners randomly", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(OrganizerEventDetailsActivity.this,
                    "Not enough users in the waiting list", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *This method gets the information about facility profile
     * @param organizerId The organizer ID of user who created with facility profile
     */
    private void getFacilityInfo(String organizerId) {
        firestore.collection("users").document(organizerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String facilityName = documentSnapshot.getString("facilityName");
                        String contactInfo = documentSnapshot.getString("contactInfo");

                        facilityNameTextView.setText("Facility: " + facilityName);
                        facilityContactInfoTextView.setText("Contact Info:\n" + contactInfo);
                    }
                });
    }
}
