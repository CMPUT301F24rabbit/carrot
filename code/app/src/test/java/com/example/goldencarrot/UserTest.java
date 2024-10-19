package com.example.goldencarrot;

import static com.example.goldencarrot.data.model.user.UserUtils.ADMIN_TYPE;
import static com.example.goldencarrot.data.model.user.UserUtils.ORGANIZER_TYPE;
import static com.example.goldencarrot.data.model.user.UserUtils.USER_TYPE;
import static com.example.goldencarrot.data.model.user.UserUtils.invalidUserTypeException;

import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
    void testCreateUserObj() {
        User nUser;
        // test if exception is thrown for invalid user type
        nUser = mockUser(ADMIN_TYPE);
        assertSame(nUser.getEmail(), mockEmail());
        assertSame(nUser.getUserType(), ADMIN_TYPE);
        assertSame(nUser.getUsername(), mockUsername());

        nUser = mockUser(ORGANIZER_TYPE);
        assertSame(nUser.getUserType(), ORGANIZER_TYPE);
    }


}
