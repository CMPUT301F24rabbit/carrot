package com.example.goldencarrot;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.Espresso;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.views.OrganizerCreateEvent;
import com.google.firebase.storage.internal.Sleeper;

@RunWith(AndroidJUnit4.class)
public class OrganizerEventTest {
    private EventRepository eventRepository;
    private static final String TAG = "OrganizerEventTest";
    private static final String EVENT_NAME = "SPIDERMAN PARTY";

    @Rule
    public ActivityScenarioRule<OrganizerCreateEvent> activityRule =
            new ActivityScenarioRule<>(OrganizerCreateEvent.class);

    @Before
    public void setUp(){
        eventRepository = new EventRepository();
    }

    @Test
    public void testEventCreation() throws InterruptedException{
        // Launch activity using the scenario
        try (ActivityScenario<OrganizerCreateEvent> scenario = ActivityScenario.launch(OrganizerCreateEvent.class)) {

            // Input event details
            onView(withId(R.id.eventNameEditText)).perform(typeText(EVENT_NAME), closeSoftKeyboard());
            onView(withId(R.id.eventLocationEditText)).perform(typeText("New York"), closeSoftKeyboard());
            onView(withId(R.id.eventDetailsEditText)).perform(typeText("This is a sample event."), closeSoftKeyboard());
            onView(withId(R.id.eventDateEditText)).perform(typeText("31-12-2024"), closeSoftKeyboard());

            // Input waitlist limit and toggle geolocation switch
            onView(withId(R.id.waitlistLimitEditText)).perform(typeText("10"), closeSoftKeyboard());

            // SET GEOLOCATION ENABLED
            onView(withId(R.id.geolocation)).perform(click());

            // Click on the "Create Event" button
            onView(withId(R.id.createEventButton)).perform(click());

            Thread.sleep(4000);

            onView(withText("New Event")).check(matches(isDisplayed()));

            // Verify if the success message or a related UI component is displayed
            //onView(withText("Event created successfully")).check(matches(isDisplayed()));
        }
    }
    
    @Test
    public void testEventCreation_NoLimit() throws InterruptedException{
        // Launch activity using the scenario
        try (ActivityScenario<OrganizerCreateEvent> scenario = ActivityScenario.launch(OrganizerCreateEvent.class)) {

            // Input event details
            onView(withId(R.id.eventNameEditText)).perform(typeText(EVENT_NAME), closeSoftKeyboard());
            onView(withId(R.id.eventLocationEditText)).perform(typeText("New York"), closeSoftKeyboard());
            onView(withId(R.id.eventDetailsEditText)).perform(typeText("This is a sample event."), closeSoftKeyboard());
            onView(withId(R.id.eventDateEditText)).perform(typeText("31-12-2024"), closeSoftKeyboard());

            // SET GEOLOCATION ENABLED
            onView(withId(R.id.geolocation)).perform(click());

            // Click on the "Create Event" button
            onView(withId(R.id.createEventButton)).perform(click());

            Thread.sleep(4000);

            onView(withText("New Event")).check(matches(isDisplayed()));

            // Verify if the success message or a related UI component is displayed
            //onView(withText("Event created successfully")).check(matches(isDisplayed()));
        }
    }

    @After
    public void cleanUpEvents() {
        eventRepository.deleteEventByName(EVENT_NAME, new EventRepository.DeleteCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "message");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "clean up failed, event was not deleted successfully");
            }
        });
    }
}

  /*  @Test
    public void testPosterImageSelection() {
        Intent resultData = new Intent();
        Uri imageUri = Uri.parse("android.resource://com.example.goldencarrot/drawable/poster_placeholder");
        resultData.setData(imageUri);

        // Set up a result stub for the intent
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Intending to open the image picker and respond with the mock result
        Intents.init();
        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);

        // Perform the click action on the poster button
        onView(withId(R.id.selectPosterButton)).perform(click());
        onView(withId(R.id.eventPosterImageView))
                .check(matches(isDisplayed()));

        Intents.release();
    }}

*/