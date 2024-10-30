package com.example.goldencarrot;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.goldencarrot.views.AdminHomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminUITest {
    @Rule
    public ActivityScenarioRule<AdminHomeActivity> scenario = new ActivityScenarioRule<>(AdminHomeActivity.class);
    /**
     * testViewAllUsers() will test if the AdminAllUsersView Activity will open upon
     * clicking the "Users" button
     */
    @Test
    public void testViewAllUsers() {
        // Click on Users button
        onView(withId(R.id.adminAllUsersButton)).perform(click());
        // Check if text "Profiles" is matched with any text on screen
        // meaning we are on the page with all profiles
        onView(withText("Profiles")).check(matches(isDisplayed()));
    }
    @Test
    public void testViewProfile() {
        // Click on Users button
        onView(withId(R.id.adminAllUsersButton)).perform(click());
        // click on user "James" from the list of all users
        onView(withText("James")).perform(click());
        // check if phone number "7800000000" is displayed on screen
        onView(withText("7800000000")).check(matches(isDisplayed()));
    }
    @Test
    public void testBackButtonFromProfiles() {
        // Click on Users button
        onView(withId(R.id.adminAllUsersButton)).perform(click());
        // click on user "James" from the list of all users
        onView(withText("James")).perform(click());
        // Click on back button on profile page
        onView(withId(R.id.adminViewProfileBackBtn)).perform(click());
        // Click on back button on all profiles page
        onView(withId(R.id.admin_all_users_back_btn)).perform(click());
        // check if we're back on Admin Home
        onView(withText("Admin Home")).check(matches(isDisplayed()));

    }
}
