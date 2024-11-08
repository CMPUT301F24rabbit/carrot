package com.example.goldencarrot;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.action.ViewActions;
import androidx.test.core.app.ActivityScenario;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.goldencarrot.views.OrganizerCreateEvent;

@RunWith(AndroidJUnit4.class)
public class OrganizerEventTest {
    @Rule
    public ActivityScenarioRule<OrganizerCreateEvent> activityRule =
            new ActivityScenarioRule<>(OrganizerCreateEvent.class);

    @Test
    public void testEventCreation() {
        // Launch the activity
        try (ActivityScenario<OrganizerCreateEvent> scenario = ActivityScenario.launch(OrganizerCreateEvent.class)) {
            // Input event details
            onView(withId(R.id.eventNameEditText)).perform(typeText("Sample Event"));
            onView(withId(R.id.eventLocationEditText)).perform(typeText("New York"));
            onView(withId(R.id.eventDetailsEditText)).perform(typeText("This is a sample event."));
            onView(withId(R.id.eventDateEditText)).perform(typeText("2024-12-31"));

            // Close the keyboard
            onView(withId(R.id.eventDateEditText)).perform(ViewActions.closeSoftKeyboard());

            // Click on "Create Event" button
            onView(withId(R.id.createEventButton)).perform(click());

            // Check for success message or behavior
            onView(withText("Welcome back ")).check(matches(isDisplayed()));
        }
    }
}

