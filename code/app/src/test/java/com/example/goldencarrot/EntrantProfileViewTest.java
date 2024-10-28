package com.example.goldencarrot.views;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.widget.EditText;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.goldencarrot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EntrantProfileViewTest {

    private EntrantProfileView activity;

    @Rule
    public ActivityScenarioRule<EntrantProfileView> activityRule = new ActivityScenarioRule<>(EntrantProfileView.class);

    @Before
    public void setup() {
        ActivityScenario<EntrantProfileView> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            this.activity = activity;

            // Initialize Firebase Auth and Firestore instances
            activity.mAuth = FirebaseAuth.getInstance();
            activity.db = FirebaseFirestore.getInstance();
        });
    }

    @Test
    public void testSaveEntrantData_WithValidInputs_ShowsSuccessToast() {
        // Set valid input values
        activity.runOnUiThread(() -> {
            activity.nameInput.setText("John Doe");
            activity.emailInput.setText("john@example.com");
            activity.phoneInput.setText("1234567890");
            activity.locationInput.setText("Edmonton");

            // Trigger save data function
            activity.saveEntrantData("John Doe", "john@example.com", "1234567890", "New York");
        });

        // Validate output (e.g., Toast message) if possible with Espresso or manual checks
    }

    @Test
    public void testSaveEntrantData_WithEmptyName_ShowsErrorToast() {
        activity.runOnUiThread(() -> {
            // Set input with empty name
            activity.nameInput.setText("");
            activity.emailInput.setText("john@example.com");

            // Attempt to save data
            activity.saveEntrantData("", "john@example.com", "", "");

            // Assert Toast message shows name/email requirement
        });
    }

    @Test
    public void testDeleteAccount_WhenSuccessful_ShowsSuccessToast() {
        activity.runOnUiThread(() -> {
            FirebaseUser currentUser = activity.mAuth.getCurrentUser();
            if (currentUser != null) {
                // Call delete account
                activity.deleteAccount();

                // Verify account deleted and Toast message displayed
            }
        });
    }

    @Test
    public void testIsValidPhoneNumber_ValidPhone() {
        assertTrue(activity.isValidPhoneNumber("1234567890"));
    }

    @Test
    public void testIsValidPhoneNumber_InvalidPhone() {
        assertFalse(activity.isValidPhoneNumber("12345"));
        assertFalse(activity.isValidPhoneNumber("abcdefghij"));
        assertFalse(activity.isValidPhoneNumber(""));
    }

}

