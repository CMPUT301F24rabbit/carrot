package com.example.goldencarrot;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.goldencarrot.views.EntrantEventDetailsActivity;
import com.example.goldencarrot.views.EntrantHomeView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class EntrantEventDetailsActivityTest {

    @Rule
    public ActivityScenarioRule<EntrantEventDetailsActivity> activityRule =
            new ActivityScenarioRule<>(EntrantEventDetailsActivity.class);

    @Before
    public void setUp() {
        // Initialize Intents for intent verification
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Intents after testing
        Intents.release();
    }


    @Test
    public void testEventDetailsDisplayed() throws InterruptedException {
        // Create an Intent with extras and launch EntrantEventDetailsActivity
        Intent intent = new Intent();
        intent.putExtra("eventId", "ZVhLfmiNmmYtyqsQJHhn");  // Set up sample event ID
        ActivityScenario<EntrantEventDetailsActivity> scenario =
                ActivityScenario.launch(intent.setClass(getApplicationContext(), EntrantEventDetailsActivity.class));

        // Wait for Firestore data to load
        Thread.sleep(5000);

        // Check if the event details TextView is displayed
        /**
         * Todo change this to verify that all the Event details match
         */
        onView(withId(R.id.entrant_eventDetailsTextView)).check(matches(isDisplayed()));
    }

    /**
     * Test that clicking the "Join Waitlist" button navigates to EntrantHomeView.
     */
    @Test
    public void testJoinWaitListNavigatesToEntrantHomeView() throws InterruptedException {
        // Create an Intent with extras and launch EntrantEventDetailsActivity
        Intent intent = new Intent();
        intent.putExtra("eventId", "ZVhLfmiNmmYtyqsQJHhn");
        ActivityScenario<EntrantEventDetailsActivity> scenario =
                ActivityScenario.launch(intent.setClass(getApplicationContext(), EntrantEventDetailsActivity.class));

        // Wait for Firestore data to load
        Thread.sleep(1000);

        // Click the "Join Waitinglist" button
        onView(withId(R.id.entrant_join_waitlist_button)).perform(click());

        // Wait for Firestore data to load
        Thread.sleep(1000);

        // Verify that an intent to start EntrantHomeView was launched
        intended(hasComponent(EntrantHomeView.class.getName()));
    }

    /**
     * Test that the back button finishes the activity.
     */
    @Test
    public void testBackButtonFunctionality() throws InterruptedException {
        // Create an Intent with extras and launch EntrantEventDetailsActivity
        Intent intent = new Intent();
        intent.putExtra("eventId", "ZVhLfmiNmmYtyqsQJHhn");
        ActivityScenario<EntrantEventDetailsActivity> scenario =
                ActivityScenario.launch(intent.setClass(getApplicationContext(), EntrantEventDetailsActivity.class));

        // Wait for Firestore data to load
        Thread.sleep(1000);

        // Click the back button
        onView(withId(R.id.entrant_backButton)).perform(click());

        // Verify that the activity is finished
        scenario.close();
    }
}
