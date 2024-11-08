package com.example.goldencarrot.data.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.goldencarrot.data.model.user.FacilityUserImpl;
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

    /**
     * Load a facility profile based on facility ID.
     *
     * @param facilityID The ID of the facility to load.
     * @param onCompleteListener Listener to handle completion of the loading operation.
     */
    public void loadFacilityProfile(String facilityID, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference docRef = db.collection("facilities").document(facilityID);
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

    /**
     * Save or update a facility profile in Firestore.
     *
     * @param facilityProfile The FacilityUserImpl instance containing facility profile data.
     * @param onCompleteListener Listener to handle completion of the saving operation.
     */
    public void saveFacilityProfile(FacilityUserImpl facilityProfile, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference docRef = db.collection("facilities").document(facilityProfile.getUserId());

        docRef.set(facilityProfile).addOnCompleteListener(onCompleteListener);
    }

    /**
     * Update specific fields in an existing facility profile document.
     *
     * @param facilityID The ID of the facility to update.
     * @param field The name of the field to update.
     * @param value The new value for the field.
     * @param onCompleteListener Listener to handle completion of the update operation.
     */
    public void updateFacilityField(String facilityID, String field, Object value, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference docRef = db.collection("facilities").document(facilityID);
        docRef.update(field, value).addOnCompleteListener(onCompleteListener);
    }
}
