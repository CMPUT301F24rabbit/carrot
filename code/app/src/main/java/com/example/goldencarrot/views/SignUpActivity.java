package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
        accountService = new AccountServiceImpl(SignUpActivity.this);  // Pass context to AccountServiceImpl
        userDb = new UserRepository();  // Initialize UserRepository

        userType = UserUtils.PARTICIPANT_TYPE;

        // Attempts to create account
        findViewById(R.id.sign_up_create_account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.sign_up_email_input);
                EditText phoneNumber = findViewById(R.id.sign_up_phone_number);
                EditText name = findViewById(R.id.sign_up_name);


                // Pass the correct context using SignUpActivity.this
                String deviceId = Settings.Secure.getString(
                        SignUpActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                addUserToFirestore(deviceId,
                        name.getText().toString(),
                        email.getText().toString(),
                        Optional.of(
                                phoneNumber.getText().toString()
                        ));
                Intent intent = new Intent(SignUpActivity.this, EntrantHomeView.class);
                startActivity(intent);
            }
        });
    }

    private void addUserToFirestore(String deviceId, String name, String email, Optional<String> phoneNumer) {

        // Create UserImpl object with the user email and userType
        try {
            User newUser = new UserImpl(email, userType, name, phoneNumer);
            // Add user to Firestore using UserRepository
            userDb.addUser(newUser, deviceId);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SignUpActivity.this, "Error: Invalid user type", Toast.LENGTH_SHORT).show();
        }

    }
}
