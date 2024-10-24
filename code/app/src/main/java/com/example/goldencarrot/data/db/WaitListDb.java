package com.example.goldencarrot.data.db;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

/**
 * The {@code WaitListDb} interface defines the contract for performing
 * database operations related to waitlists in Firestore.
 */
public interface WaitListDb {

    /**
     * Creates a new waitlist document in Firestore.
     *
     * @param waitList the waitlist to be created
     * @param docId    the document ID for this waitlist in Firestore
     */
    void createWaitList(WaitList waitList, String docId);

    /**
     * Updates the status of a user in the waitlist document in Firestore.
     *
     * @param docId   the document ID of the waitlist
     * @param user    the user to update
     * @param status  the new status of the user (e.g., "accepted", "rejected", etc.)
     */

    void updateUserStatusInWaitList(String docId, UserImpl user, String status);

    /**
     * Adds a user to the waitlist if there is room.
     *
     * @param docId the document ID of the waitlist
     * @param user the user to be added to the waitlist
     * @param callback a callback that handles the result (true if added, false if the waitlist is full)
     */
    void addUserToWaitList(String docId, UserImpl user, WaitListRepository.FirestoreCallback callback);
    /**
     * Deletes a waitlist document from Firestore.
     *
     * @param docId the document ID of the waitlist to delete
     */
    void deleteWaitList(String docId);

    /**
     * Checks if a user is in the waitlist.
     *
     * @param docId   the document ID of the waitlist
     * @param user    the user to check
     * @param callback a callback that handles the result
     */
    void isUserInWaitList(String docId, UserImpl user, WaitListRepository.FirestoreCallback callback);

    /**
     * Checks the status of a user in the waitlist.
     *
     * @param docId   the document ID of the waitlist
     * @param user    the user to check
     * @param callback a callback that handles the result
     */
    void getUserStatus(String docId, UserImpl user, WaitListRepository.FirestoreCallback callback);
}
