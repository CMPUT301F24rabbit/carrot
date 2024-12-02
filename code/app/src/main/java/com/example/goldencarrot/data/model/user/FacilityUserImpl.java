package com.example.goldencarrot.data.model.user;

import java.util.Optional;

/**
 * This class represents a user associated with a facility.
 * It extends the {@link UserImpl} class and adds additional fields related to the facility such as facility name,
 * location, and an image URL.
 */
public class FacilityUserImpl extends UserImpl {

    private Optional<String> facilityName;
    private Optional<String> location;
    private Optional<String> imageURL;

    /**
     * Default constructor to create a FacilityUserImpl instance.
     * This invokes the super constructor from the UserImpl class.
     */
    public FacilityUserImpl() {
        super();
    }

    /**
     * Constructs a FacilityUserImpl instance with the given parameters.
     * This constructor initializes both the user-related and facility-related information.
     *
     * @param email                 the email address of the user
     * @param userType              the type of user (e.g., "admin", "regular")
     * @param name                  the name of the user
     * @param phoneNumber           the optional phone number of the user
     * @param administratorNotification flag indicating if the user should receive admin notifications
     * @param organizerNotification flag indicating if the user should receive organizer notifications
     * @param facilityName          an optional name of the facility
     * @param location              an optional location of the facility
     * @param imageURL              an optional URL for the facility's image
     * @throws Exception if any error occurs while setting the values
     */
    public FacilityUserImpl(final String email,
                            final String userType,
                            final String name,
                            final Optional<String> phoneNumber,
                            final Boolean administratorNotification,
                            final Boolean organizerNotification,
                            final Optional<String> facilityName,
                            final Optional<String> location,
                            final Optional<String> imageURL) throws Exception {
        super(email, userType, name, phoneNumber, administratorNotification, organizerNotification, String.valueOf(imageURL));
        this.facilityName = facilityName;
        this.location = location;
        this.imageURL = imageURL;
    }

    /**
     * Gets the facility name.
     *
     * @return an Optional containing the facility name if present
     */
    public Optional<String> getFacilityName() {
        return facilityName;
    }

    /**
     * Sets the facility name.
     *
     * @param facilityName an Optional containing the new facility name
     */
    public void setFacilityName(Optional<String> facilityName) {
        this.facilityName = facilityName;
    }

    /**
     * Gets the location of the facility.
     *
     * @return an Optional containing the location of the facility if present
     */
    public Optional<String> getLocation() {
        return location;
    }

    /**
     * Sets the location of the facility.
     *
     * @param location an Optional containing the new location of the facility
     */
    public void setLocation(Optional<String> location) {
        this.location = location;
    }

    /**
     * Gets the image URL associated with the facility.
     *
     * @return an Optional containing the image URL if present
     */
    public Optional<String> getImageURL() {
        return imageURL;
    }

    /**
     * Sets the image URL for the facility.
     *
     * @param imageURL an Optional containing the new image URL for the facility
     */
    public void setImageURL(Optional<String> imageURL) {
        this.imageURL = imageURL;
    }
}