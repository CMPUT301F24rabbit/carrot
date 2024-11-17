package com.example.goldencarrot;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.example.goldencarrot.views.AdminHomeActivity;
import com.example.goldencarrot.views.EntrantHomeView;
import com.example.goldencarrot.views.OrganizerHomeView;
import com.example.goldencarrot.views.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * MainActivity is the landing page for users when they open the app
 * this acitivity sends the User to a sign up page if not logged in
 * otherwise it sends the user to their respective home page
 */
public class MainActivity extends AppCompatActivity {

    private String deviceId;
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        UserRepository userRepository = new UserRepository();
        FirebaseApp.initializeApp(this);

        // Testing push notifications
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        //String msg = getString(token);
                        Log.d(TAG, token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        deviceId = getDeviceId(this);

        // Check if the user exists in Firestore using the device ID and get the userType
        // if the user is logged in it gets sent to the respective usertype homeview else
        // the user gets sent to the SignUp page
        userRepository.checkUserExistsAndGetUserType(deviceId, new UserRepository.UserTypeCallback() {
            @Override
            public void onResult(boolean exists, String userType) {
                if (exists) {
                    // User exists, and we have the userType
                    Log.d("MainActivity", "User exists. User Type: " + userType);
                    navigateByUserType(userType);
                } else {
                    // User does not exist
                    Log.d("MainActivity", "User does not exist in Firestore.");
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            }
        });

        /**
         * Todo, add a loading circle bar
         */
        findViewById(R.id.auth_sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * @param userType sends each user to their respective home view
     */
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

    /**
     * Gets the Android device id used to authenticate on device
     * @param context provided by the View
     * @return the android id
     */
    private String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
