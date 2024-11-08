package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.authentication.AccountService;
import com.example.goldencarrot.authentication.AccountServiceImpl;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Optional;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private AccountService accountService;
    private String userType;
    private UserRepository userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_sign_up);

        mAuth = FirebaseAuth.getInstance();
        accountService = new AccountServiceImpl(SignUpActivity.this);
        userDb = new UserRepository();

        // Default participant type
        userType = UserUtils.PARTICIPANT_TYPE;

        findViewById(R.id.sign_up_create_account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.sign_up_email_input);
                EditText phoneNumber = findViewById(R.id.sign_up_phone_number);
                EditText name = findViewById(R.id.sign_up_name);
                Boolean nAdmin = true;
                Boolean nOrg = true;

                String deviceId = Settings.Secure.getString(
                        SignUpActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                try {
                    verifyInputs(
                            email.getText().toString(),
                            phoneNumber.getText().toString(),
                            name.getText().toString()
                    );

                    addUserToFirestore(deviceId, name.getText().toString(), email.getText().toString(), Optional.of(phoneNumber.getText().toString()), nAdmin, nOrg);
                    Intent intent = new Intent(SignUpActivity.this, EntrantHomeView.class);
                    startActivity(intent);

                } catch (Exception e) {
                    ValidationErrorDialog.show(SignUpActivity.this, "Validation Error", e.getMessage());
                }
            }
        });
    }

    /**
     * Todo add this logic to a UserController
     *
     */

    private void addUserToFirestore(String deviceId, String name, String email, Optional<String> phoneNumber, Boolean nAdmin, Boolean nOrg) {
        try {
            User newUser = new UserImpl(email, userType, name, phoneNumber, nAdmin, nOrg);
            userDb.addUser(newUser, deviceId);
        } catch (Exception e) {
            e.printStackTrace();
            ValidationErrorDialog.show(SignUpActivity.this, "Error", "Invalid user type");
        }
    }

    private void verifyInputs(final String email, final String phoneNumber, final String name) throws Exception {
        if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
            throw new Exception("Invalid email format");
        }

        if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.matches("\\d{10}")) {
            throw new Exception("Phone number must contain exactly 10 digits");
        }

        if (TextUtils.isEmpty(name)) {
            throw new Exception("Name cannot be empty");
        }
    }

    // Reference:
    // "android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();" – Pratik Butani,
    // Stack Overflow, Dec 16, 2013
    // Retrieved from https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
