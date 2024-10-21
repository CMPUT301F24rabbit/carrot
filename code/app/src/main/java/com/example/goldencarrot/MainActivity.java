package com.example.goldencarrot;

import static android.content.ContentValues.TAG;

import static com.example.goldencarrot.data.model.user.UserUtils.ADMIN_TYPE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goldencarrot.authentication.AccountService;
import com.example.goldencarrot.authentication.AccountServiceImpl;
import com.example.goldencarrot.views.AdminHomeActivity;
import com.example.goldencarrot.views.SelectUserTypeActivity;
import com.example.goldencarrot.views.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference userCollection;
    private ArrayList<String> currentUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // save collection of users
        userCollection = db.collection("users");
        currentUserData = new ArrayList<>();
        /** Todo
         *  send user to their user type welcome page if logged in
         */
        if (mAuth.getCurrentUser() != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            getCurrentUserData(currentUser);
            navigateByUserType(currentUserData.get(1));
        }

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
    public void getCurrentUserData(FirebaseUser currentUser) {
        DocumentReference userDocRef = userCollection.document(currentUser.getUid());
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        currentUserData.add((String) document.getData().get("email"));
                        currentUserData.add((String) document.getData().get("userType"));
                        currentUserData.add((String) document.getData().get("username"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void navigateByUserType(String userType) {
        Intent intent;
        if (userType.equals(ADMIN_TYPE)) {
            intent = new Intent(MainActivity.this, AdminHomeActivity.class);
            startActivity(intent);
        }
        /**
         * TODO setup organizer and entrant activities and navigate to them
         */
    }
}