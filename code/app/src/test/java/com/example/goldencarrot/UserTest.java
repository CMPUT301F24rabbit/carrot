package com.example.goldencarrot;

import static com.example.goldencarrot.data.model.user.UserUtils.ADMIN_TYPE;
import static com.example.goldencarrot.data.model.user.UserUtils.ORGANIZER_TYPE;

import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;

import com.google.firebase.firestore.FirebaseFirestore;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Optional;

public class UserTest {

    private User newUser;
    private String mockEmail() {
        return "mock@gmail.com";
    }
    private String mockName() {
        return "BugsBunny";
    }
    private FirebaseFirestore db;
    private UserRepository userRepository;
    private int userCollectionSize;
    private User mockUser(String userType) {
        try {
            newUser = new UserImpl(mockEmail(), userType, mockName(), null);
        }
        catch(Exception e){

        }
        return newUser;
    }

    @Test
    void testCreateUserObj_HappyCase() {
        User nUser;
        // test if user is successfully created
        nUser = mockUser(ADMIN_TYPE);
        assertSame(nUser.getEmail(), mockEmail());
        assertSame(nUser.getUserType(), ADMIN_TYPE);
        assertSame(nUser.getName(), mockName());

        nUser = mockUser(ORGANIZER_TYPE);
        assertSame(nUser.getUserType(), ORGANIZER_TYPE);
    }

    @Test
    void testCreateUserObj_InvalidUserType() {
        // test if exception is thrown for invalid user type
        assertThrows(Exception.class, () -> {
            User nUser = new UserImpl(mockEmail(), "InvalidType", mockName(), null);
        });
    }

    @Test
    void testUpdateUserDetails_Success() throws Exception {
        // Create an initial user and userRepository
        User user = mockUser(ADMIN_TYPE);

        // Simulate updating user details
        user = new UserImpl("newemail@gmail.com", ADMIN_TYPE, "NewName", Optional.of("0987654321"));

        // Here you'd typically call the update method on UserRepository
        userRepository.updateUser(user, "mock_android_id"); // Assuming "mock_android_id" is your device ID

        // Assert updated values
        assertEquals("newemail@gmail.com", user.getEmail());
        assertEquals("NewName", user.getName());
        assertEquals("0987654321", user.getPhoneNumber().orElse(null));
    }

}
