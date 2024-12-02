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
}
