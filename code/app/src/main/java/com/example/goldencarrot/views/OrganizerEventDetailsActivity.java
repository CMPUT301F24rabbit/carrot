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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.goldencarrot.R;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Firestore initialization
        firestore = FirebaseFirestore.getInstance();
        eventRepository = new EventRepository();
        waitListRepository = new WaitListRepository();
        List<User> usersWithStatus = new ArrayList<>();

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
    private void selectNewPosterImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, UPDATE_POSTER_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_POSTER_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            newPosterUri = data.getData();
            eventPosterView.setImageURI(newPosterUri); // Update the preview
            uploadUpdatedPoster();
        }
    }
    private void uploadUpdatedPoster() {
        if (newPosterUri == null || eventId == null) {
            Toast.makeText(this, "No poster selected or event ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        String newPosterPath = "posters/" + eventId + "_updated_poster.jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(newPosterPath);

        storageRef.putFile(newPosterUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String updatedPosterUrl = uri.toString();

                    // Update the Firestore document
                    firestore.collection("events").document(eventId)
                            .update("posterUrl", updatedPosterUrl)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Poster updated successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update poster in Firestore.", Toast.LENGTH_SHORT).show());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload poster.", Toast.LENGTH_SHORT).show());
    }



    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

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
                        Glide.with(this)
                                .load(posterUrl)
                                .placeholder(R.drawable.poster_placeholder) // Default placeholder image
                                .error(R.drawable.poster_placeholder) // Error placeholder
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

    private void displayQRCode(String qrContent) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 400, 400);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }

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

    private void openEntrantsView(String status) {
        Intent intent = new Intent(OrganizerEventDetailsActivity.this, OrganizerWaitlistView.class);
        intent.putExtra("entrantStatus", status);
        intent.putExtra("eventId", eventId);
        entrantsPopup.dismiss();
        startActivity(intent);
    }

    private void openEntrantHomeView(){
        Intent intent = new Intent(OrganizerEventDetailsActivity.this,
                OrganizerHomeView.class);
        startActivity(intent);
    }

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
