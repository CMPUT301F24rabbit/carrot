package com.example.goldencarrot.views;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.user.UserImpl;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

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

        // Configure osmdroid with a user agent
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue("com.example.goldencarrot");

        setContentView(R.layout.activity_map_view); // Use the map layout file

        // Initialize repositories
        userRepository = new UserRepository();
        waitListRepository = new WaitListRepository();

        // Initialize map
        mapView = findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(10.0); // Initial zoom level
        mapView.getController().setCenter(new GeoPoint(37.7749, -122.4194)); // Default center: San Francisco, CA

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
                                Log.d(TAG, "Adding marker for user: " + user.getName() + " at (" + user.getLatitude() + ", " + user.getLongitude() + ")");
                                // Add a pin for each user location
                                addPinToMap(user.getLatitude(), user.getLongitude(), user.getName());
                            } else {
                                Log.e(TAG, "Latitude or Longitude is null for user: " + user.getName());
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
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); // Proper alignment
        mapView.getOverlays().add(marker);
        mapView.invalidate(); // Redraw the map
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure the osmdroid configuration is reloaded
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save osmdroid configuration when the activity pauses
        Configuration.getInstance().save(this, PreferenceManager.getDefaultSharedPreferences(this));
    }
}
