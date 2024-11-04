package com.example.goldencarrot;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.example.goldencarrot.views.EntrantHomeView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.not;

/**
 * EntrantHomeViewTest will test if the EntrantHomeView will open up all its navagatability
 * by clicking all the buttons
 */
@RunWith(AndroidJUnit4.class)
public class EntrantHomeViewTest {
    @Rule
    // Make a new scenario to test in
    public ActivityScenarioRule<EntrantHomeView> activityScenarioRule = new ActivityScenarioRule<>(EntrantHomeView.class);

    /**
     * testUserNameDisplayed will verify if the username TextView is displayed
     * with the default text
     */
    @Test
    public void testUserNameDisplayed() {
        onView(withId(R.id.entrant_home_view_user_name))
                .check(matches(isDisplayed()));
    }

    /**
     * testNavigateToEditProfile will verify that we are able to go from EntrantHomeView
     * to EditUserDetailsView by long clicking on the profile image
     */
    @Test
    public void testNavigateToEditProfile() {
        onView(withId(R.id.entrant_home_view_image_view))
                .perform(longClick());

        onView(withId(R.id.edit_user_details_save_button))
                .check(matches(isDisplayed()));
    }

    /**
     * testExploreEventsButtonClick verifys that the button will take us to the
     * list of events by seeing if browseEventsBackBtn is there
     */
    @Test
    public void testExploreEventsButtonClick() {
        onView(withId(R.id.button_explore_events))
                .perform(click());

        onView(withId(R.id.browseEventsBackBtn))
                .check(matches(isDisplayed()));
    }

    /**
     * testWaitlistTitleClick verifies we move to the WaitlistActivity when we click on it
     */
    @Test
    public void testWaitlistTitleClick() {
        onView(withId(R.id.waitlisted_events_title))
                .perform(click());

        onView(withId(R.id.button_back_to_previous_activity))
                .check(matches(isDisplayed()));
    }

    /**
     * testUserDataLoading verifies whether theusername TextView gets updated after loading
     */
    @Test
    public void testUserDataLoading() {
        onView(withId(R.id.entrant_home_view_user_name))
                .check(matches(not(withText("Error: Username not found"))));
    }
}
