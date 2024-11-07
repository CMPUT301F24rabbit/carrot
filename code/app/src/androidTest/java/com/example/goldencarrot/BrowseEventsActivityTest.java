package com.example.goldencarrot;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.goldencarrot.views.BrowseEventsActivity;
import com.example.goldencarrot.views.AdminHomeActivity;
import com.example.goldencarrot.views.EventDetailsAdminActivity;

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
public class BrowseEventsActivityTest {

    @Rule
    public ActivityTestRule<BrowseEventsActivity> activityRule =
            new ActivityTestRule<>(BrowseEventsActivity.class, true, false);

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
        Intent intent = new Intent();
        intent.putExtra("userType", "ADMIN");  // Set user type as ADMIN
        activityRule.launchActivity(intent);

        // Simulate loading mock event data (instead of fetching from Firestore)
        simulateLoadEventData();

        // Check if the ListView and Back button are displayed
        Espresso.onView(withId(R.id.eventsListView)).check(ViewAssertions.matches(isDisplayed()));
        Espresso.onView(withId(R.id.browseEventsBackBtn)).check(ViewAssertions.matches(isDisplayed()));
    }

    /**
     * Simulate loading mock event data into the ListView (instead of using Firestore).
     */
    private void simulateLoadEventData() {
        // We are simulating mock data instead of fetching from Firestore
        ArrayList<String> mockEventList = new ArrayList<>();
        mockEventList.add("Mock Event 1");
        mockEventList.add("Mock Event 2");
        mockEventList.add("Mock Event 3");

        // Inject this data into the ListView adapter
        BrowseEventsActivity activity = activityRule.getActivity();
        activity.runOnUiThread(() -> {
            // Simulate loading the mock data into the ListView adapter
            ListView eventsListView = activity.findViewById(R.id.eventsListView);
            eventsListView.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, mockEventList));
        });
    }

    /**
     * Test that clicking the back button finishes the activity (navigates back to AdminHomeActivity).
     */
    @Test
    public void testBackButtonFunctionality() throws InterruptedException {
        // Launch the activity
        Intent intent = new Intent();
        intent.putExtra("userType", "ADMIN");  // Set user type as ADMIN
        activityRule.launchActivity(intent);

        // Simulate loading mock event data into the ListView
        simulateLoadEventData();

        // Add a short sleep to ensure that the view is fully loaded
        Thread.sleep(1000); // Sleep for 1 second

        // Perform a click on the back button
        Espresso.onView(withId(R.id.browseEventsBackBtn)).perform(ViewActions.click());

        // Verify that AdminHomeActivity is displayed
        Espresso.onView(withId(R.id.adminHomeHeader)).check(ViewAssertions.matches(isDisplayed()));
    }


    /**
     * Test clicking on an event item in the ListView.
     */
    @Test
    public void testEventItemClick() throws InterruptedException {
        // Launch the activity
        Intent intent = new Intent();
        intent.putExtra("userType", "ADMIN");  // Set user type as ADMIN
        activityRule.launchActivity(intent);
        Thread.sleep(1000);
        // Simulate loading mock event data into the ListView
        simulateLoadEventData();
        Thread.sleep(1000);
        // Perform a click on the first event item in the ListView
        Espresso.onView(ViewMatchers.withText("Mock Event 1")).perform(ViewActions.click());
        Thread.sleep(1000);
        // Verify that the correct Activity is started (EventDetailsAdminActivity in this case)
        // You can check if a view from EventDetailsAdminActivity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.event_DetailNameTitleView)).check(ViewAssertions.matches(isDisplayed()));
    }
}
