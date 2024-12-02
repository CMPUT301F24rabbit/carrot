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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FacilityProfileTest {
    private static final String FACILITY_NAME = "Spider-Man's Parties";
    private static final String FACILITY_DESCRIPTION = "We Throw Parties";
    private static final String FACILITY_CONTACT_INFO = "spiderman@gmail.com";
    @Rule
    public ActivityScenarioRule<OrganizerHomeView> activityScenarioRule =
            new ActivityScenarioRule<>(OrganizerHomeView.class);


    // ONLY test on organizer phone
    @Test
    public void createFacilityProfileTest() throws InterruptedException{
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            onView(withId(R.id.manageFacilityProfileBtn)).perform(click());
            Thread.sleep(3000);
            onView(withText("Facility Name:")).check(matches(isDisplayed()));


            //fill out facility profile with sample data
            onView(withId(R.id.nameEditText)).perform(clearText());
            onView(withId(R.id.nameEditText)).perform(typeText(FACILITY_NAME));

            onView(withId(R.id.locationEditText)).perform(clearText());
            onView(withId(R.id.locationEditText)).perform(typeText("U of A"));

            onView(withId(R.id.descriptionEditText)).perform(clearText());
            onView(withId(R.id.descriptionEditText)).perform(typeText(FACILITY_DESCRIPTION));

            onView(withId(R.id.contactInfoEditText)).perform(clearText());
            onView(withId(R.id.contactInfoEditText)).perform(typeText(FACILITY_CONTACT_INFO));

            // Close the keyboard
            onView(withId(R.id.contactInfoEditText)).perform(ViewActions.closeSoftKeyboard());

            onView(withId(R.id.saveFacilityButton)).perform(click());

            Thread.sleep(3000);

            onView(withText("Welcome back ")).check(matches(isDisplayed()));
        }
    }
    @Test
    public void updateFacilityProfileTest() throws InterruptedException{
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            onView(withId(R.id.manageFacilityProfileBtn)).perform(click());
            onView(withText("Facility Name:")).check(matches(isDisplayed()));


            //Check if facility profile was filled with sample data
            onView(withText(FACILITY_NAME)).check(matches(isDisplayed()));
            onView(withText("U of A")).check(matches(isDisplayed()));
            onView(withText(FACILITY_DESCRIPTION)).check(matches(isDisplayed()));
            onView(withText(FACILITY_CONTACT_INFO)).check(matches(isDisplayed()));

            // Update location

            onView(withId(R.id.locationEditText)).perform(clearText());
            onView(withId(R.id.locationEditText)).perform(typeText("Macewan"));
            // Close the keyboard
            onView(withId(R.id.locationEditText)).perform(ViewActions.closeSoftKeyboard());

            onView(withId(R.id.saveFacilityButton)).perform(click());

            Thread.sleep(3000);
            onView(withId(R.id.manageFacilityProfileBtn)).perform(click());

            //check if changes saved
            onView(withText("Macewan")).check(matches(isDisplayed()));
        }
    }
}
