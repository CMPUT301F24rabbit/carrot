package com.example.goldencarrot.data.model.user;

public class UserFacility {
    private String facilityID;
    private String organizerID;
    private String name;
    private String location;
    private String description;
    private String contactInfo;
    private String imageURL;

    public UserFacility() {
        // Default constructor required for Firebase
    }

    public UserFacility(String facilityID, String organizerID, String name, String location, String description, String contactInfo, String imageURL) {
        this.facilityID = facilityID;
        this.organizerID = organizerID;
        this.name = name;
        this.location = location;
        this.description = description;
        this.contactInfo = contactInfo;
        this.imageURL = imageURL;
    }

    // Getters and Setters for each field
    public String getFacilityID() { return facilityID; }
    public void setFacilityID(String facilityID) { this.facilityID = facilityID; }

    public String getOrganizerID() { return organizerID; }
    public void setOrganizerID(String organizerID) { this.organizerID = organizerID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
}
