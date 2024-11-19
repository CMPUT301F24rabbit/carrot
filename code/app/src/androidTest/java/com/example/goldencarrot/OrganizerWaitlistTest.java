package com.example.goldencarrot;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.goldencarrot.views.OrganizerHomeView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrganizerWaitlistTest {
    @Rule
    public ActivityScenarioRule<OrganizerHomeView> activityRule =
            new ActivityScenarioRule<>(OrganizerHomeView.class);

    @Test
    public void testShowWaitingList() {
        // US 02.02.01: Organizer can view list of entrants who joined waiting list
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            onView(withId(R.id.recycler_view_events)).perform(longClick());
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailWaitlistedEntrants)).perform(click());
            onView(withText("WAITING")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }
    @Test
    public void testShowAcceptedList() {
        // US 02.06.01: Organizer can view list of chosen entrants
        // by default they will be added to "Accepted", and moved to "Declined" if they decline
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            onView(withId(R.id.recycler_view_events)).perform(longClick());
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailChosenEntrants)).perform(click());
            onView(withText("ACCEPTED")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }
    @Test
    public void testShowDeclinedList() {
        // US 02.06.02: Organizer can view list of canceled entrants (or declined)
        try (ActivityScenario<OrganizerHomeView> scenario = ActivityScenario.launch(OrganizerHomeView.class)) {
            Thread.sleep(3000);
            onView(withId(R.id.recycler_view_events)).perform(longClick());
            onView(withId(R.id.button_DetailViewEventLists)).perform(click());
            onView(withId(R.id.button_EventDetailRejectedEntrants)).perform(click());
            onView(withText("DECLINED")).check(matches(isDisplayed()));
        } catch (Exception e) {

        }
    }
}
