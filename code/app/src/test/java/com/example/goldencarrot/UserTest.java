package com.example.goldencarrot;

import static com.example.goldencarrot.data.model.user.UserUtils.ADMIN_TYPE;
import static com.example.goldencarrot.data.model.user.UserUtils.ORGANIZER_TYPE;
import static com.example.goldencarrot.data.model.user.UserUtils.PARTICIPANT_TYPE;
import static com.example.goldencarrot.data.model.user.UserUtils.USER_TYPE;
import static com.example.goldencarrot.data.model.user.UserUtils.invalidUserTypeException;

import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

public class UserTest {

    private User newUser;
    private String mockEmail() {
        return "mock@gmail.com";
    }
    private String mockUsername() {
        return "BugsBunny";
    }
    private User mockUser(String userType) {
        try {
            newUser = new UserImpl(mockEmail(), userType, mockUsername());
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
        assertSame(nUser.getUsername(), mockUsername());

        nUser = mockUser(ORGANIZER_TYPE);
        assertSame(nUser.getUserType(), ORGANIZER_TYPE);
    }

    @Test
    void testCreateUserObj_InvalidUserType() {
        // test if exception is thrown for invalid user type
        assertThrows(Exception.class, () -> {
            User nUser = new UserImpl(mockEmail(), "InvalidType", mockUsername());
        });
    }


}
