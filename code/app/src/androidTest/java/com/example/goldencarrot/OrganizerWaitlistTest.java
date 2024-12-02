package com.example.goldencarrot;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
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

import android.util.Log;

import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.views.AdminHomeActivity;
import com.example.goldencarrot.views.OrganizerCreateEvent;
import com.example.goldencarrot.views.OrganizerHomeView;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrganizerWaitlistTest {
    private static final String TAG = "OrganizerNotificationTest";
    private static final String EVENT_NAME = "SPIDERMAN PARTY";

    @Rule
    public ActivityScenarioRule<OrganizerHomeView> activityRule =
            new ActivityScenarioRule<>(OrganizerHomeView.class);

    /**
     * Creates sample event to test viewing waitlists
     */
    @BeforeClass
    public static void testCreatingEventToViewWaitlist() throws InterruptedException{
        // Launch the activity
        try (ActivityScenario<OrganizerCreateEvent> scenario = ActivityScenario.launch(OrganizerCreateEvent.class)) {
            // Input event details
            onView(withId(R.id.eventNameEditText)).perform(typeText(EVENT_NAME));
            onView(withId(R.id.eventLocationEditText)).perform(clearText());
            onView(withId(R.id.eventLocationEditText)).perform(typeText("New York"));
            onView(withId(R.id.eventDetailsEditText)).perform(typeText("This is a sample event for testing waitlists."));
            onView(withId(R.id.eventDateEditText)).perform(typeText("31-12-2024"));

            // Close the keyboard
            onView(withId(R.id.eventDateEditText)).perform(ViewActions.closeSoftKeyboard());

            // Click on "Create Event" button
            onView(withId(R.id.createEventButton)).perform(click());

            Thread.sleep(4000);

            onView(withText("Welcome Back "));

        }
    }

    /**
     * tests button to display waiting list
     */
    @Test
    public void testShowWaitingList() {
        // US 02.02.01: Organizer can view list of entrants who joined waiting list
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            // scroll to sample event and view details
            onView(withId(R.id.recycler_view_events)).perform(
                    repeatedlyUntil(swipeUp(), hasDescendant(withText(EVENT_NAME)),
                            10)
            );
            onView(withText(EVENT_NAME)).perform(longClick());
            onView(withText(EVENT_NAME)).check(matches(isDisplayed()));
            // Viewing entrant waitlist
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailWaitlistedEntrants)).perform(click());
            onView(withText("WAITING")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }
    /**
     * tests button to display chosen (by lottery) list
     */
    @Test
    public void testShowChosenList() {
        // US 02.06.01: Organizer can view list of chosen entrants
        // by default they will be added to "Accepted", and moved to "Declined" if they decline
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            // scroll to sample event and view details
            onView(withId(R.id.recycler_view_events)).perform(
                    repeatedlyUntil(swipeUp(), hasDescendant(withText(EVENT_NAME)),
                            10)
            );
            onView(withText(EVENT_NAME)).perform(longClick());
            onView(withText(EVENT_NAME)).check(matches(isDisplayed()));
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailChosenEntrants)).perform(click());
            onView(withText("CHOSEN")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }
    /**
     * tests button to display cancelled list
     */
    @Test
    public void testShowCancelledList() {
        // US 02.06.02: Organizer can view list of canceled entrants (or declined)
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            // scroll to sample event and view details
            onView(withId(R.id.recycler_view_events)).perform(
                    repeatedlyUntil(swipeUp(), hasDescendant(withText(EVENT_NAME)),
                            10)
            );
            onView(withText(EVENT_NAME)).perform(longClick());
            onView(withText(EVENT_NAME)).check(matches(isDisplayed()));
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailRejectedEntrants)).perform(click());
            onView(withText("CANCELLED")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }
    /**
     * tests button to display accepted list
     */
    @Test
    public void testShowAcceptedList() {
        // US 02.06.02: Organizer can view list of canceled entrants (or declined)
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            // scroll to sample event and view details
            onView(withId(R.id.recycler_view_events)).perform(
                    repeatedlyUntil(swipeUp(), hasDescendant(withText(EVENT_NAME)),
                            10)
            );
            onView(withText(EVENT_NAME)).perform(longClick());
            onView(withText(EVENT_NAME)).check(matches(isDisplayed()));
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailAcceptedEntrants)).perform(click());
            onView(withText("ACCEPTED")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }

    /**
     * deletes sample event
     */
    @AfterClass
    public static void cleanUpEvent() {
        EventRepository eventRepository = new EventRepository();
        eventRepository.deleteEventByName(EVENT_NAME, new EventRepository.DeleteCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "Event cleanup successful: " + message);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Event cleanup failed", e);
            }
        });
    }
}
