package com.example.goldencarrot.data.model.user;

import java.util.Optional;

public interface User {
    String getEmail();    // Returns the user's email

    String getUserType(); // Returns the type of user (e.g., Organizer, Admin, etc.)

    String getName();

    Optional<String> getPhoneNumber();

    String getUserId();

    void setUserId(String uId);

    void setPhoneNumber(Optional<String> phoneNumber);
}
