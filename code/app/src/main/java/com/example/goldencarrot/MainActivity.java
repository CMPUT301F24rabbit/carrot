package com.example.goldencarrot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.example.goldencarrot.views.AdminHomeActivity;
import com.example.goldencarrot.views.EntrantHomeView;
import com.example.goldencarrot.views.OrganizerHomeView;
import com.example.goldencarrot.views.SelectUserTypeActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        UserRepository userRepository = new UserRepository();
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        userRepository.getUserTypeFromFirestore(new UserRepository.FirestoreCallback() {
            @Override
            public void onSuccess(String userType) {
                // Handle the retrieved user type
                navigateByUserType(userType);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle any errors
            }
        });

        //  Login Button
        findViewById(R.id.auth_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        // Sign-Up Button
        findViewById(R.id.auth_sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelectUserTypeActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void navigateByUserType(String userType) {
        Intent intent;
        if (userType.equals(UserUtils.ADMIN_TYPE)) {
            intent = new Intent(MainActivity.this, AdminHomeActivity.class);
            startActivity(intent);
        }

        if (userType.equals(UserUtils.ORGANIZER_TYPE)) {
            intent = new Intent(MainActivity.this, OrganizerHomeView.class);
            startActivity(intent);
        }

        if (userType.equals(UserUtils.PARTICIPANT_TYPE)) {
            intent = new Intent(MainActivity.this, EntrantHomeView.class);
            startActivity(intent);
        }
    }
}
