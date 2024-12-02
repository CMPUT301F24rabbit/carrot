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
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EntrantEventDetailsActivityTest {
    private TestDataHelper testDataHelper;

    @Rule
    public ActivityScenarioRule<EntrantEventDetailsActivity> activityRule =
            new ActivityScenarioRule<>(EntrantEventDetailsActivity.class);

    @Before
    public void setUp() throws Exception {
        // Initialize Intents for intent verification
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Intents after testing
        Intents.release();
    }

    /**
     * Test that clicking the "Join Waitlist" button warns user about navigation enabled
     */
    @Test
    public void TestJoinEventWithGeoLocationEnabled_WarnsUser() throws Exception {
        testDataHelper = new TestDataHelper(true);

        Thread.sleep(5000); // Allow Firestore to populate test data

        String eventId = testDataHelper.getEventId();

        Intent intent = new Intent();
        intent.putExtra("eventId", eventId); // add the test event id
        ActivityScenario<EntrantEventDetailsActivity> scenario =
                ActivityScenario.launch(intent.setClass(getApplicationContext(), EntrantEventDetailsActivity.class));

        // Click the "Join Waitinglist" button
        onView(withId(R.id.entrant_join_waitlist_button)).perform(click());

        // Assert that the dialog appears with the correct title or message
        onView(withText("Geolocation is enabled for this event"))
                .check(matches(isDisplayed()));
        onView(withText("Are you sure you want to join?"))
                .check(matches(isDisplayed()));

        // Optionally, you can interact with the dialog's buttons
        onView(withText("Yes")).perform(click());

        // Cleanup data
        testDataHelper.deleteData();
        Thread.sleep(5000);
    }

    /**
     * Test that clicking the "Join Waitlist" event with Geolocation disabled.
     * User will join the waitlist with no warning
     */
    @Test
    public void TestJoinEventWithGeoLocationDisabled() throws Exception {
        testDataHelper = new TestDataHelper(false);
        Thread.sleep(5000); // Allow Firestore to populate test data

        String eventId = testDataHelper.getEventId();

        Intent intent = new Intent();
        intent.putExtra("eventId", eventId); // add the test event id
        ActivityScenario<EntrantEventDetailsActivity> scenario =
                ActivityScenario.launch(intent.setClass(getApplicationContext(), EntrantEventDetailsActivity.class));

        // Click the "Join Waitinglist" button
        onView(withId(R.id.entrant_join_waitlist_button)).perform(click());

        Thread.sleep(5000);

        // Check that user is back at the EntrantHomeVIew
        intended(hasComponent(EntrantHomeView.class.getName()));

        // Cleanup data
        testDataHelper.deleteData();
        Thread.sleep(5000);
    }


    /**
     * Test that the back button finishes the activity.
     */
    @Test
    public void testBackButtonFunctionality() throws Exception {
        testDataHelper = new TestDataHelper(false);
        Thread.sleep(5000); // Allow Firestore to populate test data

        String eventId = testDataHelper.getEventId();

        Intent intent = new Intent();
        intent.putExtra("eventId", eventId); // add the test event id
        ActivityScenario<EntrantEventDetailsActivity> scenario =
                ActivityScenario.launch(intent.setClass(getApplicationContext(), EntrantEventDetailsActivity.class));

        // Click the back button
        onView(withId(R.id.entrant_backButton)).perform(click());

        Thread.sleep(5000);

        // Check that user is back at the EntrantHomeVIew
        intended(hasComponent(EntrantHomeView.class.getName()));

        // Cleanup data
        testDataHelper.deleteData();
        Thread.sleep(5000);

    }
}
