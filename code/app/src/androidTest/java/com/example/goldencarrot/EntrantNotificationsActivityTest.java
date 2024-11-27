package com.example.goldencarrot;
import android.content.Intent;
import android.widget.ListView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.assertion.ViewAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.goldencarrot.views.EntrantNotificationsActivity;

@RunWith(AndroidJUnit4.class)
public class EntrantNotificationsActivityTest {

    @Rule
    public ActivityTestRule<EntrantNotificationsActivity> activityRule =
            new ActivityTestRule<>(EntrantNotificationsActivity.class);

    @Before
    public void setUp() {
        // Launch the EntrantNotificationsActivity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EntrantNotificationsActivity.class);
        activityRule.launchActivity(intent);
    }

    // Test Case: Entrant opts out of receiving notifications
    @Test
    public void testOptOutNotifications() {
        onView(withId(R.id.notifications_button)).perform(ViewActions.click());
        onView(withText("Are you sure you want to opt out?")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(withText("Yes")).perform(ViewActions.click());

        // Verify that the UI updates with a confirmation message
        onView(withText("You have opted out of notifications"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    // Test Case: Entrant accepts the notification
    @Test
    public void testAcceptNotification() {
        onView(withText("You were chosen for the event")).perform(ViewActions.click());

        // Simulate clicking the "ACCEPT" button in the dialog
        onView(withText("ACCEPT")).perform(ViewActions.click());

        // Verify that the notification is deleted and the UI updates accordingly
        onView(withText("You accepted the notification"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    // Test Case: Entrant navigates back to the home view
    @Test
    public void testBackButtonNavigatesToHome() {
        // Simulate pressing the back button in the notification view
        onView(withId(R.id.back_button_notifications)).perform(ViewActions.click());
    }
}

