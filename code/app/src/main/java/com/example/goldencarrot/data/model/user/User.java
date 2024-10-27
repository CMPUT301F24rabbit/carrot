package com.example.goldencarrot.data.model.user;

public interface User {
    String getEmail();        // Returns the user's email
    String getUserType(); // Returns the type of user (e.g., Organizer, Admin, etc.)
    String getUsername();
}
