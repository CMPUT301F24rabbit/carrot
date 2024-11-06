package com.example.goldencarrot.views;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FacilityRepository {

    private static final String TAG = "FacilityRepository";
    private FirebaseFirestore db;

    public FacilityRepository() {
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
    }

    // Method to load a facility profile by ID
    public void loadFacilityProfile(String facilityID, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference docRef = db.collection("facilities").document(facilityID);
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

    // Method to save a new or existing facility profile
    public void saveFacilityProfile(UserImpl facilityProfile, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference docRef = db.collection("facilities").document(facilityProfile.getFacilityName().orElse("unknown_facility"));
        docRef.set(facilityProfile).addOnCompleteListener(onCompleteListener);
    }

    // Method to update an existing facility profile field
    public void updateFacilityField(String facilityID, String field, Object value, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference docRef = db.collection("facilities").document(facilityID);
        docRef.update(field, value).addOnCompleteListener(onCompleteListener);
    }
}
