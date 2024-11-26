package com.example.goldencarrot.data.model.user;

import java.util.Optional;

/**
 * Implementation of the User interface.
 *
 * This class provides concrete implementations for the methods defined in the User interface. It holds user-related data
 * such as email, user type, name, phone number, user ID, and notification preferences for administrators and organizers.
 */
public class UserImpl implements User {

    private String email;
    private String userType;
    private String name;
    private Optional<String> phoneNumber;
    private String uId;
    private Boolean notificationAdministrators;
    private Boolean notificationOrganizers;
    private String profileImage;

    /**
     * Default constructor for creating an empty UserImpl instance.
     */
    public UserImpl() {}

    /**
     * Constructor to initialize the user with all necessary attributes.
     *
     * @param email the user's email.
     * @param userType the type of the user (e.g., Organizer, Admin).
     * @param name the user's name.
     * @param phoneNumber the user's phone number, wrapped in an Optional.
     * @param administratorNotification whether the user wants notifications from administrators.
     * @param organizerNotification whether the user wants notifications from organizers.
     * @param userProfileImage the users profile image
     * @throws Exception if the userType is invalid.
     */
    public UserImpl(final String email,
                    final String userType,
                    final String name,
                    final Optional<String> phoneNumber,
                    final Boolean administratorNotification,
                    final Boolean organizerNotification,
                    final String userProfileImage) throws Exception {
        validateUserType(userType);
        this.email = email;
        this.userType = userType;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.notificationAdministrators = administratorNotification;
        this.notificationOrganizers = organizerNotification;
        this.profileImage = userProfileImage;
    }

    /**
     * Constructor that initializes the user with only a user ID (for cases where other attributes are set later).
     *
     * @param userId the user ID to set.
     */
    public UserImpl(String userId) {
        this.uId = userId;
    }

    /**
     * Returns the email of the user.
     *
     * @return the user's email as a String.
     */
    @Override
    public String getEmail() {
        return this.email;
    }

    /**
     * Returns the type of the user (e.g., Organizer, Admin).
     *
     * @return the user type as a String.
     */
    @Override
    public String getUserType() {
        return this.userType;
    }

    /**
     * Returns the name of the user.
     *
     * @return the user's name as a String.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber the phone number to set, wrapped in an Optional.
     */
    @Override
    public void setPhoneNumber(final Optional<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the phone number of the user, if available.
     *
     * @return the user's phone number as an Optional.
     */
    @Override
    public Optional<String> getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Sets the unique user ID.
     *
     * @param uId the user ID to set.
     */
    @Override
    public void setUserId(String uId) {
        this.uId = uId;
    }

    /**
     * Returns the unique user ID.
     *
     * @return the user ID as a String.
     */
    @Override
    public String getUserId() {
        return this.uId;
    }

    /**
     * Returns whether the user has enabled notifications from administrators.
     *
     * @return true if the user wants notifications from administrators, false otherwise.
     */
    public Boolean getAdminNotification() {
        return this.notificationAdministrators;
    }

    /**
     * Returns whether the user has enabled notifications from organizers.
     *
     * @return true if the user wants notifications from organizers, false otherwise.
     */
    public Boolean getOrganizerNotifications() {
        return this.notificationOrganizers;
    }


    /**
     * Returns the unique user profile.
     *
     * @return the user profile image as a String.
     */
    public String getProfileImage() {
        return profileImage;
    }

    /**
     * Sets the unique user profile.
     * @param profileImage the profile image of the user
     */
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * Validates the user type to ensure it is a valid type.
     *
     * @param userType the user type to validate.
     * @throws Exception if the user type is invalid.
     */
    private void validateUserType(String userType) throws Exception {
        if (!UserUtils.validUserTypes.contains(userType)) {
            throw UserUtils.invalidUserTypeException;
        }
    }
}

