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

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminUiTest {
    private TestDataHelper testDataHelper;

    @Rule
    public ActivityScenarioRule<AdminHomeActivity> scenario = new ActivityScenarioRule<>(AdminHomeActivity.class);

    @After
    public void cleanup() throws Exception {
        if (testDataHelper != null) {
            testDataHelper.deleteData();
            Thread.sleep(5000); // Optional delay for cleanup
        }
    }


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
    public void testViewProfile() throws Exception {
        testDataHelper = new TestDataHelper();

        Thread.sleep(5000);

        // Click on Users button
        onView(withId(R.id.adminAllUsersButton)).perform(click());
        // click on user "SpiderMan" from the list of all users
        onView(withText(TestDataHelper.TEST_USER_NAME)).perform(click());
        // check if delete button is displayed on screen
        onView(withId(R.id.deleteProfileBtn)).check(matches(isDisplayed()));
    }

    @Test
    public void testBrowseUsers() throws Exception {
        // initialize test data Factory method
        testDataHelper = new TestDataHelper();

        Thread.sleep(5000);
        // Click on Users button
        onView(withId(R.id.adminAllUsersButton)).perform(click());
        // click on user "SpiderMan" from the list of all users
        onView(withText(TestDataHelper.TEST_USER_NAME)).perform(click());
        // Click on back button on profile page
        onView(withId(R.id.adminViewProfileBackBtn)).perform(click());
        // Click on back button on all profiles page
        onView(withId(R.id.admin_all_users_back_btn)).perform(click());
        // check if we're back on Admin Home
        onView(withText("Admin Home")).check(matches(isDisplayed()));
    }

    @Test
    public void testBrowseEvents() {
        // Click on Events button
        onView(withId(R.id.adminAllEventsButton)).perform(click());
        // Check if text "Browse Events" is matched with any text opn screen
        onView(withText("Browse Events")).check(matches(isDisplayed()));
    }
}
