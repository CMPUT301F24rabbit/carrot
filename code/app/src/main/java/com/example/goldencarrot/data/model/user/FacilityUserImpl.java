package com.example.goldencarrot.data.model.user;

import java.util.Optional;

public class FacilityUserImpl extends UserImpl {
    private Optional<String> facilityName;
    private Optional<String> location;
    private Optional<String> imageURL;

    public FacilityUserImpl() {
        super();
    }

    public FacilityUserImpl(final String email,
                            final String userType,
                            final String name,
                            final Optional<String> phoneNumber,
                            final Boolean administratorNotification,
                            final Boolean organizerNotification,
                            final Optional<String> facilityName,
                            final Optional<String> location,
                            final Optional<String> imageURL) throws Exception {
        super(email, userType, name, phoneNumber, administratorNotification, organizerNotification);
        this.facilityName = facilityName;
        this.location = location;
        this.imageURL = imageURL;
    }

    // Facility-specific methods
    public Optional<String> getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(Optional<String> facilityName) {
        this.facilityName = facilityName;
    }

    public Optional<String> getLocation() {
        return location;
    }

    public void setLocation(Optional<String> location) {
        this.location = location;
    }

    public Optional<String> getImageURL() {
        return imageURL;
    }

    public void setImageURL(Optional<String> imageURL) {
        this.imageURL = imageURL;
    }
}
