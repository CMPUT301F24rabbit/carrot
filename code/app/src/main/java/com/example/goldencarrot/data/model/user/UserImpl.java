package com.example.goldencarrot.data.model.user;

import java.util.Optional;

public class UserImpl implements User{
    private  String email;
    private  String userType;
    private String name;
    private Optional<String> phoneNumber;
    private String uId;

    // Facility-specific fields
    private Optional<String> facilityName; // Facility ID, if applicable
    private Optional<String> location; // Facility location
    private Optional<String> imageURL; // Facility image URL

    public UserImpl(){}

    public UserImpl(final String email,
                    final String userType,
                    final String name,
                    final Optional<String> phoneNumber,
                    final Optional<String> facilityName,
                    final Optional<String> location,
                    final Optional<String> imageURL) throws Exception{
        validateUserType(userType);
        this.email = email;
        this.userType = userType;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.facilityName = facilityName;
        this.location = location;
        this.imageURL = imageURL;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getUserType() {
        return this.userType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setPhoneNumber(final Optional<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Optional<String> getPhoneNumber() {
        return this.phoneNumber;
    }

    @Override
    public void setUserId(String uId) {
        this.uId = uId;
    }

    @Override
    public String getUserId() {
        return this.uId;
    }

    // Facility-related methods
    public Optional<String> getFacilityName() {
        return this.facilityName;
    }

    public void setFacilityName(final Optional<String> facilityName) {
        this.facilityName = facilityName;
    }

    public Optional<String> getLocation() {
        return this.location;
    }

    public void setLocation(final Optional<String> location) {
        this.location = location;
    }

    public Optional<String> getImageURL() {
        return this.imageURL;
    }


    public void setImageURL(final Optional<String> imageURL) {
        this.imageURL = imageURL;
    }

    private void validateUserType(String userType) throws Exception {
        if (!UserUtils.validUserTypes.contains(userType)){
            throw UserUtils.invalidUserTypeException;
        }
    }

}
