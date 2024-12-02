package com.example.goldencarrot;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.goldencarrot.views.EntrantNotificationsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class EntrantNotificationsActivityTest {
    TestDataHelper testDataHelper;

    String deviceId;
    int randomNumber;

    @Rule
    public ActivityTestRule<EntrantNotificationsActivity> activityRule =
            new ActivityTestRule<>(EntrantNotificationsActivity.class);

    @Before
    public void setUp() throws Exception {
        testDataHelper = new TestDataHelper();
        // Launch the EntrantNotificationsActivity
        deviceId = getDeviceId(ApplicationProvider.getApplicationContext());

        Random random = new Random();
        randomNumber = random.nextInt(9999) + 1; // Generates a number between 1 and 9999
        testDataHelper.createSingleUserNotification(deviceId, String.valueOf(randomNumber));

        Thread.sleep(5000);

        activityRule.getActivity().runOnUiThread(() -> activityRule.getActivity().recreate());
    }

    @Test
    public void clickOnSingleUserNotificationTest() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EntrantNotificationsActivity.class);
        activityRule.launchActivity(intent);

        // Verify the notification is displayed and click on it
        onView(withText(String.valueOf(randomNumber))).perform(ViewActions.click());
    }

    @Test
    public void testBackButtonNavigatesToHome() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EntrantNotificationsActivity.class);
        activityRule.launchActivity(intent);

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
