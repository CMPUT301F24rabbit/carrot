package com.example.goldencarrot;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.goldencarrot.views.EntrantEditUserDetailsView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
public class EntrantEditUserDetailsViewTest {

    @Rule
    public ActivityScenarioRule<EntrantEditUserDetailsView> activityRule = new ActivityScenarioRule<>(EntrantEditUserDetailsView.class);

    @Test
    public void testInvalidEmailFormat() {
        onView(withId(R.id.edit_user_details_name)).perform(replaceText("John Doe"));
        onView(withId(R.id.edit_user_details_email_input)).perform(replaceText("invalid-email"));
        onView(withId(R.id.edit_user_details_phone_number)).perform(replaceText("1234567890"));
        onView(withId(R.id.edit_user_details_save_button)).perform(click());

        onView(withText("Invalid email format")).check(matches(isDisplayed()));
    }

    @Test
    public void testInvalidPhoneNumber() {
        onView(withId(R.id.edit_user_details_name)).perform(replaceText("John Doe"));
        onView(withId(R.id.edit_user_details_email_input)).perform(replaceText("test@example.com"));
        onView(withId(R.id.edit_user_details_phone_number)).perform(replaceText("12345")); // Invalid phone number
        onView(withId(R.id.edit_user_details_save_button)).perform(click());

        onView(withText("Phone number must contain exactly 10 digits")).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyNameField() {
        onView(withId(R.id.edit_user_details_name)).perform(replaceText(""));
        onView(withId(R.id.edit_user_details_email_input)).perform(replaceText("test@example.com"));
        onView(withId(R.id.edit_user_details_phone_number)).perform(replaceText("1234567890"));
        onView(withId(R.id.edit_user_details_save_button)).perform(click());

        onView(withText("Name cannot be empty")).check(matches(isDisplayed()));
    }

    @Test
    public void testValidInputNavigatesToEntrantHomeView() {
        // Enter valid data
        onView(withId(R.id.edit_user_details_name)).perform(replaceText("John Doe"));
        onView(withId(R.id.edit_user_details_email_input)).perform(replaceText("test@example.com"));
        onView(withId(R.id.edit_user_details_phone_number)).perform(replaceText("1234567890"));

        // Click the save button
        onView(withId(R.id.edit_user_details_save_button)).perform(click());

        onView(withId(R.id.entrant_home_view_user_name)).check(matches(isDisplayed()));
    }

}
