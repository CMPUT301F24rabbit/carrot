package com.example.goldencarrot;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.ListView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.assertion.ViewAssertions;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.goldencarrot.views.EntrantNotificationsActivity;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class EntrantNotificationsActivityTest {
    TestDataHelper testDataHelper;
    
    String deviceId;

    @Rule
    public ActivityTestRule<EntrantNotificationsActivity> activityRule =
            new ActivityTestRule<>(EntrantNotificationsActivity.class);

    @Before
    public void setUp() throws Exception {
        testDataHelper = new TestDataHelper();
        // Launch the EntrantNotificationsActivity
        deviceId = getDeviceId(ApplicationProvider.getApplicationContext());
        
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EntrantNotificationsActivity.class);
        activityRule.launchActivity(intent);
    }

    // Tests that a user can get notifications when in a waitlist
    @Test
    public void clickOnSingleUserNotificationTest() throws InterruptedException {

        Random random = new Random();
        int randomNumber = random.nextInt(9999) + 1; // Generates a number between 1 and 9999
        String message = String.valueOf(randomNumber);
        testDataHelper.createSingleUserNotification(deviceId, String.valueOf(randomNumber));

        Thread.sleep(5000);
        onView(withText(String.valueOf(randomNumber))).perform(ViewActions.click());
    }

    // Test Case: Entrant navigates back to the home view
    @Test
    public void testBackButtonNavigatesToHome() {
        // Simulate pressing the back button in the notification view
        onView(withId(R.id.back_button_notifications)).perform(ViewActions.click());
    }

    @After
    public void cleanUp() throws InterruptedException {
        Thread.sleep(5000);

        testDataHelper.deleteData();
        Thread.sleep(5000);
    }


    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}

