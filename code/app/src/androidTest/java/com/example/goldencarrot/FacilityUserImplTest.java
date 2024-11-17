package com.example.goldencarrot;

import com.example.goldencarrot.data.model.user.FacilityUserImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FacilityUserImplTest {

    private FacilityUserImpl facilityUser;

    @Before
    public void setUp() throws Exception {
        // Initialize a FacilityUserImpl object with test data
        facilityUser = new FacilityUserImpl(
                "test@example.com",
                "ORGANIZER",
                "Test Facility",
                Optional.of("1234567890"),
                true,
                true,
                Optional.of("Test Facility Name"),
                Optional.of("Test Location"),
                Optional.of("http://example.com/image.jpg")
        );
    }

    @Test
    public void testGetFacilityName() {
        assertEquals("Test Facility Name", facilityUser.getFacilityName().orElse(""));
    }

    @Test
    public void testGetLocation() {
        assertEquals("Test Location", facilityUser.getLocation().orElse(""));
    }

    @Test
    public void testGetImageURL() {
        assertEquals("http://example.com/image.jpg", facilityUser.getImageURL().orElse(""));
    }

    @Test
    public void testGetUserType() {
        assertEquals("ORGANIZER", facilityUser.getUserType());
    }

    @Test
    public void testNotifications() {
        assertTrue(facilityUser.getAdminNotification());
        assertTrue(facilityUser.getOrganizerNotifications());
    }
}
