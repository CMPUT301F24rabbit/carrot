package com.example.goldencarrot;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.goldencarrot.R;
import com.example.goldencarrot.views.WaitlistActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class WaitlistActivityTest {

    @Rule
    public ActivityTestRule<WaitlistActivity> activityRule =
            new ActivityTestRule<>(WaitlistActivity.class, true, false);

    @Before
    public void setUp() {
        // Initialize the activity
    }

    @After
    public void tearDown() {
        // Clean up after tests (if needed)
    }

    /**
     * Test that the UI components (ListView and Back button) are displayed.
     */
    @Test
    public void testUIComponentsDisplayed() {
        // Launch the activity
        activityRule.launchActivity(new Intent());

        // Simulate loading mock data (instead of fetching from Firestore)
        simulateLoadWaitlistData();

        // Check if the ListView and Back button are displayed
        Espresso.onView(withId(R.id.waitingListView)).check(ViewAssertions.matches(isDisplayed()));
        Espresso.onView(withId(R.id.button_back_to_previous_activity)).check(ViewAssertions.matches(isDisplayed()));
    }

    /**
     * Simulate loading mock data into the ListView (instead of using Firestore).
     */
    private void simulateLoadWaitlistData() {
        // We are simulating mock data instead of fetching from Firestore
        ArrayList<String> mockWaitlist = new ArrayList<>();
        mockWaitlist.add("Mock Event 1");
        mockWaitlist.add("Mock Event 2");
        mockWaitlist.add("Mock Event 3");

        // Inject this data into the ListView adapter
        WaitlistActivity activity = activityRule.getActivity();
        activity.runOnUiThread(() -> {

        });
    }

    /**
     * Test that clicking the back button finishes the activity (navigates back).
     */
    @Test
    public void testBackButtonFunctionality() {
        // Launch the activity
        activityRule.launchActivity(new Intent());

        // Simulate loading mock data into the ListView
        simulateLoadWaitlistData();

        // Perform a click on the back button
        Espresso.onView(withId(R.id.button_back_to_previous_activity)).perform(ViewActions.click());

        // Check if the activity is finishing (which means it went back to the previous activity)
        assertTrue(activityRule.getActivity().isFinishing());
    }
}
