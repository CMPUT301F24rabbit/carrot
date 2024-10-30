package com.example.goldencarrot;

import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

public class AdminTest {
    private User newAdmin;
    private String mockEmail() {
        return "mock@gmail.com";
    }
    private String mockName() {
        return "BugsBunny";
    }
    private FirebaseFirestore db;
    private UserRepository userRepository;
    private int userCollectionSize;
    private User mockUser() {
        try {
            newAdmin = new UserImpl(mockEmail(), "ADMIN", mockName(), null);
        }
        catch(Exception e){

        }
        return newAdmin;
    }
}
