package com.example.goldencarrot;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isActivated;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.goldencarrot.views.AdminHomeActivity;
import com.example.goldencarrot.views.OrganizerHomeView;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FacilityProfileTest {
    public ActivityScenarioRule<OrganizerHomeView> activityScenarioRule =
            new ActivityScenarioRule<>(OrganizerHomeView.class);


    // ONLY test on organizer phone
    @BeforeClass
    public static void createFacilityProfileTest() {
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            onView(withId(R.id.manageFacilityProfileBtn)).perform(click());
            onView(withText("Facility Name:")).check(matches(isDisplayed()));


            //fill out facility profile with sample data
            onView(withId(R.id.nameEditText)).perform(clearText());
            onView(withId(R.id.nameEditText)).perform(typeText("Sample Facility"));

            onView(withId(R.id.locationEditText)).perform(clearText());
            onView(withId(R.id.locationEditText)).perform(typeText("U of A"));

            onView(withId(R.id.descriptionEditText)).perform(clearText());
            onView(withId(R.id.descriptionEditText)).perform(typeText("This is a sample facility for testing."));

            onView(withId(R.id.contactInfoEditText)).perform(clearText());
            onView(withId(R.id.contactInfoEditText)).perform(typeText("Phone number: 7800000004 \nEmail: sampleemail@test.com"));

            // Close the keyboard
            onView(withId(R.id.contactInfoEditText)).perform(ViewActions.closeSoftKeyboard());

            onView(withId(R.id.saveFacilityButton)).perform(click());
        }
    }

}
