package com.example.goldencarrot.views;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.user.UserImpl;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MapViewActivity extends AppCompatActivity {
    private MapView mapView;
    private UserRepository userRepository;
    private WaitListRepository waitListRepository;
    private String waitlistId; // Passed from OrganizerWaitlistView
    private static final String TAG = "MapViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view); // Use the map layout file

        // Initialize repositories
        userRepository = new UserRepository();
        waitListRepository = new WaitListRepository();

        // Initialize map
        mapView = findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Get waitlistId from intent
        waitlistId = getIntent().getStringExtra("waitlistId");

        // Fetch user locations and display them on the map
        fetchUserLocations();
    }

    private void fetchUserLocations() {
        waitListRepository.getUsersWithStatus(waitlistId, "waiting", new WaitListRepository.FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                List<String> userIds = (List<String>) result;
                for (String userId : userIds) {
                    userRepository.getSingleUser(userId, new UserRepository.FirestoreCallbackSingleUser() {
                        @Override
                        public void onSuccess(UserImpl user) {
                            if (user.getLatitude() != null && user.getLongitude() != null) {
                                // Add a pin for each user location
                                addPinToMap(user.getLatitude(), user.getLongitude(), user.getName());
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "Failed to fetch user location: " + e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to fetch waitlist users: " + e.getMessage());
            }
        });
    }

    private void addPinToMap(double latitude, double longitude, String userName) {
        GeoPoint point = new GeoPoint(latitude, longitude);
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(userName);
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }
}
