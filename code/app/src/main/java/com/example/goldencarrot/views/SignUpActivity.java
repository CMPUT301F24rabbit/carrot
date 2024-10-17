package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.authentication.AccountService;
import com.example.goldencarrot.authentication.AccountServiceImpl;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        accountService = new AccountServiceImpl(SignUpActivity.this);  // Pass context to AccountServiceImpl
        userDb = new UserRepository();  // Initialize UserRepository

        Intent data = getIntent();
        userType = data.getStringExtra(UserUtils.USER_TYPE);

        // Attempts to create account
        findViewById(R.id.sign_up_create_account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.sign_up_email_input);
                EditText password = findViewById(R.id.sign_up_password_input);
                EditText username = findViewById(R.id.sign_up_username);

                // Pass the correct context using SignUpActivity.this
                accountService.signUp(email.getText().toString(),
                        password.getText().toString(),
                        new AccountService.AuthCallback() {
                            @Override
                            public void onAuthSuccess(FirebaseUser user) {
                                // After successful sign up, add the user to Firestore
                                addUserToFirestore(user, username.toString());
                                updateUI(user);
                            }

                            @Override
                            public void onAuthFailure() {
                                updateUI(null);
                            }
                        });
            }
        });

        if (mAuth.getCurrentUser() != null) {
            TextView welcomeTextView = findViewById(R.id.auth_welcome_message);
            welcomeTextView.setText(mAuth.getCurrentUser().getEmail());
        }
    }

    private void addUserToFirestore(FirebaseUser firebaseUser, String username) {
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            String email = firebaseUser.getEmail();

            // Create UserImpl object with the user email and userType
            try {
                User newUser = new UserImpl(email, userType, username);
                // Add user to Firestore using UserRepository
                userDb.addUser(newUser, uid);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SignUpActivity.this, "Error: Invalid user type", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // The user is signed in
            String userEmail = user.getEmail();
            Toast.makeText(SignUpActivity.this, "Logged in as: " + userEmail, Toast.LENGTH_LONG).show();
        } else {
            // The user is signed out or something went wrong
            Toast.makeText(SignUpActivity.this, "Not logged in", Toast.LENGTH_LONG).show();
        }
    }
}
