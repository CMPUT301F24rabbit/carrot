package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.model.user.UserUtils;
/**
 * This activity allows the user to select their user type (Organizer or Participant) during the sign-up process.
 * After selecting the user type, the user is redirected to the sign-up screen with the selected type passed as an extra.
 */
public class SelectUserTypeActivity extends AppCompatActivity {

    // Holds the selected user type
    String userType = null;

    /**
     * Called when the activity is created. Sets up the user interface elements and their click listeners.
     * The user can select between "Organizer" or "Participant" and proceed to the next screen.
     *
     * @param savedInstanceState The saved instance state if the activity is being re-initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_sign_up_select_user_type);

        // Organizer Button click listener
        findViewById(R.id.select_user_type_organizer_button).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userType = UserUtils.ORGANIZER_TYPE;
                    }
                });

        // Participant Button click listener
        findViewById(R.id.select_user_type_participant_button).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userType = UserUtils.PARTICIPANT_TYPE;
                    }
                });

        // Next Button click listener
        findViewById(R.id.select_user_type_next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Proceed to the next activity if a user type is selected
                if (userType != null) {
                    Intent intent = new Intent(SelectUserTypeActivity.this,
                            SignUpActivity.class);
                    intent.putExtra(UserUtils.USER_TYPE, userType);
                    startActivity(intent);
                }
            }
        });
    }
}
