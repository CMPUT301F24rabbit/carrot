package com.example.goldencarrot;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.goldencarrot.data.db.FacilityRepository;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FacilityRepositoryTest {

    @Test
    public void testAddFacilityFieldsToSpecificUser() {
        // Initialize FacilityRepository
        FacilityRepository facilityRepository = new FacilityRepository();

        // Specify the user ID (hardcoded for this test)
        String userId = "982d734ccdd16e72";

        // Facility details to add
        String facilityName = "My Test Facility";
        String location = "123 Test Street";
        String imageURL = "http://example.com/facility.jpg";

        // Add facility fields to the specified user
        facilityRepository.addFacilityFields(userId, facilityName, location, imageURL);
    }
}
