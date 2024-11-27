package com.example.goldencarrot;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.repeatedlyUntil;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.instanceOf;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.goldencarrot.views.AdminHomeActivity;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminCleanupTest {
    @Rule
    public ActivityScenarioRule<AdminHomeActivity> scenario = new ActivityScenarioRule<>(AdminHomeActivity.class);

    /**
     * Test to delete all events created by another test
     */
    @Test
    public void testCleanUpEvents() {
        // Click on Events button
        onView(withId(R.id.adminAllEventsButton)).perform(click());
        onView(withText("Browse Events")).check(matches(isDisplayed()));
        //
        try {
            Thread.sleep(3000);
            // scroll to sample event and view details
            onView(withId(R.id.eventsListView)).perform(
                    repeatedlyUntil(swipeUp(), hasDescendant(withText("Sample Event For Waitlist Test")),
                            10)
            );
            //onView(withText("Sample Event For Waitlist Test")).check(matches(isDisplayed()));
            onView(withText("Sample Event For Waitlist Test")).perform(click());
            //onView(withText("Sample Event For Waitlist Test")).perform(click());
            onView(withId(R.id.delete_DetailEventBtn)).perform(click());
            onView(withText("Event deleted"));

        } catch (Exception e) {

        }
        onView(withId(R.id.browseEventsBackBtn)).perform(click());
        onView(withId(R.id.adminAllEventsButton)).perform(click());
        onView(withText("Browse Events")).check(matches(isDisplayed()));
        try {
            Thread.sleep(3000);
            // scroll to sample event and view details
            onView(withId(R.id.eventsListView)).perform(
                    repeatedlyUntil(swipeUp(), hasDescendant(withText("Sample Event For Notification Test")),
                            10)
            );
            //onView(withText("Sample Event For Waitlist Test")).check(matches(isDisplayed()));
            onView(withText("Sample Event For Notification Test")).perform(click());
            //onView(withText("Sample Event For Waitlist Test")).perform(click());
            onView(withId(R.id.delete_DetailEventBtn)).perform(click());
            onView(withText("Event deleted"));

        } catch (Exception e) {

        }

    }
}
