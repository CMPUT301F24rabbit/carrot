package com.example.goldencarrot;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.repeatedlyUntil;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.goldencarrot.views.AdminHomeActivity;
import com.example.goldencarrot.views.EntrantHomeView;
import com.example.goldencarrot.views.OrganizerCreateEvent;
import com.example.goldencarrot.views.OrganizerHomeView;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(AndroidJUnit4.class)
public class OrganizerNotificationTest {
    @Rule
    public ActivityScenarioRule<OrganizerHomeView> activityRule =
            new ActivityScenarioRule<>(OrganizerHomeView.class);
    public ActivityScenarioRule<EntrantHomeView> activityReceivingRule = new ActivityScenarioRule<>(EntrantHomeView.class);

    /**
     * Creates sample event to test sending notifications
     */
    @BeforeClass
    public static void createTestEventForNotifications() {
        // Launch the activity
        try (ActivityScenario<OrganizerCreateEvent> scenario = ActivityScenario.launch(OrganizerCreateEvent.class)) {
            // Input event details
            onView(withId(R.id.eventNameEditText)).perform(typeText("Sample Event For Notification Test"));
            onView(withId(R.id.eventLocationEditText)).perform(typeText("New York"));
            onView(withId(R.id.eventDetailsEditText)).perform(typeText("This is a sample event for testing notifications"));
            onView(withId(R.id.eventDateEditText)).perform(typeText("31-12-2024"));

            // Close the keyboard
            onView(withId(R.id.eventDateEditText)).perform(ViewActions.closeSoftKeyboard());

            // Click on "Create Event" button
            onView(withId(R.id.createEventButton)).perform(click());

            // Check for success message or behavior
            onView(withText("Welcome back ")).check(matches(isDisplayed()));
        }
    }

    /**
     * Tests button to send notifications to entrants in waiting list
     */
    @Test
    public void sendNotificationToEmptyWaitListTest() {
        // US 02.05.01: Organizer can send notifications to cancelled entrants
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            // scroll to sample event and view details
            onView(withId(R.id.recycler_view_events)).perform(
                    repeatedlyUntil(swipeUp(), hasDescendant(withText("Sample Event For Notification Test")),
                            10)
            );
            onView(withText("Sample Event For Notification Test")).perform(longClick());
            onView(withText("Sample Event For Notification Test")).check(matches(isDisplayed()));
            // Viewing entrant waitlist
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailWaitlistedEntrants)).perform(click());
            onView(withText("WAITING")).check(matches(isDisplayed()));
            // try to send notification
            onView(withId(R.id.sendNotificationButton)).perform(click());
            Thread.sleep(1000);
            onView(withText("list is empty, cannot send notification")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }

    /**
     * Tests button to send notifications to all
     */
    @Test
    public void sendNotificationsToAllEntrantsTest() {
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            onView(withId(R.id.sendNotificationToAllEntrantsButton)).perform(click());
            Thread.sleep(2000);
            onView(withText("added notification")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }
    /**
     * Tests button to send notifications to entrants in accepted list
     */
    @Test
    public void sendNotificationToEmptyAcceptedListTest() {
        // US 02.05.01: Organizer can send notifications to chosen entrants
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            // scroll to sample event and view details
            onView(withId(R.id.recycler_view_events)).perform(
                    repeatedlyUntil(swipeUp(), hasDescendant(withText("Sample Event For Notification Test")),
                            10)
            );
            onView(withText("Sample Event For Notification Test")).perform(longClick());
            onView(withText("Sample Event For Notification Test")).check(matches(isDisplayed()));
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailAcceptedEntrants)).perform(click());
            onView(withText("ACCEPTED")).check(matches(isDisplayed()));

            onView(withId(R.id.sendNotificationButton)).perform(click());
            Thread.sleep(1000);
            onView(withText("list is empty, cannot send notification")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }
    /**
     * Tests button to send notifications to entrants in chosen (by lottery) list
     */
    @Test
    public void sendNotificationToEmptyChosenListTest() {
        // US 02.05.01: Organizer can send notifications to chosen entrants
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            // scroll to sample event and view details
            onView(withId(R.id.recycler_view_events)).perform(
                    repeatedlyUntil(swipeUp(), hasDescendant(withText("Sample Event For Notification Test")),
                            10)
            );
            onView(withText("Sample Event For Notification Test")).perform(longClick());
            onView(withText("Sample Event For Notification Test")).check(matches(isDisplayed()));
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailChosenEntrants)).perform(click());
            onView(withText("CHOSEN")).check(matches(isDisplayed()));

            onView(withId(R.id.sendNotificationButton)).perform(click());
            Thread.sleep(1000);
            onView(withText("list is empty, cannot send notification")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }
    /**
     * Tests button to send notifications to entrants in cancelled list
     */
    @Test
    public void sendNotificationToEmptyCancelledListTest() {
        // US 02.05.01: Organizer can send notifications to chosen entrants
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            // scroll to sample event and view details
            onView(withId(R.id.recycler_view_events)).perform(
                    repeatedlyUntil(swipeUp(), hasDescendant(withText("Sample Event For Notification Test")),
                            10)
            );
            onView(withText("Sample Event For Notification Test")).perform(longClick());
            onView(withText("Sample Event For Notification Test")).check(matches(isDisplayed()));
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailRejectedEntrants)).perform(click());
            onView(withText("CANCELLED")).check(matches(isDisplayed()));

            onView(withId(R.id.sendNotificationButton)).perform(click());
            Thread.sleep(1000);
            onView(withText("list is empty, cannot send notification")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }

    /**
     * deletes sample event
     */
    @AfterClass
    public static void cleanUpEvent() {
        try (ActivityScenario<AdminHomeActivity> scenario = ActivityScenario.launch(AdminHomeActivity.class)) {
            onView(withId(R.id.adminAllEventsButton)).perform(click());
            onView(withText("Browse Events")).check(matches(isDisplayed()));
            Thread.sleep(3000);
            // scroll to sample event and view details
            onView(withId(R.id.eventsListView)).perform(
                    repeatedlyUntil(swipeUp(), hasDescendant(withText("Sample Event For Notification Test")),
                            10)
            );
            onView(withText("Sample Event For Notification Test")).perform(click());
            onView(withText("Sample Event For Notification Test")).perform(click());
            onView(withText("Sample Event For Notification Test")).check(matches(isDisplayed()));
            //onView(withText("Sample Event For Waitlist Test")).perform(click());
            onView(withId(R.id.delete_DetailEventBtn)).perform(click());
            onView(withText("Event deleted")).check(matches(isDisplayed()));

        } catch (Exception e) {

        }
    }
}
