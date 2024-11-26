package com.example.goldencarrot.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrCodeScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startQrScanner();
    }

    // Start the QR scanner
    private void startQrScanner() {
        new IntentIntegrator(this).initiateScan();
    }

    // Handle the result of the QR scan
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
