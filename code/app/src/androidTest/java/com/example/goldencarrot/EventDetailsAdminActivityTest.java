package com.example.goldencarrot;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.intent.Intents;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.example.goldencarrot.views.BrowseEventsActivity;
import com.example.goldencarrot.views.EventDetailsAdminActivity;

@RunWith(AndroidJUnit4.class)
public class EventDetailsAdminActivityTest {

    @Rule
    public ActivityTestRule<EventDetailsAdminActivity> activityRule =
            new ActivityTestRule<>(EventDetailsAdminActivity.class, true, false);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test that event details are displayed correctly.
     */
    @Test
    public void testEventDetailsDisplayed() {
        // Launch activity with an event ID as an intent extra
        Intent intent = new Intent();
        intent.putExtra("eventId", "testEventId");
        activityRule.launchActivity(intent);

        // Verify that the event details are displayed
        onView(withId(R.id.event_DetailNameTitleView)).check(matches(isDisplayed()));
        onView(withId(R.id.event_DetailDateView)).check(matches(isDisplayed()));
        onView(withId(R.id.event_DetailLocationView)).check(matches(isDisplayed()));
        onView(withId(R.id.event_DetailDetailsView)).check(matches(isDisplayed()));
    }

    /**
     * Test that clicking the back button navigates to BrowseEventsActivity.
     */
    @Test
    public void testBackButtonNavigation() {
        // Launch the activity
        activityRule.launchActivity(new Intent());

        // Click the back button
        onView(withId(R.id.back_DetailButton)).perform(click());

        // Verify that BrowseEventsActivity is launched
        intended(hasComponent(BrowseEventsActivity.class.getName()));
    }

    /**
     * Test that clicking the delete button deletes the event and navigates to BrowseEventsActivity.
     */
}
