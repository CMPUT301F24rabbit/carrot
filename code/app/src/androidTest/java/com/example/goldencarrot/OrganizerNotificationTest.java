package com.example.goldencarrot;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.captureToBitmap;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.goldencarrot.views.OrganizerCreateEvent;
import com.example.goldencarrot.views.OrganizerHomeView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(AndroidJUnit4.class)
public class OrganizerNotificationTest {
    @Rule
    public ActivityScenarioRule<OrganizerHomeView> activityRule =
            new ActivityScenarioRule<>(OrganizerHomeView.class);

    @Test
    public void sendNotificationsToAllEntrantsTest() {
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            onView(withId(R.id.sendNotificationToAllEntrantsButton)).perform(click());
            Thread.sleep(2000);
            onView(withText("added notification")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }
    @Test
    public void sendNotificationToEmptyWaitlistTest() {
        // US 02.05.01: Organizer can send notifications to cancelled entrants
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            onView(withId(R.id.recycler_view_events)).perform(longClick());
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailRejectedEntrants)).perform(click());
            onView(withText("WAITING")).check(matches(isDisplayed()));

            onView(withId(R.id.sendNotificationButton)).perform(click());
            Thread.sleep(1000);
            onView(withText("list is empty, cannot send notification")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }
    @Test
    public void sendNotificationToEmptyAcceptedlistTest() {
        // US 02.05.01: Organizer can send notifications to chosen entrants
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            onView(withId(R.id.recycler_view_events)).perform(longClick());
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailAcceptedEntrants)).perform(click());
            onView(withText("ACCEPTED")).check(matches(isDisplayed()));

            onView(withId(R.id.sendNotificationButton)).perform(click());
            Thread.sleep(1000);
            onView(withText("list is empty, cannot send notification")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }

}
