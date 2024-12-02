package com.example.goldencarrot.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Activity that allows the user to scan a QR code and navigate to the details of an event.
 * The QR code is expected to contain a URL with an event ID. Upon successful scan,
 * the event details screen is displayed using the extracted event ID.
 */
public class QrCodeScannerActivity extends AppCompatActivity {

    /**
     * Called when the activity is created. Starts the QR scanner to allow the user
     * to scan a QR code.
     *
     * @param savedInstanceState the saved instance state for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startQrScanner();
    }

    /**
     * Starts the QR scanner using the {@link IntentIntegrator} to initiate a scan.
     */
    private void startQrScanner() {
        new IntentIntegrator(this).initiateScan();
    }

    /**
     * Handles the result of the QR scan. If the scanned content is a valid URL that
     * starts with "golden carrot://eventDetails", the event ID is extracted and used
     * to navigate to the {@link EntrantEventDetailsActivity}.
     *
     * If the QR code does not contain a valid event ID, a toast message is shown.
     *
     * @param requestCode the request code associated with the activity result
     * @param resultCode the result code of the activity
     * @param data the data returned from the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Parse the scan result
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String scannedContent = result.getContents();
            if (scannedContent != null && scannedContent.startsWith("golden carrot://eventDetails")) {
                // Extract the eventId from the QR code URL
                Uri uri = Uri.parse(scannedContent);
                String eventId = uri.getQueryParameter("eventId");

                // If the eventId is extracted successfully, navigate to EntrantEventDetailsActivity
                if (eventId != null) {
                    Intent intent = new Intent(this, EntrantEventDetailsActivity.class);
                    intent.putExtra("eventId", eventId); // Pass the eventId to the next activity
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No valid event ID found in the QR code", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No valid event ID found in the QR code", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
