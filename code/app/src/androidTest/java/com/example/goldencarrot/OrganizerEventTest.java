package com.example.goldencarrot;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.click;
import static org.hamcrest.Matchers.allOf;
import com.example.goldencarrot.views.OrganizerCreateEvent;
import com.example.goldencarrot.views.OrganizerApprovedView;
import com.example.goldencarrot.views.OrganizerCancelledView;

@RunWith(AndroidJUnit4.class)
public class OrganizerEventTest {

    @Rule
    public ActivityScenarioRule<OrganizerCreateEvent> activityRule =
            new ActivityScenarioRule<>(OrganizerCreateEvent.class);

    // Test case for creating an event
    @Test
    public void testEventCreation() {
        try (ActivityScenario<OrganizerCreateEvent> scenario = ActivityScenario.launch(OrganizerCreateEvent.class)) {
            // Input event details
            onView(withId(R.id.eventNameEditText)).perform(ViewActions.typeText("Sample Event"));
            onView(withId(R.id.eventLocationEditText)).perform(ViewActions.typeText("New York"));
            onView(withId(R.id.eventDetailsEditText)).perform(ViewActions.typeText("This is a sample event."));
            onView(withId(R.id.eventDateEditText)).perform(ViewActions.typeText("2024-12-31"));

            // Close the keyboard
            onView(withId(R.id.eventDateEditText)).perform(ViewActions.closeSoftKeyboard());

            // Click on "Create Event" button
            onView(withId(R.id.createEventButton)).perform(click());

            // Check for success message or behavior
            onView(withId(R.id.successMessageTextView))
                    .check(matches(withText("Event created successfully")));
        }
    }
}

